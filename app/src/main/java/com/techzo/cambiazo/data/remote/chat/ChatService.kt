package com.techzo.cambiazo.data.remote.chat

import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import com.google.gson.Gson
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Singleton

@Singleton
class ChatService {

    private val SOCKET_URL =
        "wss://cambiazo-techzo-gzdtcfcca4fxeaec.westus-01.azurewebsites.net/ws/websocket"

    private val stompClient: StompClient =
        Stomp.over(Stomp.ConnectionProvider.OKHTTP, SOCKET_URL).apply {
            withClientHeartbeat(10_000)
            withServerHeartbeat(10_000)
        }

    private val gson = Gson()

    @Volatile private var connected = false
    private val lifecycleBag = CompositeDisposable()

    @Volatile private var onMessageGlobal: ((ServerChatDto) -> Unit)? = null

    private val subscribedTopics = ConcurrentHashMap.newKeySet<String>()

    private val topicDispos: MutableMap<String, Disposable> = ConcurrentHashMap()

    fun setConsumer(consumer: (ServerChatDto) -> Unit) {
        onMessageGlobal = consumer
    }

    fun connectIfNeeded(onStatus: (String) -> Unit = {}) {
        if (connected) return

        val lifeDisp = stompClient.lifecycle()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ event ->
                when (event.type) {
                    LifecycleEvent.Type.OPENED -> {
                        connected = true
                        onStatus("OPENED")
                        resubscribeAll()
                    }
                    LifecycleEvent.Type.CLOSED -> {
                        connected = false
                        onStatus("CLOSED")
                        tryReconnect(onStatus)
                    }
                    LifecycleEvent.Type.ERROR -> {
                        connected = false
                        Log.e("ChatService", "STOMP error", event.exception)
                        onStatus("ERROR: ${event.exception?.message}")
                        tryReconnect(onStatus)
                    }
                    LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> {
                        onStatus("FAILED_SERVER_HEARTBEAT")
                        // cerramos y reintentamos
                        softReconnect(onStatus)
                    }
                }
            }, { e ->
                Log.e("ChatService", "Lifecycle subscribe error: ${e.message}")
            })

        lifecycleBag.add(lifeDisp)
        stompClient.connect()
    }

    fun subscribeConversation(conversationId: String) {
        if (conversationId.isBlank()) return
        subscribedTopics.add(conversationId)
        if (connected) subscribeNow(conversationId)
    }

    fun unsubscribeConversation(conversationId: String) {
        subscribedTopics.remove(conversationId)
        topicDispos.remove(conversationId)?.dispose()
    }

    fun sendPayload(
        payload: ChatPayload,
        onOk: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        if (!connected) {
            onError(IllegalStateException("Socket no conectado"))
            return
        }
        val dispSend = stompClient.send("/app/chat.send", gson.toJson(payload))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ onOk() }, { e -> onError(e) })
    }

    fun disconnectAll() {
        try {
            topicDispos.values.forEach { it.dispose() }
            topicDispos.clear()
            subscribedTopics.clear()
            lifecycleBag.clear()
            stompClient.disconnect()
        } catch (_: Throwable) { }
        connected = false
    }

    private fun resubscribeAll() {
        subscribedTopics.forEach { convId ->
            subscribeNow(convId)
        }
    }

    private fun subscribeNow(conversationId: String) {
        topicDispos.remove(conversationId)?.dispose()

        val topic = "/topic/chat.$conversationId"
        val disp = stompClient.topic(topic)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ msg ->
                try {
                    val dto = gson.fromJson(msg.payload, ServerChatDto::class.java)
                    onMessageGlobal?.invoke(dto)
                } catch (ex: Exception) {
                    Log.e("ChatService", "Parse error: ${ex.message} - payload=${msg.payload}")
                }
            }, { e ->
                Log.e("ChatService", "Error suscribiendo $topic: ${e.message}")
            })

        topicDispos[conversationId] = disp
    }

    private fun tryReconnect(onStatus: (String) -> Unit) {
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            if (!connected) {
                softReconnect(onStatus)
            }
        }, 1500)
    }

    private fun softReconnect(onStatus: (String) -> Unit) {
        try {
            stompClient.disconnect()
        } catch (_: Throwable) { }
        connectIfNeeded(onStatus)
    }
}