package com.techzo.cambiazo.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.techzo.cambiazo.R

@Composable
fun LoginScreen(openRegister: () -> Unit = {},
                openApp: () -> Unit = {},
                openForgotPassword: () -> Unit = {}){

    val email = remember {
        mutableStateOf("")
    }

    val password = remember {
        mutableStateOf("")
    }

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


        FieldTextApp(email.value,"Correo electrónico",onValueChange = { email.value = it })

        FieldTextApp(password.value,"Contrasenia",onValueChange = { password.value = it })

        TextLink("","Olvidé mi contraseña", clickable = {openForgotPassword()},Arrangement.End)

        ButtonApp("Iniciar Sesion", onClick = {openApp()})


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

@Composable
fun LoginGoogleApp(){
    Surface(
        modifier = Modifier
            .size(56.dp)
            .clickable(onClick = {}),
        shape = CircleShape,
        shadowElevation = 5.dp
    ) {
        Image(
            painter = painterResource(R.drawable.logo_gmail),
            contentDescription = "logo gmail",

            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(15.dp),
        )
    }
}

@Composable
fun FieldTextApp(valueText:String,text:String,onValueChange: (String) -> Unit){

    OutlinedTextField(
        value = valueText,
        placeholder = {
            Text(text, color = Color.Gray,
                style= MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily.SansSerif))
        },
        onValueChange = { onValueChange(it) },
        modifier = Modifier
            .padding(bottom = 10.dp, top = 10.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color.Gray, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
    )
}

@Composable
fun ButtonApp(text:String,onClick: ()-> Unit){
    Button(
        onClick = { onClick() },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp, top = 10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFFD146),
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(10.dp),
    ) {
        Text(
            text = text,
            fontSize = 17.sp,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif)
        )
    }

}
@Composable
fun ButtonIconHeaderApp(iconVector: ImageVector,onClick: () -> Unit){
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        IconButton(
            onClick = { onClick() },
            modifier = Modifier.align(Alignment.Start)
        ) {
            Icon(
                imageVector = iconVector,
                contentDescription = "Volver",
            )
        }
    }
}

@Composable
fun TextLink(text1:String = "",
             text2:String = "",
             clickable: () -> Unit,
             horizontal: Arrangement.Horizontal = Arrangement.Center){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        horizontalArrangement = horizontal,

        ) {
        Text(
            text = text1,
            style= MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif)
        )

        Text(
            text = text2,
            color = Color(0xFFFFD146),
            modifier = Modifier
                .clickable {clickable()  },
            style= MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif)
        )

    }
}

@Composable
fun CardApp(padding: PaddingValues,content: @Composable () -> Unit = {}) {
    Card(
        modifier = Modifier
            .background(Color.White, RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
            .padding(padding)
    ) {
        Column(
            modifier = Modifier.background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            content()
        }
    }
}


@Composable
fun MainScaffoldApp( paddingCard: PaddingValues,
                     contentsHeader: @Composable () -> Unit = {},
                     content: @Composable () -> Unit = {},
) {
    Scaffold { paddingValues ->
        val p = paddingValues
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFD146)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(1f))
            contentsHeader()
            Spacer(modifier = Modifier.weight(1f))
            CardApp(paddingCard){
                content()
            }
        }
    }
}

@Composable
fun TextTitleHeaderApp(text: String){
    Text(
        text = text,
        fontSize = 35.sp,
        style = MaterialTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif
        ),
    )
}