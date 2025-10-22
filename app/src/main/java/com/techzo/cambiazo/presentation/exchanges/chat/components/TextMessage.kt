package com.techzo.cambiazo.presentation.exchanges.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techzo.cambiazo.domain.Chat

@Composable
fun TextMessage(
    message: Chat,
    currentUserId: String
) {
    val isMine = message.senderId == currentUserId
    val alignment = if (isMine) Arrangement.End else Arrangement.Start
    val bubbleColor = Color(if (isMine) 0xFFFFD146 else 0xFFEDEDED)
    val borderColor = Color(if (isMine) 0xFFFFD146 else 0xFFDCDCDC)

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = alignment
    ) {
        if (!isMine) Spacer(modifier = Modifier.width(2.dp))
        Box(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .shadow(
                    elevation = 2.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = Color(0x14000000)
                )
                .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
                .background(bubbleColor, RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .widthIn(max = 270.dp)
        ) {
//            if (message.latitude != null && message.longitude != null) {
//
//
//            } else {
            Text(
                text = message.content,
                color = Color(0xFF222222),
                fontSize = 16.sp
            )
//            }
        }
        if (isMine) Spacer(modifier = Modifier.width(2.dp))
    }
}