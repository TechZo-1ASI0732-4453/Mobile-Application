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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.techzo.cambiazo.common.components.ButtonApp
import com.techzo.cambiazo.common.components.ButtonIconHeaderApp
import com.techzo.cambiazo.common.components.FieldTextApp
import com.techzo.cambiazo.common.components.LoginGoogleApp
import com.techzo.cambiazo.common.components.MainScaffoldApp
import com.techzo.cambiazo.common.components.TextLink
import com.techzo.cambiazo.common.components.TextTitleHeaderApp


@Composable
fun SingInScreen(openLogin: () -> Unit = {},
                 back: () -> Unit = {}){

    val email = remember {
        mutableStateOf("")
    }

    val password = remember {
        mutableStateOf("")
    }

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
        FieldTextApp(email.value,"Nombre",onValueChange = { email.value = it })
        FieldTextApp(email.value,"Numero de Telefono",onValueChange = { email.value = it })
        FieldTextApp(email.value,"Correo electrónico",onValueChange = { email.value = it })
        FieldTextApp(email.value,"Contrasenia",onValueChange = { email.value = it })
        FieldTextApp(email.value,"Confirmar contrasenia",onValueChange = { email.value = it })


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

        ButtonApp("Registrarse", onClick = {})

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