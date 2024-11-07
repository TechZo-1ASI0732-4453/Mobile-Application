package com.techzo.cambiazo.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
fun MainScaffoldApp(
    paddingCard: PaddingValues,
    bottomBar: Pair<@Composable () -> Unit, () -> Unit>? = null,
    contentsHeader: @Composable () -> Unit = {},
    profileImage: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit = {}
) {
    val (bar, newPost) = bottomBar?: Pair(null, {})

    Scaffold(
        floatingActionButton = {
            bottomBar?.let {
                FloatingActionButton(
                    modifier = Modifier
                        .offset(y = (-8).dp)
                        .size(60.dp),
                    containerColor = Color(0xFFFFD146),
                    shape = CircleShape,
                    onClick = {newPost() }) {
                    Icon(
                        modifier = Modifier.size(25.dp),
                        imageVector = Icons.Filled.Add,
                        contentDescription =null,
                        tint = Color.White
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,

    ){
        paddingValues ->
        Box(contentAlignment = Alignment.BottomCenter) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFFFD146)),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                contentsHeader()
                profileImage?.let { it() }
                CardApp(paddingCard) {
                    content()
                }
                Box(Modifier.height(50.dp)) {

                }

            }

            bar?.let { it() }



        }

    }

}

@Composable
fun ProfileImage(url: String, shape: Shape, size: Dp) {

    Surface(
        modifier = Modifier
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                layout(placeable.width, placeable.height / 2) {
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