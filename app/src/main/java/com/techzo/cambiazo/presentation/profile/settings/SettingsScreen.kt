package com.techzo.cambiazo.presentation.profile.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techzo.cambiazo.common.Constants
import com.techzo.cambiazo.common.components.ButtonIconHeaderApp
import com.techzo.cambiazo.common.components.DialogApp
import com.techzo.cambiazo.common.components.MainScaffoldApp
import com.techzo.cambiazo.common.components.TextTitleHeaderApp
import com.techzo.cambiazo.presentation.profile.ProfileOption
import com.techzo.cambiazo.presentation.profile.ProfileViewModel

@Composable
fun SettingsScreen(
    deleteAccount: () -> Unit = {},
    changePassword: (String) -> Unit = {},
    back: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.isLoggedOut.value) {
        if (viewModel.isLoggedOut.value) {
            deleteAccount()
        }
    }

    if (showDeleteDialog) {
        DialogApp(
            message = "Confirmación",
            description = "¿Está seguro de que desea eliminar tu cuenta?",
            labelButton1 = "Aceptar",
            labelButton2 = "Cancelar",
            onDismissRequest = { showDeleteDialog = false },
            onClickButton1 = {
                viewModel.deleteAccount()
                showDeleteDialog = false
            },
            onClickButton2 = { showDeleteDialog = false }
        )
    }


    MainScaffoldApp(
        paddingCard = PaddingValues(top = 40.dp),
        contentsHeader = {
            Column(
                Modifier
                    .padding(bottom = 30.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ButtonIconHeaderApp(Icons.Filled.ArrowBack, onClick = { back() })
                TextTitleHeaderApp("Configuración")
            }
        },
        content = {
            Column(modifier = Modifier.padding(horizontal = 25.dp)) {
                ProfileOption(
                    icon = Icons.Outlined.Lock,
                    text = "Cambiar Contraseña",
                    onClick = {
                        Constants.user?.let { changePassword(it.username) }
                    }
                )

                HorizontalDivider(color = Color(0xFFF2F2F2), thickness = 1.5.dp, modifier = Modifier.padding(vertical = 5.dp))

                ProfileOption(
                    icon = Icons.Outlined.Delete,
                    text = "Eliminar Cuenta",
                    onClick = {
                        showDeleteDialog = true
                    }
                )
            }
        }
    )
}