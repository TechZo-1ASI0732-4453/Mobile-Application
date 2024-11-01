package com.techzo.cambiazo.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.zIndex
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun CardApp(padding: PaddingValues, content: @Composable () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White, RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
            .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
    ) {
        Column(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))

                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            content()
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}


@Composable
fun MainScaffoldApp(paddingCard: PaddingValues,
                    bottomBar: @Composable () -> Unit = {},
                    contentsHeader: @Composable () -> Unit = {},
                    profileImage: (@Composable () -> Unit)? = null,
                    content: @Composable () -> Unit = {}
) {
    Scaffold(
        bottomBar = bottomBar,
    ) { paddingValues->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFFFD146)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            contentsHeader()
            profileImage?.let {it()}
            CardApp(paddingCard) {
                content()
            }

        }
    }
}

@Composable
fun ProfileImage(url: String, shape: Shape, size: Dp) {

    Surface(
        modifier = Modifier
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                layout(placeable.width, placeable.height/2) {
                    placeable.placeRelative(0, 0)
                }
            }
            .zIndex(1f)
            .clip(shape)
            .size(size),
        shape = shape,
    ) {
        GlideImage(
            imageModel = { url },
            modifier = Modifier
                .fillMaxWidth()
                .height(size),
            requestOptions = {
                RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
            },
        )
    }
}