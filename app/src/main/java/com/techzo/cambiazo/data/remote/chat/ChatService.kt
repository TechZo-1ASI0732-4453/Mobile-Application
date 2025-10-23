package com.techzo.cambiazo.data.remote.chat

import android.util.Log
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent

class ChatService {

    private val SOCKET_URL =
        "wss://cambiazo-techzo-gzdtcfcca4fxeaec.westus-01.azurewebsites.net/ws/websocket"

    private val stompClient: StompClient =
        Stomp.over(Stomp.ConnectionProvider.OKHTTP, SOCKET_URL).apply {
            withClientHeartbeat(10_000)
            withServerHeartbeat(10_000)
        }

    private val composite = CompositeDisposable()
    @Volatile private var connected = false
    private val gson = Gson()

    fun connect(
        conversationId: String,
        onMessageDto: (ServerChatDto) -> Unit,
        onStatus: (String) -> Unit = {}
    ) {
        if (conversationId.isBlank()) return
        if (connected) return

        val lifeDisp = stompClient.lifecycle()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ event ->
                when (event.type) {
                    LifecycleEvent.Type.OPENED -> {
                        connected = true
                        onStatus("OPENED")
                        subscribeToChat(conversationId, onMessageDto)
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

    private fun subscribeToChat(
        conversationId: String,
        onMessageDto: (ServerChatDto) -> Unit
    ) {
        val topic = "/topic/chat.$conversationId"
        val dispTopic = stompClient.topic(topic)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ msg ->
                try {
                    val dto = gson.fromJson(msg.payload, ServerChatDto::class.java)
                    onMessageDto(dto)
                } catch (ex: Exception) {
                    Log.e("ChatService", "Parse payload error: ${ex.message} - payload=${msg.payload}")
                }
            }, { e ->
                Log.e("ChatService", "Error en suscripción a $topic: ${e.message}")
            })

        composite.add(dispTopic)
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
        composite.add(dispSend)
    }

    fun disconnect() {
        try {
            composite.clear()
            stompClient.disconnect()
        } catch (_: Throwable) { }
        connected = false
    }
}