package com.techzo.cambiazo.common.permissions

import android.Manifest

sealed class Permission(val permission: String) {
    object CAMERA : Permission(Manifest.permission.CAMERA)
    object AUDIO : Permission(Manifest.permission.RECORD_AUDIO)
    object LOCATION : Permission(Manifest.permission.ACCESS_FINE_LOCATION)
}

