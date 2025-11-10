package com.techzo.cambiazo.presentation.exchanges.chat

import android.app.Activity
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skydoves.landscapist.glide.GlideImage
import com.techzo.cambiazo.common.Constants
import com.techzo.cambiazo.common.permissions.Permission
import com.techzo.cambiazo.common.permissions.PermissionViewModel
import com.techzo.cambiazo.common.permissions.rememberPermissionLauncher
import com.techzo.cambiazo.domain.MessageType
import com.techzo.cambiazo.domain.SendStatus
import com.techzo.cambiazo.presentation.exchanges.chat.components.ChatInput
import com.techzo.cambiazo.presentation.exchanges.chat.components.LocationMessageItem
import com.techzo.cambiazo.presentation.exchanges.chat.components.TextMessage
import com.techzo.cambiazo.presentation.exchanges.chat.components.parseLocationMessage
import kotlinx.coroutines.launch

@Composable
private fun SystemNotice(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 6.dp, bottom = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color(0xFF6B7280),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier
                .background(Color(0xFFF0F2F5), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun LocationSharedPlaceholder(
    isMine: Boolean,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val bubbleColor = if (isMine) Color(0xFFDCF8C6) else Color.White
    val borderColor = Color(0x14000000)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(min = 140.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(bubbleColor)
                .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Column {
                Text(
                    text = "Ubicación compartida",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Color(0xFF1F2937)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = if (isLoading) "Enviando…" else "Sin coordenadas disponibles",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF6B7280)
                )
            }
        }
    }
}

@Composable
fun ChatScreen(
    onExit: () -> Unit = {},
    viewModel: ChatViewModel = hiltViewModel(),
    permissionViewModel: PermissionViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as Activity
    val locationLauncher = rememberPermissionLauncher(Permission.LOCATION, permissionViewModel, activity)

    val myUserId = remember { Constants.user?.id?.toString().orEmpty() }
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val inputText by viewModel.inputText

    val isAtBottom by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible >= layoutInfo.totalItemsCount - 2
        }
    }

    LaunchedEffect(messages.size) {
        if (isAtBottom && messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }
    LaunchedEffect(Unit) { viewModel.reconnect() }

    var prevHeightPx by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        containerColor = Color(0xFFF6F7FB),
        topBar = {
            Surface(
                color = Color(0xFFFFD146),
                tonalElevation = 0.dp,
                shadowElevation = 6.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .drawBehind {
                            val y = size.height - 0.5.dp.toPx()
                            drawLine(
                                color = Color(0x33000000),
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = 0.5.dp.toPx()
                            )
                        }
                ) {
                    Box(
                        Modifier
                            .matchParentSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.10f),
                                        Color.Transparent,
                                        Color.Transparent
                                    ),
                                    startY = 0f,
                                    endY = Float.POSITIVE_INFINITY
                                )
                            )
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color.Black.copy(alpha = 0.04f),
                            shape = CircleShape,
                            tonalElevation = 0.dp,
                            shadowElevation = 0.dp
                        ) {
                            IconButton(
                                onClick = onExit,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Salir",
                                    tint = Color(0xFF222222)
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .size(46.dp)
                                .clip(CircleShape)
                                .border(
                                    width = 1.5.dp,
                                    color = Color(0xFF222222).copy(alpha = 0.10f),
                                    shape = CircleShape
                                )
                        ) {
                            GlideImage(
                                imageModel = { viewModel.getPeerPhoto() },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Text(
                            text = viewModel.getPeerName(),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF222222),
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                ChatInput(
                    inputText = inputText,
                    onInputChange = { viewModel.onInputChange(it) },
                    context = context,
                    onSend = { viewModel.send(inputText.text.trim()) },
                    stateViewModel = permissionViewModel,
                    permissionLauncher = locationLauncher,
                    sendLocation = { viewModel.sendLocationMessage(activity) },
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
                .onSizeChanged { size ->
                    val newH = size.height
                    val oldH = prevHeightPx
                    if (oldH != null) {
                        val delta = oldH - newH
                        if (delta != 0) {
                            scope.launch {
                                if (delta > 0) {
                                    listState.scrollBy(delta.toFloat())
                                } else {
                                    if (messages.isNotEmpty()) {
                                        listState.scrollToItem(messages.lastIndex)
                                    }
                                }
                            }
                        }
                    }
                    prevHeightPx = newH
                },
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 15.dp),
            state = listState
        ) {
            item(key = "system_notice_started") {
                AnimatedVisibility(
                    visible = messages.isEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    SystemNotice("Conversación iniciada")
                }
            }

            items(messages, key = { it.localId }) { message ->
                if (message.type == MessageType.LOCATION) {

                    val lat = message.latitude
                    val lng = message.longitude
                    if (lat != null && lng != null) {
                        LocationMessageItem(
                            latitude = lat,
                            longitude = lng,
                            isMine = message.isMine,
                            isLoading = message.status == SendStatus.SENDING
                        )
                        Log.d("Chat", "Location msg -> lat=${lat}, lng=${lng}")
                    } else {
                        LocationSharedPlaceholder(
                            isMine = message.isMine,
                            isLoading = message.status == SendStatus.SENDING
                        )
                    }
                } else {
                    TextMessage(
                        message = message,
                        currentUserId = myUserId
                    )
                }
            }
        }
    }
}