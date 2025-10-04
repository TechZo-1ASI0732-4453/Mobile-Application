package com.techzo.cambiazo.common.permissions

import android.app.Activity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable

@Composable
fun rememberPermissionLauncher(
    permission: Permission,
    permissionViewModel: PermissionViewModel,
    activity: Activity
): ManagedActivityResultLauncher<String, Boolean> {

    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        val status = when {
            isGranted -> PermissionStatus.GRANTED
            !activity.shouldShowRequestPermissionRationale(permission.permission) -> PermissionStatus.PERMANENTLY_DENIED
            else -> PermissionStatus.DENIED
        }
        permissionViewModel.updatePermissionStatus(permission, status)
    }
}