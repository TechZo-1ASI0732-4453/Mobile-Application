package com.techzo.cambiazo.common.permissions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun SettingsPermissionDialog(
    showDialog: Boolean,
    permission: Permission,
    context: Context = LocalContext.current,
    onDismiss: () -> Unit
) {
    if (!showDialog) return
    val activity = context as? Activity
    val textProvider = PermissionTextProviderFactory.getProvider(permission)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(textProvider.title) },
        text = { Text(textProvider.message) },
        confirmButton = {
            TextButton(
                onClick = {
                    activity?.startActivity(
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                    )
                    onDismiss()
                }
            ) { Text(textProvider.confirmButtonText) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(textProvider.dismissButtonText) }
        }
    )
}