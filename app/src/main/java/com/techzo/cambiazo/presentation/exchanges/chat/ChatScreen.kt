package com.techzo.cambiazo.presentation.exchanges.chat

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.techzo.cambiazo.common.Constants
import com.techzo.cambiazo.common.permissions.Permission
import com.techzo.cambiazo.common.permissions.PermissionViewModel
import com.techzo.cambiazo.common.permissions.rememberPermissionLauncher
import com.techzo.cambiazo.domain.SendStatus
import com.techzo.cambiazo.presentation.exchanges.chat.components.ChatInput
import com.techzo.cambiazo.presentation.exchanges.chat.components.LocationMessageItem
import com.techzo.cambiazo.presentation.exchanges.chat.components.TextMessage
import com.techzo.cambiazo.presentation.exchanges.chat.components.parseLocationMessage

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
    var inputText by remember { mutableStateOf(TextFieldValue("")) }

    val isAtBottom by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible >= layoutInfo.totalItemsCount - 2
        }
    }

    LaunchedEffect(messages.size) {
        if (isAtBottom && messages.isNotEmpty()) listState.animateScrollToItem(messages.lastIndex)
    }
    LaunchedEffect(Unit) { viewModel.reconnect() }

    Scaffold(
        containerColor = Color(0xFFF6F7FB),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFD146))
                    .statusBarsPadding()
                    .padding(vertical = 14.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onExit,
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .size(36.dp)
                        .background(Color.Transparent, CircleShape)
                ) {
                    Icon(Icons.Default.ArrowBack, "Salir", tint = Color(0xFF222222))
                }
                Text(
                    text = "Chat",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
        },
        bottomBar = {
            ChatInput(
                inputText = inputText,
                onInputChange = { inputText = it },
                context = context,
                onSend = { viewModel.send(inputText.text) },
                stateViewModel = permissionViewModel,
                permissionLauncher = locationLauncher,
                sendLocation = { viewModel.sendLocationMessage(activity) }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 10.dp,
                    end = 10.dp,
                    top = innerPadding.calculateTopPadding() + 10.dp,
                    bottom = innerPadding.calculateBottomPadding()
                ),
            verticalArrangement = Arrangement.Top,
            state = listState
        ) {
            items(messages, key = { it.localId }) { message ->
                val parsedLocation = parseLocationMessage(message.content)
                if (parsedLocation == null) {
                    TextMessage(
                        message = message,
                        currentUserId = myUserId
                    )
                } else {
                    val (lat, lng) = parsedLocation
                    LocationMessageItem(
                        latitude = lat,
                        longitude = lng,
                        isMine = message.isMine,
                        isLoading = message.status == SendStatus.SENDING
                    )
                }
            }
        }
    }
}