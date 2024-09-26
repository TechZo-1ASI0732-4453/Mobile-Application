package com.techzo.cambiazo.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CardApp(padding: PaddingValues, content: @Composable () -> Unit = {}) {
    Card(
        modifier = Modifier
            .background(Color.White, RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
            .padding(padding)
    ) {
        Column(
            modifier = Modifier.background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            content()
        }
    }
}


@Composable
fun MainScaffoldApp(paddingCard: PaddingValues,
                    bottomBar: @Composable () -> Unit = {},
                    contentsHeader: @Composable () -> Unit = {},
                    content: @Composable () -> Unit = {},
) {
    Scaffold(
        bottomBar = bottomBar
    ) { paddingValues ->
        val p = paddingValues
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFD146)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(1f))
            contentsHeader()
            Spacer(modifier = Modifier.weight(1f))
            CardApp(paddingCard){
                content()
            }
        }
    }
}