package com.techzo.cambiazo.presentation.auth.changepassword.newpasswordscreen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techzo.cambiazo.common.components.ButtonApp
import com.techzo.cambiazo.common.components.CustomInput
import com.techzo.cambiazo.common.components.DialogApp
import com.techzo.cambiazo.common.components.MainScaffoldApp
import com.techzo.cambiazo.presentation.auth.changepassword.ChangePasswordViewModel

@Composable
fun NewPasswordScreen(
    goBack: () -> Unit,
    goSignIn: () -> Unit,
    email: String,
    changePasswordViewModel: ChangePasswordViewModel = hiltViewModel()
) {

    val passwordError = remember { mutableStateOf(false) }
    val messageError = remember {mutableStateOf("")}

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
                        text = "Nueva\nContraseña",
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
        var password by remember { mutableStateOf("") }
        var repeatPassword by remember { mutableStateOf("") }
        var showDialog by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier.padding(vertical = 30.dp, horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Tu nueva contraseña debe ser distinta a las anteriores contraseñas",
                textAlign = TextAlign.Center,
                fontSize = 19.sp,
                letterSpacing = 1.2.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
            )

            CustomInput(
                value = password,
                placeHolder = "Contraseña",
                type = "Password",
                isError = passwordError.value,
                messageError = null,
                onValueChange = {
                    passwordError.value = false
                    password = it
                }
            )
            CustomInput(
                value = repeatPassword,
                placeHolder = "Contraseña",
                type = "Password",
                isError = passwordError.value,
                messageError = null,
                onValueChange = {
                    passwordError.value = false
                    repeatPassword = it
                }
            )
            if(passwordError.value){
                Text(modifier = Modifier.fillMaxWidth(),
                    text = "*${messageError.value}",
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }
            ButtonApp(
                text = "Cambiar contraseña",
                onClick = {
                    if(password.isNotEmpty() && repeatPassword.isNotEmpty()) {
                        if (password == repeatPassword) {
                            changePasswordViewModel.changePassword(email, password)
                            showDialog = true
                        } else {
                            passwordError.value = true
                            messageError.value = "Las contraseñas no coinciden"
                        }
                    }else{
                        passwordError.value = true
                        messageError.value = "Por favor, llena todos los campos"
                    }
                },
            )
            if (showDialog) {
                // Mostrar el diálogo de éxito
                DialogApp(
                    message = "Contraseña cambiada con éxito",
                    onDismissRequest = { showDialog = false },
                    labelButton1 = "Iniciar Sesión",
                    onClickButton1 = {
                        showDialog=false
                        goSignIn()
                    }
                )
            }
        }
    }


}