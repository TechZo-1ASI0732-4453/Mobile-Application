package com.techzo.cambiazo.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
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
    isNewReview: Boolean = false,
    onDismissRequest: () -> Unit = {},
    onClickButton1: () -> Unit = {},
    onClickButton2: () -> Unit= {},
    onSubmitReview: (Int, String) -> Unit = { _, _ -> }
) {
    var rating by remember { mutableIntStateOf(0) }
    var review by remember { mutableStateOf("") }

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

            if(isNewReview) {
                OutlinedTextField(
                    value = review,
                    placeholder = {
                        Text("Comentario...", color = Color.Gray,
                            style= MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Normal,
                                fontFamily = FontFamily.SansSerif))
                    },
                    onValueChange = { review = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, Color.Gray, RoundedCornerShape(10.dp)),
                    shape = RoundedCornerShape(10.dp),
                )
                /*
                OutlinedTextField(
                    value = review,
                    onValueChange = {
                        review = it
                    },
                    label = { Text("Escribe tu reseña") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    maxLines = 5
                )

                 */
                Spacer(modifier = Modifier.padding(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(-10.dp)) {
                    repeat(5) { index ->
                        IconButton(
                            onClick = {
                                rating = if (index +1 == rating) 0 else index + 1
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Star,
                                contentDescription = null,
                                tint = if (index < rating) Color(0xFFFFD146) else Color.Gray,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                }
            }

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
                ButtonApp(text =labelButton1) { onClickButton1()
                    if(isNewReview) {
                        onSubmitReview(rating, review)
                    }
                }
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
