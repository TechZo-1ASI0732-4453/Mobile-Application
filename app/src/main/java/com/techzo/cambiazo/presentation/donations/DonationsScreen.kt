package com.techzo.cambiazo.presentation.donations

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Upcoming
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.techzo.cambiazo.common.components.EmptyStateMessage
import com.techzo.cambiazo.common.components.MainScaffoldApp
import com.techzo.cambiazo.common.components.TextTitleHeaderApp

@Composable
fun DonationsScreen(
    bottomBar: @Composable () -> Unit = {},
    ) {
    MainScaffoldApp(
        bottomBar = bottomBar,
        paddingCard = PaddingValues(start = 15.dp, end = 15.dp),
        contentsHeader = {
            Spacer(modifier = Modifier.height(30.dp))
            TextTitleHeaderApp(text ="Haz tu donación")
            Spacer(modifier = Modifier.height(30.dp))
        }
    ){
        EmptyStateMessage(
            icon = Icons.Default.Upcoming,
            message = "Proximamente...",
            subMessage = "",
            modifier = Modifier.padding(top = 200.dp)
        )
    }
}