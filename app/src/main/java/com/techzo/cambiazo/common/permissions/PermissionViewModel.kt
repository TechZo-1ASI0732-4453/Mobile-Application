package com.techzo.cambiazo.common.permissions

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PermissionViewModel @Inject constructor() : ViewModel() {

    private val _permissionStates = mutableStateMapOf<Permission, PermissionStatus>()
    val permissionStates: Map<Permission, PermissionStatus> get() = _permissionStates

    private val _showDialog = mutableStateOf(false)
    val showDialog: State<Boolean> get() = _showDialog

    // ------------------------------
    // GENERAL FUNCTIONS
    // ------------------------------
    fun updatePermissionStatus(permission: Permission, status: PermissionStatus) {
        _permissionStates[permission] = status
    }

    // ------------------------------
    // CAMERA PERMISSION
    // ------------------------------
    val cameraStatus: PermissionStatus
        get() = _permissionStates[Permission.CAMERA] ?: PermissionStatus.DENIED
    val isCameraGranted: Boolean
        get() = cameraStatus == PermissionStatus.GRANTED
    val isCameraDenied: Boolean
        get() = cameraStatus == PermissionStatus.DENIED
    val isCameraPermanentlyDenied: Boolean
        get() = cameraStatus == PermissionStatus.PERMANENTLY_DENIED

    // ------------------------------
    // LOCATION PERMISSION
    // ------------------------------
    val locationStatus: PermissionStatus
        get() = _permissionStates[Permission.LOCATION] ?: PermissionStatus.DENIED
    val isLocationGranted: Boolean
        get() = locationStatus == PermissionStatus.GRANTED
    val isLocationDenied: Boolean
        get() = locationStatus == PermissionStatus.DENIED
    val isLocationPermanentlyDenied: Boolean
        get() = locationStatus == PermissionStatus.PERMANENTLY_DENIED

    // ------------------------------
    // AUDIO PERMISSION
    // ------------------------------
    val audioStatus: PermissionStatus
        get() = _permissionStates[Permission.AUDIO] ?: PermissionStatus.DENIED
    val isAudioGranted: Boolean
        get() = audioStatus == PermissionStatus.GRANTED
    val isAudioDenied: Boolean
        get() = audioStatus == PermissionStatus.DENIED
    val isAudioPermanentlyDenied: Boolean
        get() = audioStatus == PermissionStatus.PERMANENTLY_DENIED

    // ------------------------------
    // OTHERS PERMISSION
    // ------------------------------

    fun refreshPermission(permission: Permission, context: Context) {
        val status = when {
            ContextCompat.checkSelfPermission(context, permission.permission) == PackageManager.PERMISSION_GRANTED ->
                PermissionStatus.GRANTED
            !ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, permission.permission) ->
                PermissionStatus.PERMANENTLY_DENIED
            else -> PermissionStatus.DENIED
        }

        updatePermissionStatus(permission, status)
    }
}