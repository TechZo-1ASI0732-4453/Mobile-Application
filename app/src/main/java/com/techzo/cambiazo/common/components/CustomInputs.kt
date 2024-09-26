package com.techzo.cambiazo.common.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

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