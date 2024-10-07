package com.techzo.cambiazo.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TextToggleButton(
    selected: Boolean,
    onToggle: () -> Unit,
    labelOff: String = "Off",
    labelOn: String = "On"
) {
    Row(
        modifier = Modifier
            .background(Color(0xFFF2F4F5), RoundedCornerShape(12.dp))
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        NewButtonApp(
            labelOn,
            !selected,
            Modifier
                .weight(1f)
            ,onClick = { onToggle() }
        )
        NewButtonApp(
            labelOff,
            selected,
            Modifier
                .weight(1f),

            onClick = { onToggle() }
        )
    }
}

@Composable
fun NewButtonApp(text:String,selected: Boolean, modifier: Modifier = Modifier, onClick: ()-> Unit){
    Button(
        onClick = { onClick() },
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth()
            .height(40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Color(0xFFFFD146) else Color.Transparent,
            contentColor = Color.Black,
        ),
        shape =  RoundedCornerShape(10.dp)
        ,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif)
        )
    }

}