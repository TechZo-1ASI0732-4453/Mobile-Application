package com.techzo.cambiazo.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techzo.cambiazo.R

@Composable
fun LoginGoogleApp(onClick: () -> Unit){
    Surface(
        modifier = Modifier
            .size(56.dp).shadow(2.dp, CircleShape)
            .border(0.5.dp, Color(0xFFF2F2F2), CircleShape)
            .clip(CircleShape)
            .clickable(onClick = onClick),
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
fun ButtonApp(
    text: String,
    bgColor: Color = Color(0xFFFFD146),
    fColor: Color = Color.Black,
    bColor: Color = Color(0xFFFFD146),
    enable: Boolean = true,
    isLoading: Boolean = false,
    onClick: () -> Unit
) {
    Button(
        onClick = { onClick() },
        modifier = Modifier
            .fillMaxWidth().height(65.dp)
            .padding(bottom = 10.dp, top = 10.dp)
            .border(1.5.dp, color = if (enable) bColor else Color.Transparent, RoundedCornerShape(10.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = bgColor,
            contentColor = fColor,
        ),
        enabled = enable && !isLoading,
        shape = RoundedCornerShape(10.dp),
    ) {
        if (isLoading) {
            androidx.compose.material3.CircularProgressIndicator(
                color = Color(0xFFFFD146),
                strokeWidth = 3.dp,
                modifier = Modifier.size(28.dp)
            )
        } else {
            Text(
                text = text,
                fontSize = 18.sp,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )
            )
        }
    }
}

@Composable
fun ButtonIconHeaderApp(iconVector: ImageVector, onClick: () -> Unit, iconSize: Dp = 35.dp){
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        IconButton(
            onClick = { onClick() },
            modifier = Modifier.align(Alignment.Start).padding(start = 15.dp)
        ) {
            Icon(
                imageVector = iconVector,
                contentDescription = "Volver",
                modifier = Modifier.size(iconSize)
            )
        }
    }
}


@Composable
fun FloatingButtonApp(text: String,modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier
            .padding(vertical = 5.dp)
            .fillMaxWidth()
            .height(65.dp)
            .padding(bottom = 10.dp, top = 10.dp)
            .border(1.5.dp, color = Color(0xFFFFD146), RoundedCornerShape(10.dp)),
        containerColor = Color(0xFFFFD146),
        contentColor = Color.Black,
        shape = RoundedCornerShape(10.dp),
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif)
        )
    }
}

