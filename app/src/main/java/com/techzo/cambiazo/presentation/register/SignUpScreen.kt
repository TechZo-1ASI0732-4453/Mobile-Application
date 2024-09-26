package com.techzo.cambiazo.presentation.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.techzo.cambiazo.common.components.ButtonApp
import com.techzo.cambiazo.common.components.ButtonIconHeaderApp
import com.techzo.cambiazo.common.components.FieldTextApp
import com.techzo.cambiazo.common.components.LoginGoogleApp
import com.techzo.cambiazo.common.components.MainScaffoldApp
import com.techzo.cambiazo.common.components.TextLink
import com.techzo.cambiazo.common.components.TextTitleHeaderApp


@Composable
fun SignUpScreen(openLogin: () -> Unit = {},
                 back: () -> Unit = {},
                 viewModel: SignUpViewModel = viewModel()
                 ){

    val state = viewModel.state.value
    val username = viewModel.username.value
    val password = viewModel.password.value
    val name = viewModel.name.value
    val phoneNumber = viewModel.phoneNumber.value
    val repitePassword = viewModel.repitePassword.value

    val isChecked = remember {
        mutableStateOf(false)
    }


    MainScaffoldApp(
        paddingCard = PaddingValues(40.dp),
        contentsHeader = {

            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){

                ButtonIconHeaderApp(Icons.Filled.ArrowBack,onClick = {back()})
                TextTitleHeaderApp("Registrarse")
            }
        }
    ){
        FieldTextApp(name,"Nombre",onValueChange = { viewModel.onNameChange(it) })
        FieldTextApp(phoneNumber,"Numero de Telefono",onValueChange = { viewModel.onPhoneNumberChange(it) })
        FieldTextApp(username,"Correo electrónico",onValueChange = { viewModel.onUsernameChange(it) })
        FieldTextApp(password,"Contraseña",onValueChange = { viewModel.onPasswordChange(it) })
        FieldTextApp(repitePassword,"Confirmar contrasenia",onValueChange = { viewModel.onRepitePasswordChange(it) })


        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            Checkbox(checked = isChecked.value,
                onCheckedChange = {},
                modifier = Modifier.size(20.dp)
            )
            TextLink(" Aceptar ","terminos y condiciones",clickable = { },Arrangement.Start)
        }

        ButtonApp("Registrarse", onClick = {
            viewModel.signUp()
        })
        state.data?.let {
            openLogin()
        }
        if (state.isLoading) {
            CircularProgressIndicator()
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
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

        TextLink("¿Ya tienes una cuenta? "," Inicia Sesión", clickable = {openLogin()})

    }
}