package com.techzo.cambiazo.presentation.auth

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techzo.cambiazo.common.components.ButtonApp
import com.techzo.cambiazo.common.components.ButtonIconHeaderApp
import com.techzo.cambiazo.common.components.MainScaffoldApp

@Composable
fun PhoneInputScreen(
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onBack: () -> Unit
) {
    MainScaffoldApp(
        paddingCard = PaddingValues(horizontal = 27.dp, vertical = 21.dp),
        contentsHeader = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                ButtonIconHeaderApp(
                    iconVector = Icons.Filled.ArrowBack,
                    onClick = onBack,
                    iconSize = 35.dp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Ingresa tu número",
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    color = Color(0xFF333333),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "de teléfono",
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    color = Color(0xFF333333),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    ) {
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = onPhoneNumberChange,
            placeholder = {
                Text(
                    "Número de teléfono",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily.SansSerif
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .clip(RoundedCornerShape(size = 10.dp))
                .border(1.dp, Color.Gray, RoundedCornerShape(10.dp)),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        ButtonApp("Confirmar", onClick = onConfirm)
    }
}
