package com.techzo.cambiazo.presentation.navigate

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BottomBarNavigation(items: List<ItemsScreens>) {

    BottomAppBar(
        modifier = Modifier
            .border(width = 1.dp, color = Color.Gray),
        containerColor = Color.White,
    ) {
        NavigationBar(
            modifier = Modifier.background(Color.White),
            containerColor = Color.White,
        ) {
            items.forEach { screen ->
                NavigationBarItem(
                    selected = false,
                    onClick = { screen.navigate() },
                    icon = {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = screen.title,
                            tint = Color.Black,
                            modifier = Modifier
                                .size(35.dp)
                        )
                    }
                )
            }
        }
    }
}