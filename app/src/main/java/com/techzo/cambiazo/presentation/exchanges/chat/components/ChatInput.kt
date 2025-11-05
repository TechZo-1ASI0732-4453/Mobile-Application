package com.techzo.cambiazo.presentation.exchanges.chat.components

import android.content.Context
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.techzo.cambiazo.common.permissions.Permission
import com.techzo.cambiazo.common.permissions.PermissionViewModel
import com.techzo.cambiazo.common.permissions.SettingsPermissionDialog

@Composable
fun ChatInput(
    inputText: TextFieldValue,
    onInputChange: (TextFieldValue) -> Unit,
    context: Context,
    onSend: () -> Unit,
            stateViewModel: PermissionViewModel,
    permissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
    sendLocation: () -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                stateViewModel.refreshPermission(Permission.LOCATION, context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = inputText,
            onValueChange = onInputChange,
            placeholder = { Text("Escribe un mensaje...") },
            modifier = Modifier
                .weight(1f)
                .background(Color.White, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color(0xFFDCDCDC),
                unfocusedBorderColor = Color(0xFFDCDCDC),
                focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            ),
            maxLines = 3
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            modifier = Modifier.size(44.dp).background(Color(0xFFFFD146), CircleShape)
                .border(1.dp, Color(0xFFDCDCDC), CircleShape),
            onClick = {
                when {
                    stateViewModel.isLocationGranted -> {
                        sendLocation()
                    }
                    stateViewModel.isLocationDenied -> {
                        permissionLauncher.launch(Permission.LOCATION.permission)
                    }
                    stateViewModel.isLocationPermanentlyDenied ->{
                        showDialog = true
                    }
                }
            }
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Enviar ubicación",
                tint = Color.Black
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            modifier = Modifier
                .size(44.dp)
                .background(Color(0xFFFFD146), CircleShape)
                .border(1.dp, Color(0xFFDCDCDC), CircleShape),
            onClick = onSend
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Enviar",
                tint = Color.Black
            )
        }
    }

    SettingsPermissionDialog(showDialog,Permission.LOCATION,context) {
        showDialog = false
    }
}