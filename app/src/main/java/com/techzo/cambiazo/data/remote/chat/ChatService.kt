package com.techzo.cambiazo.data.remote.chat

import android.util.Log
import com.techzo.cambiazo.domain.Chat
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
class ChatService {
    private val SOCKET_URL = "wss://cambiazo-techzo-gzdtcfcca4fxeaec.westus-01.azurewebsites.net/ws/websocket"

    private val stompClient: StompClient =
        Stomp.over(Stomp.ConnectionProvider.OKHTTP, SOCKET_URL).apply {
            withClientHeartbeat(10_000)
            withServerHeartbeat(10_000)
        }

    private val composite = CompositeDisposable()
    @Volatile private var connected = false

    fun connect(
        conversationId: String,
        onMessage: (Chat) -> Unit,
        onStatus: (String) -> Unit = {}
    ) {
        if (conversationId.isBlank()) {
            Log.w("ChatService", "conversationId vacío: no conecto")
            return
        }
        if (connected) {
            Log.d("ChatService", "Ya conectado, ignoro connect()")
            return
        }

        val lifeDisp = stompClient.lifecycle()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ event ->
                when (event.type) {
                    LifecycleEvent.Type.OPENED -> {
                        connected = true
                        onStatus("OPENED")
                        subscribeToChat(conversationId, onMessage)
                    }
                    LifecycleEvent.Type.CLOSED -> {
                        connected = false
                        onStatus("CLOSED")
                    }
                    LifecycleEvent.Type.ERROR -> {
                        connected = false
                        Log.e("ChatService", "STOMP error", event.exception)
                        onStatus("ERROR: ${event.exception?.message}")
                    }
                    LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> {
                        onStatus("FAILED_SERVER_HEARTBEAT")
                    }
                }
            }, { e ->
                Log.e("ChatService", "Lifecycle subscribe error: ${e.message}")
            })

        composite.add(lifeDisp)
        stompClient.connect()
    }

    private fun subscribeToChat(conversationId: String, onMessage: (Chat) -> Unit) {
        val topic = "/topic/chat.$conversationId"
        val dispTopic = stompClient.topic(topic)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ msg ->
                try {
                    val chat = Chat.fromJson(msg.payload) // o msg.getPayload()
                    onMessage(chat)
                } catch (ex: Exception) {
                    Log.e("ChatService", "Parse payload error: ${ex.message} - payload=${msg.payload}")
                }
            }, { e ->
                Log.e("ChatService", "Error en suscripción a $topic: ${e.message}")
            })

        composite.add(dispTopic)
    }

    fun sendMessage(message: Chat) {
        if (!connected) {
            Log.w("ChatService", "Intento de envío sin conexión. Ignorado.")
            return
        }
        val dispSend = stompClient.send("/app/chat.send", message.toJson())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, { e ->
                Log.e("ChatService", "Error al enviar: ${e.message}")
            })
        composite.add(dispSend)
    }

    fun disconnect() {
        try {
            composite.clear() // cancela lifecycle + topic + send
            stompClient.disconnect()
        } catch (_: Throwable) { }
        connected = false
    }
}
