package com.techzo.cambiazo.presentation.auth.changepassword

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techzo.cambiazo.common.components.ButtonApp
import com.techzo.cambiazo.common.components.CustomInput
import com.techzo.cambiazo.common.components.DialogApp
import com.techzo.cambiazo.common.components.MainScaffoldApp

@Composable
fun ChangePasswordScreen(
    goBack: () -> Unit,
    goOtpCodeVerificationScreen: (String, Any?) -> Unit,
    changePasswordViewModel: ChangePasswordViewModel = hiltViewModel()
) {
    val email = changePasswordViewModel.email.collectAsState().value
    val isEmailSent = changePasswordViewModel.isEmailSent.value
    val codeGenerated = changePasswordViewModel.code.value

    MainScaffoldApp(
        paddingCard = PaddingValues(top = 10.dp),
        contentsHeader = {
            Column(
                modifier = Modifier.padding(bottom = 50.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 0.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { goBack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(35.dp)
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 0.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Restablece tu\ncontraseña",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 40.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 20.dp)
                    )
                }
            }
        }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 30.dp, horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Ingresa tu correo y te enviaremos instrucciones para restablecer tu contraseña",
                textAlign = TextAlign.Center,
                fontSize = 19.sp,
                letterSpacing = 1.2.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            CustomInput(
                value = email,
                placeHolder = "Correo Electrónico",
                type = "Email",
                pressEnter = {},
            ) {
                changePasswordViewModel.onEmailChange(it)
            }

            Spacer(modifier = Modifier.height(10.dp))

            ButtonApp(
                text = "Enviar",
                onClick = {
                    if (email.isNotBlank()) {
                        changePasswordViewModel.sendEmail(email)
                    } else {
                        Log.e("EMAIL_VERIFICATION", "El correo está vacío")
                    }
                }
            )


            if (isEmailSent) {
                DialogApp(
                    isEmailIcon = true,
                    message = "Revisa tu correo",
                    description = "Hemos enviado las instrucciones de recuperación a su correo electrónico.",
                    labelButton1 = "Entendido",
                    onClickButton1 = {
                        changePasswordViewModel.resetEmailState()
                        goOtpCodeVerificationScreen(email,codeGenerated)
                    }
                )
            }
        }
    }
}
