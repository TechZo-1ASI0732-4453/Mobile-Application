package com.techzo.cambiazo.presentation.exchanges.chat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.IntentSender
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.techzo.cambiazo.data.repository.ChatRepository
import com.techzo.cambiazo.domain.Chat
import com.techzo.cambiazo.domain.MessageType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object { private const val TAG = "ChatViewModel" }

    private var currentCid: String = savedStateHandle.get<String>(ChatNavArgs.CONVERSATION_ID).orEmpty()
    private val currentUserId: String = savedStateHandle.get<String>(ChatNavArgs.SENDER_ID).orEmpty()
    private val peerUserId: String = savedStateHandle.get<String>(ChatNavArgs.RECEIVER_ID).orEmpty()
    private val peerUserName: String = savedStateHandle.get<String>(ChatNavArgs.RECEIVER_NAME).orEmpty()
    private val peerUserPhoto: String = savedStateHandle.get<String>(ChatNavArgs.RECEIVER_PHOTO).orEmpty()

    private val exchangeId: String? = run {
        val asInt: Int? = savedStateHandle.get(ChatNavArgs.EXCHANGE_ID)
        if (asInt != null) asInt.toString() else savedStateHandle.get(ChatNavArgs.EXCHANGE_ID)
    }

    private val _messages = MutableStateFlow<List<Chat>>(emptyList())
    val messages: StateFlow<List<Chat>> get() = _messages

    private var subscribed = false
    private var observeJob: Job? = null

    private val _inputText = mutableStateOf(TextFieldValue(""))
    val inputText: State<TextFieldValue> get() = _inputText

    init {
        logArgs()
        if (currentCid.isNotBlank()) {
            viewModelScope.launch {
                chatRepository.syncConversation(currentCid)
               attachMessages(currentCid)
                reconnect()
            }
        }
    }

    private fun logArgs() {
        Log.d(TAG, "Args -> cid=$currentCid, me=$currentUserId, peer=$peerUserId, name=$peerUserName, exchangeId=$exchangeId")
        if (currentUserId.isBlank() || peerUserId.isBlank()) {
            Log.w(TAG, "Argumentos incompletos: no se puede suscribir ni enviar mensajes.")
        }
    }

    private fun attachMessages(cid: String) {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            chatRepository.observeConversation(cid, currentUserId).collectLatest { _messages.value = it }
        }
    }

    fun onInputChange(newValue: TextFieldValue) { _inputText.value = newValue }
    fun getPeerName(): String = peerUserName
    fun getPeerPhoto(): String = peerUserPhoto

    fun reconnect() {
        if (subscribed) return
        if (currentCid.isBlank() || currentUserId.isBlank() || peerUserId.isBlank()) return
        subscribed = true
        chatRepository.subscribeConversation(currentCid)
        Log.d(TAG, "Suscrito a /topic/chat.$currentCid")
    }

    fun send(text: String) {
        val trimmed = text.trim()
        if (trimmed.isBlank()) return
        if (currentUserId.isBlank() || peerUserId.isBlank()) { Log.w(TAG, "Args incompletos"); return }

        viewModelScope.launch {
            if (currentCid.isBlank()) {
                val cid = chatRepository.openConversation(exchangeId, exchangeId)
                if (cid.isNullOrBlank()) { Log.e(TAG, "No se pudo abrir conversación"); return@launch }
                currentCid = cid
                chatRepository.subscribeConversation(currentCid)
                attachMessages(currentCid)
            }

            _inputText.value = TextFieldValue("")
            chatRepository.sendMessage(
                me = currentUserId,
                peer = peerUserId,
                conversationId = currentCid,
                content = trimmed,
                exchangeId = exchangeId,
                type = MessageType.TEXT
            )
            Log.d(TAG, "TEXT enviado -> cid=$currentCid, exchangeId=$exchangeId")
        }
    }

    // ----- Ubicación -----

    fun sendLocationMessage(activity: Activity, label: String? = null) {
        if (currentUserId.isBlank() || peerUserId.isBlank()) return

        val sendWith = { lat: Double?, lng: Double? ->
            if (lat != null && lng != null) {
                viewModelScope.launch {
                    if (currentCid.isBlank()) {
                        val cid = chatRepository.openConversation(null, exchangeId)
                        if (cid.isNullOrBlank()) { Log.e(TAG, "No se pudo abrir conversación"); return@launch }
                        currentCid = cid
                        chatRepository.subscribeConversation(currentCid)
                        attachMessages(currentCid)
                    }
                    chatRepository.sendMessage(
                        me = currentUserId,
                        peer = peerUserId,
                        conversationId = currentCid,
                        content = null,
                        exchangeId = exchangeId,
                        type = MessageType.LOCATION,
                        latitude = lat,
                        longitude = lng,
                        locationLabel = label
                    )
                    Log.d(TAG, "LOCATION enviado -> cid=$currentCid, exchangeId=$exchangeId ($lat,$lng)")
                }
            }
        }

        if (isEmulator()) { getCurrentLocationOnce(activity, sendWith); return }
        checkLocationSettings(activity) { getCurrentLocationOnce(activity, sendWith) }
    }

    // Helpers de ubicación (tu misma lógica de antes) …

    fun checkLocationSettings(activity: Activity, onReady: () -> Unit) {
        val locationRequest = createHighAccuracyLocationRequest()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest).setAlwaysShow(true)
        val client = LocationServices.getSettingsClient(activity)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener { onReady() }
        task.addOnFailureListener { ex ->
            if (ex is ResolvableApiException) {
                try { ex.startResolutionForResult(activity, 1001) } catch (_: IntentSender.SendIntentException) { }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocationOnce(activity: Activity, onResult: (Double?, Double?) -> Unit) {
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
                    android.os.Handler(Looper.getMainLooper()).postDelayed({ cts.cancel() }, timeoutMs)
                    fused.getCurrentLocation(
                        com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, cts.token
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
        onResult: (Double?, Double?) -> Unit,
        timeoutMs: Long,
        accuracyM: Float
    ) {
        @Suppress("DEPRECATION")
        val req = com.google.android.gms.location.LocationRequest.create().apply {
            priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 0L; fastestInterval = 0L; numUpdates = 1
        }
        val cb = object : com.google.android.gms.location.LocationCallback() {
            override fun onLocationResult(result: com.google.android.gms.location.LocationResult) {
                val loc = result.lastLocation
                fused.removeLocationUpdates(this)
                if (loc != null && (!loc.hasAccuracy() || loc.accuracy <= accuracyM)) {
                    onResult(loc.latitude, loc.longitude)
                } else onResult(null, null)
            }
        }
        fused.requestLocationUpdates(req, cb, Looper.getMainLooper())
        android.os.Handler(Looper.getMainLooper()).postDelayed({
            fused.removeLocationUpdates(cb); onResult(null, null)
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
        return f.startsWith("generic") || f.lowercase().contains("vbox") || f.lowercase().contains("test-keys") ||
                m.contains("Emulator", true) || m.contains("Android SDK built for x86", true) ||
                man.contains("Genymotion", true) || (b.startsWith("generic") && d.startsWith("generic")) ||
                p == "google_sdk"
    }
}
