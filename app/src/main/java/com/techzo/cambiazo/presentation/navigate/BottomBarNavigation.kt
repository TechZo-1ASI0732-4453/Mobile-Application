package com.techzo.cambiazo.presentation.navigate


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomBarNavigation(items: List<ItemsScreens>,currentScreen: String) {


        NavigationBar(
            modifier = Modifier
                .height(90.dp)
                .shadow(elevation = 15.dp,
                    spotColor = Color.Black,
                    ambientColor = Color.Black,
                    clip = true
                ),
            containerColor = Color.White,

        ) {

            items.forEach { screen ->
                val isCurrentScreen = currentScreen == screen.route
                val color = if (isCurrentScreen) Color(0xFFFFD146) else Color.Gray.copy(alpha = 1f)
                val icon = if (isCurrentScreen) screen.iconSelected else screen.icon

                NavigationBarItem(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .drawBehind {
                            drawRect(
                                color = if (isCurrentScreen) Color(0xFFFFD146) else Color.Transparent,
                                topLeft = Offset(0f, -22f),
                                size = Size(size.width, 10f),
                            )
                        },
                    alwaysShowLabel = isCurrentScreen,
                    selected = false,
                    onClick = { screen.navigate() },
                    icon = {
                        Icon(
                            imageVector = icon,
                            contentDescription = screen.title,
                            tint = color,
                            modifier = Modifier.size(30.dp)
                        )
                    },
                    label = {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = screen.title,
                            fontSize = 11.sp,
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
