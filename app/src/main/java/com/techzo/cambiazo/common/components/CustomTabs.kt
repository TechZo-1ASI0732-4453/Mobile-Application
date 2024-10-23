package com.techzo.cambiazo.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun CustomTabs(selectedTabIndex: Int,itemTabs: List<String> ,onTabSelected: (Int) -> Unit) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(16.dp)),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            itemTabs.forEachIndexed { index, title ->
                val isSelected = selectedTabIndex == index
                Button(
                    onClick = { onTabSelected(index) },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Color(0xFFFFD146) else Color(0xFFF0F0F0),
                        contentColor = if (isSelected) Color.Black else Color.Gray
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.5.dp)
                        .padding(vertical = 3.dp)
                        .height(48.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                    )
                }
            }
        }
    }
}