package com.techzo.cambiazo.data.remote.chat

import com.google.gson.Gson
import com.techzo.cambiazo.common.Constants.WS_URL
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class ChatService {

    private val stompClient: StompClient =
        Stomp.over(Stomp.ConnectionProvider.OKHTTP, WS_URL).apply {
            withClientHeartbeat(10_000)
            withServerHeartbeat(10_000)
        }

    private val gson = Gson()

    private val lifecycleSubscribed = AtomicBoolean(false)
    private val isConnected = AtomicBoolean(false)
    private val isConnecting = AtomicBoolean(false)

    private val lifecycleDisposables = CompositeDisposable()
    private val topicDisposables = CompositeDisposable()

    private val activeTopics = ConcurrentHashMap.newKeySet<String>()
    private val pendingTopics = ConcurrentLinkedQueue<() -> Unit>()

    @Volatile private var chatConsumer: ((ServerChatDto) -> Unit)? = null
    @Volatile private var reconnectAttempts = 0

    fun setChatConsumer(c: (ServerChatDto) -> Unit) {
        chatConsumer = c
    }

    fun connectIfNeeded() {
        if (isConnected.get() || isConnecting.get()) return
        isConnecting.set(true)

        if (lifecycleSubscribed.compareAndSet(false, true)) {
            val lifeDisp = stompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ event ->
                    when (event.type) {
                        LifecycleEvent.Type.OPENED -> {
                            reconnectAttempts = 0
                            isConnecting.set(false)
                            isConnected.set(true)
                            flushPending()
                        }
                        LifecycleEvent.Type.CLOSED,
                        LifecycleEvent.Type.ERROR -> {
                            isConnected.set(false)
                            isConnecting.set(false)
                            activeTopics.clear()
                            topicDisposables.clear()
                            scheduleReconnect()
                        }
                        else -> Unit
                    }
                }, {
                    isConnected.set(false)
                    isConnecting.set(false)
                    scheduleReconnect()
                })
            lifecycleDisposables.add(lifeDisp)
        }

        stompClient.connect()
    }

    fun subscribeInbox(userId: String, onActive: (ActiveConversationDto) -> Unit) {
        val topic = "/topic/inbox.$userId"
        enqueueOrRun {
            if (!activeTopics.add(topic)) return@enqueueOrRun
            val d = stompClient.topic(topic)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ msg ->
                    val dto = gson.fromJson(msg.payload, ActiveConversationDto::class.java)
                    onActive(dto)
                }, {
                    activeTopics.remove(topic)
                })
            topicDisposables.add(d)
        }
    }

    fun subscribeConversation(conversationId: String) {
        val topic = "/topic/chat.$conversationId"
        enqueueOrRun {
            if (!activeTopics.add(topic)) return@enqueueOrRun
            val d = stompClient.topic(topic)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ msg ->
                    chatConsumer?.let {
                        val dto = gson.fromJson(msg.payload, ServerChatDto::class.java)
                        it(dto)
                    }
                }, {
                    activeTopics.remove(topic)
                })
            topicDisposables.add(d)
        }
    }

    private fun enqueueOrRun(block: () -> Unit) {
        connectIfNeeded()
        if (isConnected.get()) {
            block()
        } else {
            pendingTopics.add(block)
        }
    }

    private fun flushPending() {
        while (true) {
            val task = pendingTopics.poll() ?: break
            task()
        }
    }

    private fun scheduleReconnect() {
        val delayMs = 1000L * (1 shl reconnectAttempts.coerceAtMost(6)) // 1s,2s,4s,... hasta 64s
        reconnectAttempts++
        val d = Observable.timer(delayMs, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ connectIfNeeded() }, { /* ignore */ })
        lifecycleDisposables.add(d)
    }

    fun sendChatMessage(
        senderId: String,
        receiverId: String,
        conversationId: String?,
        exchangeId: String?,
        content: String?,
        type: ChatMessagePayload.MessageType = ChatMessagePayload.MessageType.TEXT,
        latitude: Double? = null,
        longitude: Double? = null,
        locationLabel: String? = null,
        onOk: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val dto = ChatMessagePayload(
            id = UUID.randomUUID().toString(),
            senderId = senderId,
            receiverId = receiverId,
            conversationId = conversationId,
            exchangeId = exchangeId,
            content = content,
            type = type,
            latitude = latitude,
            longitude = longitude,
            locationLabel = locationLabel,
            timestamp = DateTimeUtils.nowIsoUtcMillis()
        )
        sendPayload(dto, onOk, onError)
    }

    fun sendPayload(
        payload: ChatMessagePayload,
        onOk: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        enqueueOrRun {
            val d = stompClient
                .send("/app/chat.send", Gson().toJson(payload))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ onOk() }, { e -> onError(e) })
            topicDisposables.add(d)
        }
    }

    fun disconnectAll() {
        try {
            pendingTopics.clear()
            activeTopics.clear()
            topicDisposables.clear()
            lifecycleDisposables.clear()
            lifecycleSubscribed.set(false)
            isConnected.set(false)
            isConnecting.set(false)
            reconnectAttempts = 0
            stompClient.disconnect()
        } catch (_: Throwable) { }
    }
}