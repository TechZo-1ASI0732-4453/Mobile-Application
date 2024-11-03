package com.techzo.cambiazo.presentation.auth.register

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techzo.cambiazo.common.components.ButtonApp
import com.techzo.cambiazo.common.components.ButtonIconHeaderApp
import com.techzo.cambiazo.common.components.CustomInput
import com.techzo.cambiazo.common.components.DialogApp
import com.techzo.cambiazo.common.components.LoginGoogleApp
import com.techzo.cambiazo.common.components.MainScaffoldApp
import com.techzo.cambiazo.common.components.TextLink
import com.techzo.cambiazo.common.components.TextTitleHeaderApp
import com.techzo.cambiazo.presentation.auth.GoogleAuthViewModel
import com.techzo.cambiazo.presentation.auth.PhoneInputScreen
import kotlinx.coroutines.launch


@Composable
fun SignUpScreen(
    openLogin: () -> Unit = {},
    back: () -> Unit = {},
    openApp: () -> Unit = {},
    navigateToTermsAndConditions: () -> Unit,
    viewModel: SignUpViewModel = hiltViewModel(),
    googleAuthViewModel: GoogleAuthViewModel = hiltViewModel()
) {
    val name = viewModel.name.value
    val password = viewModel.password.value
    val email = viewModel.username.value
    val phoneNumber = viewModel.phoneNumber.value
    val showPassword = viewModel.showPassword.value
    val showPasswordRepeat = viewModel.showPasswordRepeat.value
    val repitePassword = viewModel.repitePassword.value
    val isChecked = viewModel.isChecked
    var showPhoneInput by remember { mutableStateOf(false) }
    val state = viewModel.state.value
    val successDialog = viewModel.successDialog.value
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        googleAuthViewModel.handleGoogleSignInResult(result.data) { credential, isValid ->
            if (isValid && credential != null) {
                coroutineScope.launch {
                    googleAuthViewModel.signInWithGoogleCredential(credential) { isNewUser ->
                        if (isNewUser) {
                            showPhoneInput = true
                        } else {
                            openApp()
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
            paddingCard = PaddingValues(horizontal = 40.dp),
            contentsHeader = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                ) {
                    ButtonIconHeaderApp(
                        iconVector = Icons.Filled.ArrowBack,
                        onClick = { back() },
                        iconSize = 35.dp,
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(top = 40.dp)
                    ) {
                        Text(
                            text = "Registrarse",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        ) {
            Spacer(modifier = Modifier.size(45.dp))
            CustomInput(
                value = name,
                type = "Text",
                placeHolder = "Nombre",
                onValueChange = { viewModel.onNameChange(it) }
            )
            Spacer(modifier = Modifier.size(10.dp))
            CustomInput(
                value = phoneNumber,
                type = "Number",
                placeHolder = "Numero de Telefono",
                onValueChange = { viewModel.onPhoneNumberChange(it) }
            )
            Spacer(modifier = Modifier.size(10.dp))
            CustomInput(
                value = email,
                type = "Text",
                placeHolder = "Correo electrónico",
                onValueChange = { viewModel.onUsernameChange(it) }
            )

            Spacer(modifier = Modifier.size(10.dp))


            CustomInput(
                value = password,
                type = "Password",
                placeHolder = "Contraseña",
                onValueChange = { viewModel.onPasswordChange(it) })


            Spacer(modifier = Modifier.size(10.dp))

            CustomInput(
                value = password,
                type = "Password",
                placeHolder = "Confirmar contraseña",
                onValueChange = { viewModel.onRepitePasswordChange(it) })


            Spacer(modifier = Modifier.size(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Checkbox(
                    checked = isChecked.value,
                    onCheckedChange = { viewModel.onCheckedChange(it) },
                    modifier = Modifier.size(20.dp),
                    colors = CheckboxDefaults.colors(
                        uncheckedColor = Color.Gray,
                        checkedColor = Color(0xFFFFD146)
                    )
                )
                TextLink(
                    "  Aceptar ",
                    "terminos y condiciones",
                    clickable = { navigateToTermsAndConditions() },
                    Arrangement.Start
                )
            }

            Spacer(modifier = Modifier.size(15.dp))


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


            if (state.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            } else {
                ButtonApp("Registrarse", onClick = {
                    viewModel.signUp()
                })
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp, top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    color = Color(0xFF888888),
                    thickness = 1.dp,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "o Registrate con",
                    fontSize = 16.sp,
                    color = Color(0xFF888888),
                    modifier = Modifier.padding(horizontal = 16.dp),
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

            Spacer(modifier = Modifier.size(20.dp))

            TextLink("¿Ya tienes una cuenta? ", " Inicia Sesión", clickable = { openLogin() })

            if (successDialog) {
                DialogApp(
                    message = "¡Registro exitoso!",
                    description = "Solo queda un último paso para empezar con los CambiaZos.",
                    labelButton1 = "Iniciar Sesión",
                    onClickButton1 = {
                        viewModel.hideSuccessDialog()
                        openLogin()
                    }
                )
            }
        }
    }
}
