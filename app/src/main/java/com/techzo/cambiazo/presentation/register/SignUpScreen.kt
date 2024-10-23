package com.techzo.cambiazo.presentation.register

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techzo.cambiazo.common.components.ButtonApp
import com.techzo.cambiazo.common.components.ButtonIconHeaderApp
import com.techzo.cambiazo.common.components.DialogApp
import com.techzo.cambiazo.common.components.FieldTextApp
import com.techzo.cambiazo.common.components.LoginGoogleApp
import com.techzo.cambiazo.common.components.MainScaffoldApp
import com.techzo.cambiazo.common.components.TextLink
import com.techzo.cambiazo.common.components.TextTitleHeaderApp


@Composable
fun SignUpScreen(
    openLogin: () -> Unit = {},
    back: () -> Unit = {},
    navigateToTermsAndConditions: () -> Unit,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val username = viewModel.username.value
    val password = viewModel.password.value
    val name = viewModel.name.value
    val phoneNumber = viewModel.phoneNumber.value
    val showPassword = viewModel.showPassword.value
    val showPasswordRepeat = viewModel.showPasswordRepeat.value
    val repitePassword = viewModel.repitePassword.value

    val isChecked = remember { mutableStateOf(false) }
    val showDialog = remember { mutableStateOf(false) }

    MainScaffoldApp(
        paddingCard = PaddingValues(horizontal = 40.dp, vertical = 25.dp),
        contentsHeader = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 35.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                ButtonIconHeaderApp(Icons.Filled.ArrowBack, onClick = { back() }, iconSize = 35.dp)
                TextTitleHeaderApp("Registrarse")
            }
        }
    ) {
        Spacer(modifier = Modifier.size(20.dp))
        FieldTextApp(name, "Nombre", onValueChange = { viewModel.onNameChange(it) })
        Spacer(modifier = Modifier.size(20.dp))
        FieldTextApp(phoneNumber, "Numero de Telefono", onValueChange = { viewModel.onPhoneNumberChange(it) })
        Spacer(modifier = Modifier.size(20.dp))
        FieldTextApp(username, "Correo electrónico", onValueChange = { viewModel.onUsernameChange(it) })

        OutlinedTextField(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, Color.Gray, RoundedCornerShape(10.dp)),
            shape = RoundedCornerShape(10.dp),
            value = password,
            placeholder = { Text("Contraseña", color = Color.Gray) },
            onValueChange = { viewModel.onPasswordChange(it) },
            visualTransformation =
            if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = {
                    viewModel.onShowPasswordChange(!showPassword)
                }) {
                    Icon(
                        imageVector = Icons.Filled.Visibility,
                        contentDescription = "Visible"
                    )
                }
            }
        )

        OutlinedTextField(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, Color.Gray, RoundedCornerShape(10.dp)),
            shape = RoundedCornerShape(10.dp),
            value = repitePassword,
            placeholder = { Text("Confirmar contraseña", color = Color.Gray) },
            onValueChange = { viewModel.onRepitePasswordChange(it) },
            visualTransformation =
            if (showPasswordRepeat) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = {
                    viewModel.onShowPasswordRepeatChange(!showPasswordRepeat)
                }) {
                    Icon(
                        imageVector = Icons.Filled.Visibility,
                        contentDescription = "Visible"
                    )
                }
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Checkbox(
                checked = isChecked.value,
                onCheckedChange = { isChecked.value = it },
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

        ButtonApp("Registrarse", onClick = {
            viewModel.signUp()
            showDialog.value = true
        })

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp, top = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                color = Color.Gray,
                thickness = 1.dp,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "o Registrate con",
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            HorizontalDivider(
                color = Color.Gray,
                thickness = 1.dp,
                modifier = Modifier.weight(1f)
            )
        }

        LoginGoogleApp()

        TextLink("¿Ya tienes una cuenta? ", " Inicia Sesión", clickable = { openLogin() })

        if (showDialog.value) {
            DialogApp(
                message = "¡Registro exitoso!",
                description = "Solo queda un último paso para empezar con los CambiaZos.",
                labelButton1 = "Iniciar Sesión",
                onClickButton1 = {
                    showDialog.value = false
                    openLogin()
                }
            )
        }
    }
}
