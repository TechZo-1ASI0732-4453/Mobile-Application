package com.techzo.cambiazo.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun DialogApp(
    message: String,
    description: String?= null,
    labelButton1: String? = null,
    labelButton2: String?= null,
    onDismissRequest: () -> Unit = {},
    onClickButton1: () -> Unit = {},
    onClickButton2: () -> Unit= {}
) {
    Dialog(onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
        )
    ) {

        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(20.dp)
                .background(Color.White, shape = RoundedCornerShape(25.dp))
                .padding(30.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,

            ) {

            Text(text =message,
                color = Color.Black,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 30.sp,
                    color = Color.Black
                ),
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.padding(8.dp))

            if(description != null) {
                Text(text =description,
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    style = TextStyle(
                        fontSize = 20.sp
                    ),
                    fontWeight = FontWeight.Normal
                )
            }
            Spacer(modifier = Modifier.padding(8.dp))

            if(labelButton1 != null) {
                ButtonApp(text =labelButton1) { onClickButton1()}
            }

            if(labelButton2 != null) {
                ButtonApp(text =labelButton2
                    , bgColor = Color.White
                    , fColor = Color(0xFFFFD146)
                ) { onClickButton2()}
            }

        }
    }
}
