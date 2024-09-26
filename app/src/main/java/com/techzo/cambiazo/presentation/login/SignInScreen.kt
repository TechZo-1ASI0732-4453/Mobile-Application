package com.techzo.cambiazo.presentation.login

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.techzo.cambiazo.R
import com.techzo.cambiazo.common.components.ButtonApp
import com.techzo.cambiazo.common.components.FieldTextApp
import com.techzo.cambiazo.common.components.LoginGoogleApp
import com.techzo.cambiazo.common.components.MainScaffoldApp
import com.techzo.cambiazo.common.components.TextLink

@Composable
fun SignInScreen(openRegister: () -> Unit = {},
                openApp: () -> Unit = {},
                openForgotPassword: () -> Unit = {},
                viewModel: SignInViewModel = viewModel()){


    val state = viewModel.state.value
    val username = viewModel.username.value
    val password = viewModel.password.value
    val showPassword = viewModel.showPassword.value

    MainScaffoldApp(
        paddingCard = PaddingValues(40.dp),
        contentsHeader = {
            Image(
                painter = painterResource(R.drawable.cambiazo_logo_name),
                contentDescription = "logo gmail",
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) {
        Text(
            text = "Iniciar Sesión",
            fontSize = 38.sp,
            modifier = Modifier
                .padding(bottom =35.dp),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily.SansSerif),
        )


        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
            value = username,
            placeholder = {
                Text("Correo electrónico")},
            onValueChange = { viewModel.onUsernameChange(it)}
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = password,
            placeholder = { Text("Contrasenia")},
            onValueChange = { viewModel.onPasswordChange(it) },
            visualTransformation =
                if(showPassword) VisualTransformation.None else PasswordVisualTransformation(),
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

        TextLink("","Olvidé mi contraseña", clickable = {openForgotPassword()},Arrangement.End)

        ButtonApp("Iniciar Sesion", onClick = {
            viewModel.signIn()
        })
        state.data?.let {
            openApp()
        }
        if (state.isLoading) {
            CircularProgressIndicator()
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                color = Color.Gray,
                thickness = 1.dp,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "o Inicia Sesion con",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily.SansSerif)
            )
            HorizontalDivider(
                color = Color.Gray,
                thickness = 1.dp,
                modifier = Modifier.weight(1f)
            )

        }

        LoginGoogleApp()

        TextLink("¿Todavía no tienes cuenta?"," Regístrate", clickable = {openRegister()})

    }
}

@Preview
@Composable
fun PreviewSignInScreen(){
    SignInScreen()
}


