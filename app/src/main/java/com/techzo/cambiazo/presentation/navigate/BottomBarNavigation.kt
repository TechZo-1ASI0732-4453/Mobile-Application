package com.techzo.cambiazo.presentation.navigate


import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomBarNavigation(items: List<ItemsScreens>,currentScreen: String) {

        NavigationBar(
            modifier = Modifier.border(width = 1.dp,color = Color.Gray).height(100.dp),
            containerColor = Color.White,

        ) {

            items.forEach { screen ->
                val isCurrentScreen = currentScreen == screen.route
                val color = if (isCurrentScreen) Color(0xFFFFD146) else Color.Black
                NavigationBarItem(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp)
                        .drawBehind {
                        drawRect(
                            color = if (isCurrentScreen) Color(0xFFFFD146) else Color.Transparent,
                            topLeft = Offset(0f, -20f),
                            size = Size(size.width, 10f),
                        )
                    },
                    alwaysShowLabel = true,
                    selected = false,
                    onClick = { screen.navigate() },
                    icon = {
                        Icon(
                            imageVector =  screen.icon ,
                            contentDescription = screen.title,
                            tint = color,
                            modifier = Modifier.size(35.dp)
                        )
                    },
                    label = {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = screen.title,
                            maxLines = 1,
                            color = color,
                            letterSpacing = 0.001.sp,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                        )
                    },
                )
            }
        }
}