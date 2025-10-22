package com.techzo.cambiazo.presentation.exchanges.chat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.IntentSender
import android.os.Looper
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.techzo.cambiazo.common.UIState
import com.techzo.cambiazo.data.repository.ChatRepository
import com.techzo.cambiazo.domain.Chat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

const val ARG_CONVERSATION_ID = "conversationId"
const val ARG_SENDER_ID       = "userSenderId"
const val ARG_RECEIVER_ID     = "userReceiverId"


@HiltViewModel
class ChatViewModel @Inject constructor( private val chatRepository: ChatRepository,savedStateHandle: SavedStateHandle) : ViewModel() {

    private val _messages = MutableStateFlow<List<UIState<Chat>>>(emptyList())
    val messages: StateFlow<List<UIState<Chat>>> = _messages

    private val conversationId: String =
        savedStateHandle.get<String>(ARG_CONVERSATION_ID).orEmpty()
    private val currentUserId: String =
        savedStateHandle.get<String>(ARG_SENDER_ID).orEmpty()
    private val peerUserId: String =
        savedStateHandle.get<String>(ARG_RECEIVER_ID).orEmpty()
//    init {
//        reconnect()
//    }
   private var isConnected = false
    fun reconnect() {
        if (isConnected) return
        if (!conversationId.isBlank() && !currentUserId.isBlank() && !peerUserId.isBlank()) {
            isConnected = true
            chatRepository.connect(
                conversationId = conversationId,
                onMessage = { chat ->
                    viewModelScope.launch {
//                        if(appendIfNewById(_messages.value,chat)){
                            val newState = UIState(isLoading = false, data = chat)
                            _messages.value = _messages.value + newState
//                        }else{
//                            _messages.value = _messages.value.map {
//                                if (it.data!!.id == chat.id) it.copy(isLoading = false) else it
//                            }
//                        }


                    }
                },
            )
        }
    }
    fun send(text: String) {
        if (text.isBlank()) return
        val chat = Chat(
            conversationId = conversationId,
            senderId = currentUserId,
            receiverId = peerUserId,
            content = text
        )
        chatRepository.sendMessage(chat)
    }

    override fun onCleared() {
        super.onCleared()
        chatRepository.disconnect()
    }


    fun sendLocationMessage(activity: Activity) {

        val tempMessage = Chat(
                senderId = currentUserId,
                receiverId = peerUserId,
                conversationId = conversationId,
                content = "L0C4t10N: {latitud:0, longitud:0}" // placeholder
        )
        val tempId = tempMessage.id

        // Añadimos el mensaje temporal
        _messages.value = _messages.value + UIState(isLoading = true, data = tempMessage)

        checkLocationSettings(activity) {
            getCurrentLocationOnce(activity) { lat, lng ->

                if (lat != null && lng != null) {
                    val updatedMessage = tempMessage.copy(
                            content = "L0C4t10N: {latitud:$lat, longitud:$lng}"
                    )


                    viewModelScope.launch {

                        chatRepository.sendMessage(updatedMessage)

                    }

                } else {
                    // Si falla, eliminamos el temporal
                    _messages.value = _messages.value.filterNot { it.data!!.id == tempId }
                }
            }
        }
    }

    fun checkLocationSettings(activity: Activity, onReady: () -> Unit) {
        val locationRequest = createHighAccuracyLocationRequest()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        val client = LocationServices.getSettingsClient(activity)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            onReady()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(activity, 1001)
                } catch (_: IntentSender.SendIntentException) { }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocationOnce(activity: Activity, onResult: (lat: Double?, lng: Double?) -> Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

        @Suppress("DEPRECATION")
        val locationRequest = createHighAccuracyLocationRequest()  // Solo una actualización

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val location = locationResult.lastLocation
                    if (location != null) {
                        onResult(location.latitude, location.longitude)
                    } else {
                        onResult(null, null)
                    }
                    fusedLocationClient.removeLocationUpdates(this)
                }
            },
            Looper.getMainLooper()
        )
    }

    private fun createHighAccuracyLocationRequest(): com.google.android.gms.location.LocationRequest {
        @Suppress("DEPRECATION")
        return com.google.android.gms.location.LocationRequest.create().apply {
            priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
            numUpdates = 1
        }

    }


    //eliminar despues
    private fun appendIfNewById(list: List<UIState<Chat>>, msg: Chat): Boolean {
        val exists = msg.id != null && list.any { it.data?.id == msg.id }
        return exists
    }

    private fun promoteByClientTempId(list: List<UIState<Chat>>, serverMsg: Chat): List<UIState<Chat>> {
        if(appendIfNewById(list, serverMsg)){
            return list + UIState(isLoading = false, data = serverMsg)
        }else{
        val tmp = serverMsg.id
        var replaced = false
        val newList = list.map { ui ->
            val d = ui.data
            if (d?.id == tmp) {
                replaced = true
                ui.copy(
                    isLoading = false,
                    data = d.copy(
                        id = serverMsg.id ?: d.id,
                        content = serverMsg.content
                    )
                )
            } else ui
        }
        return newList
        }
    }

}
