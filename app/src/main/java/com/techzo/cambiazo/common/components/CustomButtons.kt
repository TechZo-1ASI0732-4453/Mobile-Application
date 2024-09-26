package com.techzo.cambiazo.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techzo.cambiazo.R

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
fun ButtonIconHeaderApp(iconVector: ImageVector, onClick: () -> Unit){
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
