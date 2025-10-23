package com.techzo.cambiazo.data.remote.chat

import android.util.Log
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.min

class ChatService {

    private val SOCKET_URL =
        "wss://cambiazo-techzo-gzdtcfcca4fxeaec.westus-01.azurewebsites.net/ws/websocket"

    private val stompClient: StompClient =
        Stomp.over(Stomp.ConnectionProvider.OKHTTP, SOCKET_URL).apply {
            withClientHeartbeat(10_000)
            withServerHeartbeat(10_000)
        }

    private val gson = Gson()

    private val lifecycleComposite = CompositeDisposable()
    private val sendComposite = CompositeDisposable()

    @Volatile private var connected = false
    private val connecting = AtomicBoolean(false)

    private val pendingTopics = mutableSetOf<String>()
    private val activeSubscriptions: MutableMap<String, Disposable> = ConcurrentHashMap()

    private var retryAttempt = 0

    fun ensureConnected() {
        if (connected || connecting.get()) return
        connecting.set(true)

        val disp = stompClient.lifecycle()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ event ->
                when (event.type) {
                    LifecycleEvent.Type.OPENED -> {
                        connected = true
                        connecting.set(false)
                        retryAttempt = 0
                        resubscribeAll()
                    }
                    LifecycleEvent.Type.CLOSED, LifecycleEvent.Type.ERROR -> {
                        connected = false
                        connecting.set(false)
                        scheduleReconnect()
                    }
                    LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> { /* opcional: log */ }
                }
            }, {
                connected = false
                connecting.set(false)
                scheduleReconnect()
            })

        lifecycleComposite.add(disp)
        stompClient.connect()
    }

    fun subscribeConversation(
        conversationId: String,
        onMessageDto: (ServerChatDto) -> Unit
    ) {
        if (conversationId.isBlank()) return
        val topic = "/topic/chat.$conversationId"

        pendingTopics.add(topic)
        if (activeSubscriptions.containsKey(topic)) return

        if (connected) {
            val sub = stompClient.topic(topic)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ msg ->
                    try {
                        val dto = gson.fromJson(msg.payload, ServerChatDto::class.java)
                        onMessageDto(dto)
                    } catch (e: Exception) {
                        Log.e("ChatService", "parse error: ${e.message}")
                    }
                }, { e ->
                    Log.e("ChatService", "topic error $topic: ${e.message}")
                    activeSubscriptions.remove(topic)
                })
            activeSubscriptions[topic] = sub
        } else {
            ensureConnected()
        }
    }

    private fun resubscribeAll() {
        if (!connected) return
        val iterator = pendingTopics.toList()
        iterator.forEach { topic ->
            if (activeSubscriptions.containsKey(topic)) return@forEach
            val sub = stompClient.topic(topic)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ msg ->
                    try {
                        val dto = gson.fromJson(msg.payload, ServerChatDto::class.java)
                        // No tenemos callback aquí; este método sólo se usa en OPENED.
                        // Las subs reales se crean vía subscribeConversation, que sí recibe callback.
                    } catch (e: Exception) {
                        Log.e("ChatService", "parse error: ${e.message}")
                    }
                }, { e ->
                    Log.e("ChatService", "topic error $topic: ${e.message}")
                    activeSubscriptions.remove(topic)
                })
            activeSubscriptions[topic] = sub
        }
    }

    fun sendPayload(
        payload: ChatPayload,
        onOk: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        if (!connected) {
            onError(IllegalStateException("Socket no conectado"))
            ensureConnected()
            return
        }
        val d = stompClient.send("/app/chat.send", gson.toJson(payload))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ onOk() }, { e -> onError(e) })
        sendComposite.add(d)
    }

    private fun scheduleReconnect() {
        retryAttempt++
        val delayMs = min(30_000, (1_000 * Math.pow(2.0, (retryAttempt - 1).toDouble())).toInt())
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            if (!connected) {
                try {
                    stompClient.disconnect()
                } catch (_: Throwable) {}
                lifecycleComposite.clear()
                ensureConnected()
            }
        }, delayMs.toLong())
    }

    fun stop() {
        try {
            activeSubscriptions.values.forEach { it.dispose() }
            activeSubscriptions.clear()
            pendingTopics.clear()
            sendComposite.clear()
            lifecycleComposite.clear()
            stompClient.disconnect()
        } catch (_: Throwable) { }
        connected = false
        connecting.set(false)
        retryAttempt = 0
    }
}