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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

    val shape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = if (isMine) 16.dp else 4.dp,
        bottomEnd  = if (isMine) 4.dp  else 16.dp
    )


    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = alignment
    ) {
        if (!isMine) Spacer(Modifier.width(2.dp))

        Box(
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 0.dp)
                .shadow(
                    elevation = 3.dp,
                    shape = shape,
                    spotColor = Color(0x22000000)
                )
                .border(1.dp, borderColor.copy(alpha = 0.9f), shape)
                .clip(shape)
                .background(bubbleColor)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = message.content,
                color = Color(0xFF222222),
                fontSize = 16.sp,
                lineHeight = 20.sp
            )
        }

        if (isMine) Spacer(Modifier.width(2.dp))
    }
}