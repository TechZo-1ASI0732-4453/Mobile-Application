package com.techzo.cambiazo.presentation.auth.login

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.techzo.cambiazo.R
import com.techzo.cambiazo.common.components.ButtonApp
import com.techzo.cambiazo.common.components.CustomInput
import com.techzo.cambiazo.common.components.LoginGoogleApp
import com.techzo.cambiazo.common.components.MainScaffoldApp
import com.techzo.cambiazo.common.components.TextLink
import com.techzo.cambiazo.presentation.auth.GoogleAuthViewModel
import com.techzo.cambiazo.presentation.auth.PhoneInputScreen
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(
    openRegister: () -> Unit = {},
    openApp: () -> Unit = {},
    openForgotPassword: () -> Unit = {},
    viewModel: SignInViewModel = hiltViewModel(),
    googleAuthViewModel: GoogleAuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state = viewModel.state.value
    val username = viewModel.username.value
    val password = viewModel.password.value
    val isChecked = viewModel.isChecked
    val coroutineScope = rememberCoroutineScope()
    val errorUsername = viewModel.errorUsername.value
    val errorPassword = viewModel.errorPassword.value
    var showPhoneInput by remember { mutableStateOf(false) }
    val phoneNumber = googleAuthViewModel.phoneNumber.value


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        googleAuthViewModel.handleGoogleSignInResult(result.data) { credential, isValid ->
            if (isValid && credential != null) {
                coroutineScope.launch {
                    googleAuthViewModel.signInWithGoogleCredential(credential) { success, needPhone ->
                        when {
                            success -> openApp()
                            needPhone -> showPhoneInput = true
                        }
                    }
                }
            }
        }
    }


    if (showPhoneInput) {
        PhoneInputScreen(
            phoneNumber = phoneNumber,
            onPhoneNumberChange = { googleAuthViewModel.onPhoneNumberChange(it) },
            onConfirm = {
                googleAuthViewModel.completeRegistration(phoneNumber) {
                    openApp()
                }
            },
            onBack = { showPhoneInput = false }
        )
    } else {
        MainScaffoldApp(
            paddingCard = PaddingValues(start = 40.dp, end = 40.dp, top = 50.dp),
            contentsHeader = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.cambiazo_logo_name),
                        contentDescription = "logo gmail",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(65.dp)
                    )
                }

            }
        ) {

            Text(
                text = "Iniciar Sesión",
                fontSize = 32.sp,
                modifier = Modifier
                    .padding(bottom = 30.dp),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.SansSerif
                ),
            )


            CustomInput(
                value = username,
                placeHolder = "Correo electrónico",
                type = "Email",
                isError = errorUsername.data ?: false,
                messageError = errorUsername.message
            ) {
                viewModel.onUsernameChange(it)
            }
            Spacer(modifier = Modifier.height(10.dp))

            CustomInput(
                value = password,
                placeHolder = "Contraseña",
                type = "Password",
                isError = errorPassword.data ?: false,
                messageError = errorPassword.message
            ) {
                viewModel.onPasswordChange(it)
            }


            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Checkbox(
                    checked = isChecked.value,
                    onCheckedChange = { viewModel.onCheckedChange(it) },
                    modifier = Modifier.size(20.dp),
                    colors = CheckboxDefaults.colors(
                        checkmarkColor = Color.White,
                        checkedColor = Color(0xFFFFD146),
                        uncheckedColor = Color.Gray
                    )
                )
                Text(
                    text = "Recordar sesión",
                    modifier = Modifier.padding(start = 5.dp),
                )
                TextLink(
                    "",
                    "Olvidé mi contraseña",
                    clickable = { openForgotPassword() },
                    Arrangement.End
                )
            }


            Spacer(modifier = Modifier.height(15.dp))

            if (state.message.isEmpty()) {
                Spacer(modifier = Modifier.height(22.dp))
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                        .fillMaxWidth()
                        .height(22.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Error,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = state.message,
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }

            }

            state.data?.let {
                openApp()
            }
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp, bottom = 31.dp)
                ) {
                    LinearProgressIndicator(
                        color = Color(0xFFFFD146),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth()
                    )
                }
            } else {
                ButtonApp("Iniciar Sesion", onClick = {
                    viewModel.signIn()
                })
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp, top = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    color = Color(0xFF888888),
                    thickness = 1.dp,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "o Inicia Sesion con",
                    color = Color(0xFF888888),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Normal
                    )
                )
                HorizontalDivider(
                    color = Color(0xFF888888),
                    thickness = 1.dp,
                    modifier = Modifier.weight(1f)
                )

            }

            LoginGoogleApp(onClick = {
                launcher.launch(googleAuthViewModel.getGoogleSignInIntent(context))
            })

            Spacer(modifier = Modifier.height(20.dp))

            TextLink("¿Todavía no tienes cuenta?", " Regístrate", clickable = { openRegister() })


        }
    }
}



