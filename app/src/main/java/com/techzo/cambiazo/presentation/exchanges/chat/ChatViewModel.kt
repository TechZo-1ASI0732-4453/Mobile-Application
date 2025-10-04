package com.techzo.cambiazo.presentation.exchanges.chat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.os.Looper
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Priority
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

enum class MessageType {
    TEXT, LOCATION
}

data class ChatMessage(
    val id: Int,
    val text: String,
    val isSentByMe: Boolean,
    val type: MessageType = MessageType.TEXT,
    val latitude: Double? = null,
    val longitude: Double? = null
)
@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel(){

    private val _messages = mutableStateListOf<ChatMessage>()
    val messages: List<ChatMessage> get() = _messages

    fun sendTextMessage(text: String) {
        _messages.add(ChatMessage(id = _messages.size + 1, text = text, isSentByMe = true))
    }

    fun sendLocationMessage(lat: Double, lng: Double) {
        _messages.add(ChatMessage(
            id = _messages.size + 1,
            text = "Ubicación",
            isSentByMe = true,
            latitude = lat,
            longitude = lng,
            type = MessageType.LOCATION
        ))
    }

     fun checkLocationSettings(activity: Activity) {
        @Suppress("DEPRECATION")
        val locationRequest = createHighAccuracyLocationRequest()

         val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        val client = LocationServices.getSettingsClient(activity)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            getCurrentLocation(activity) { lat, lng ->
                sendLocationMessage(lat, lng)
            }

        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(activity, 1001)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // No se pudo mostrar el diálogo → manejar error
                }
            } else {
                // No se puede resolver → GPS apagado y no hay diálogo disponible
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
}
