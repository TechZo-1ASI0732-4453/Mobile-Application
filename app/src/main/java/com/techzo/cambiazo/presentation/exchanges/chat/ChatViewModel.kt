package com.techzo.cambiazo.presentation.exchanges.chat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.IntentSender
import android.os.Looper
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.techzo.cambiazo.data.repository.ChatRepository
import com.techzo.cambiazo.domain.Chat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val conversationId: String =
        savedStateHandle.get<String>(ChatNavArgs.CONVERSATION_ID).orEmpty()
    private val currentUserId: String =
        savedStateHandle.get<String>(ChatNavArgs.SENDER_ID).orEmpty()
    private val peerUserId: String =
        savedStateHandle.get<String>(ChatNavArgs.RECEIVER_ID).orEmpty()

    val messages: StateFlow<List<Chat>> =
        if (conversationId.isNotBlank())
            chatRepository.observeConversation(conversationId, currentUserId)
                .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        else
            MutableStateFlow(emptyList<Chat>())
                .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private var subscribed = false

    init {
        reconnect()
    }

    fun reconnect() {
        if (subscribed) return
        if (conversationId.isBlank() || currentUserId.isBlank() || peerUserId.isBlank()) return
        subscribed = true
        chatRepository.subscribeConversation(conversationId)
    }

    fun send(text: String) {
        if (text.isBlank()) return
        if (conversationId.isBlank() || currentUserId.isBlank() || peerUserId.isBlank()) return
        chatRepository.sendMessage(currentUserId, peerUserId, conversationId, text)
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun sendLocationMessage(activity: Activity) {
        if (conversationId.isBlank() || currentUserId.isBlank() || peerUserId.isBlank()) return
        if (isEmulator()) {
            getCurrentLocationOnce(activity) { lat, lng ->
                if (lat != null && lng != null) {
                    val content = "L0C4t10N: {latitud:$lat, longitud:$lng}"
                    viewModelScope.launch {
                        chatRepository.sendMessage(currentUserId, peerUserId, conversationId, content)
                    }
                }
            }
            return
        }
        checkLocationSettings(activity) {
            getCurrentLocationOnce(activity) { lat, lng ->
                if (lat != null && lng != null) {
                    val content = "L0C4t10N: {latitud:$lat, longitud:$lng}"
                    viewModelScope.launch {
                        chatRepository.sendMessage(currentUserId, peerUserId, conversationId, content)
                    }
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
        task.addOnSuccessListener { onReady() }
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
        val fused = LocationServices.getFusedLocationProviderClient(activity)
        val isEmu = isEmulator()
        val accuracyThreshold = if (isEmu) 5000f else 50f
        val timeoutMs = if (isEmu) 3000L else 10000L
        fused.lastLocation
            .addOnSuccessListener { last ->
                if (last != null && (!last.hasAccuracy() || last.accuracy <= accuracyThreshold)) {
                    onResult(last.latitude, last.longitude)
                } else {
                    val cts = com.google.android.gms.tasks.CancellationTokenSource()
                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(
                        { cts.cancel() },
                        timeoutMs
                    )
                    fused.getCurrentLocation(
                        com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                        cts.token
                    ).addOnSuccessListener { loc ->
                        if (loc != null && (!loc.hasAccuracy() || loc.accuracy <= accuracyThreshold)) {
                            onResult(loc.latitude, loc.longitude)
                        } else {
                            requestSingleHighAccUpdate(fused, onResult, timeoutMs, accuracyThreshold)
                        }
                    }.addOnFailureListener {
                        requestSingleHighAccUpdate(fused, onResult, timeoutMs, accuracyThreshold)
                    }
                }
            }
            .addOnFailureListener {
                requestSingleHighAccUpdate(fused, onResult, timeoutMs, accuracyThreshold)
            }
    }

    @SuppressLint("MissingPermission")
    private fun requestSingleHighAccUpdate(
        fused: com.google.android.gms.location.FusedLocationProviderClient,
        onResult: (lat: Double?, lng: Double?) -> Unit,
        timeoutMs: Long,
        accuracyM: Float
    ) {
        @Suppress("DEPRECATION")
        val req = com.google.android.gms.location.LocationRequest.create().apply {
            priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 0L
            fastestInterval = 0L
            numUpdates = 1
        }
        val cb = object : com.google.android.gms.location.LocationCallback() {
            override fun onLocationResult(result: com.google.android.gms.location.LocationResult) {
                val loc = result.lastLocation
                fused.removeLocationUpdates(this)
                if (loc != null && (!loc.hasAccuracy() || loc.accuracy <= accuracyM)) {
                    onResult(loc.latitude, loc.longitude)
                } else {
                    onResult(null, null)
                }
            }
        }
        fused.requestLocationUpdates(req, cb, Looper.getMainLooper())
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            fused.removeLocationUpdates(cb)
            onResult(null, null)
        }, timeoutMs)
    }

    private fun createHighAccuracyLocationRequest(): com.google.android.gms.location.LocationRequest {
        @Suppress("DEPRECATION")
        return com.google.android.gms.location.LocationRequest.create().apply {
            priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
            numUpdates = 1
        }
    }

    private fun isEmulator(): Boolean {
        val f = android.os.Build.FINGERPRINT ?: ""
        val m = android.os.Build.MODEL ?: ""
        val man = android.os.Build.MANUFACTURER ?: ""
        val b = android.os.Build.BRAND ?: ""
        val d = android.os.Build.DEVICE ?: ""
        val p = android.os.Build.PRODUCT ?: ""
        return f.startsWith("generic") ||
                f.lowercase().contains("vbox") ||
                f.lowercase().contains("test-keys") ||
                m.contains("Emulator", true) ||
                m.contains("Android SDK built for x86", true) ||
                man.contains("Genymotion", true) ||
                (b.startsWith("generic") && d.startsWith("generic")) ||
                p == "google_sdk"
    }
}