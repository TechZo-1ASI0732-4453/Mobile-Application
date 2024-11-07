package com.techzo.cambiazo.common.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TextLink(text1:String = "",
             text2:String = "",
             clickable: () -> Unit,
             horizontal: Arrangement.Horizontal = Arrangement.Center){
    Row(
        modifier = Modifier
            .fillMaxWidth(),
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
fun TextTitleHeaderApp(text: String){
    Text(
        text = text,
        fontSize = 30.sp,
        style = MaterialTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif
        ),
    )
}

@Composable
fun SubTitleText(subTittle: String){
    Box( modifier = Modifier.fillMaxWidth()) {
        Text(text = subTittle,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 2.dp))
    }
}


@Composable
fun PaymentText(subTittle: String){
    Box( modifier = Modifier) {
        Text(text = subTittle,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 2.dp))
    }
}