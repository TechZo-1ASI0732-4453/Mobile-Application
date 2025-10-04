package com.techzo.cambiazo.presentation.exchanges.chat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.ui.draw.clip
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.techzo.cambiazo.common.permissions.Permission
import com.techzo.cambiazo.common.permissions.PermissionViewModel
import com.techzo.cambiazo.common.permissions.SettingsPermissionDialog
import com.techzo.cambiazo.common.permissions.rememberPermissionLauncher


@Composable
fun ChatScreen(
    onExit: () -> Unit = {},
    viewModel: ChatViewModel = hiltViewModel(),
    permissionViewModel: PermissionViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val activity = context as Activity
    val locationLauncher = rememberPermissionLauncher(Permission.LOCATION, permissionViewModel, context as Activity)



    val backgroundColor = Color(0xFFF6F7FB)
    val bubbleMine = Color.White
    val bubbleOther = Color(0xFFEDEDED)
    val borderMine = Color(0xFFFFD146)
    val borderOther = Color(0xFFDCDCDC)
    val shadowColor = Color(0x14000000)

    val messages = viewModel.messages
    val listState = rememberLazyListState()
    var inputText by remember { mutableStateOf(TextFieldValue("")) }


    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }


    Scaffold(
        containerColor = backgroundColor,
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
                context,
                onSend = {
                    viewModel.sendTextMessage(inputText.text)
                },
                permissionViewModel,
                permissionLauncher = locationLauncher,
                checkLocationEnable = {viewModel.checkLocationSettings(activity)}
            )
        }
    ) { innerPadding ->
        // El LazyColumn ahora se ajusta con el padding del scaffold
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
            state = listState,
            ) {
            items(messages) { message ->

                when (message.type) {
                    MessageType.TEXT -> ChatBubbleV2(
                        message = message,
                        bubbleMine = borderMine,
                        bubbleOther = bubbleOther,
                        borderMine = borderMine,
                        borderOther = borderOther,
                        shadowColor = shadowColor
                    )
                    MessageType.LOCATION -> LocationMessageItem(
                        message,
                        bubbleMine = borderMine,
                        bubbleOther = bubbleOther,
                        borderMine = borderMine,
                        borderOther = borderOther,
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubbleV2(
    message: ChatMessage,
    bubbleMine: Color,
    bubbleOther: Color,
    borderMine: Color,
    borderOther: Color,
    shadowColor: Color
) {
    val isMine = message.isSentByMe
    val alignment = if (isMine) Arrangement.End else Arrangement.Start
    val bubbleColor = if (isMine) bubbleMine else bubbleOther
    val borderColor = if (isMine) borderMine else borderOther

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = alignment
    ) {
        if (!isMine) Spacer(modifier = Modifier.width(2.dp))
        Box(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .shadow(
                    elevation = 2.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = shadowColor
                )
                .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
                .background(bubbleColor, RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .widthIn(max = 270.dp)
        ) {
            if (message.latitude != null && message.longitude != null) {


            } else {
                Text(
                    text = message.text,
                    color = Color(0xFF222222),
                    fontSize = 16.sp
                )
            }
        }
        if (isMine) Spacer(modifier = Modifier.width(2.dp))
    }
}

@Composable
fun ChatInput(
    inputText: TextFieldValue,
    onInputChange: (TextFieldValue) -> Unit,
    context: Context,
    onSend: () -> Unit,
    stateViewModel: PermissionViewModel,
    permissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
    checkLocationEnable: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                stateViewModel.refreshPermission(Permission.LOCATION, context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = inputText,
            onValueChange = onInputChange,
            placeholder = { Text("Escribe un mensaje...") },
            modifier = Modifier
                .weight(1f)
                .background(Color.White, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color(0xFFDCDCDC),
                unfocusedBorderColor = Color(0xFFDCDCDC)
            ),
            maxLines = 3
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            modifier = Modifier.size(44.dp).background(Color(0xFFFFD146), CircleShape)
                .border(1.dp, Color(0xFFDCDCDC), CircleShape),
            onClick = {
                when {
                    stateViewModel.isLocationGranted -> {
                        checkLocationEnable()
                    }
                    stateViewModel.isLocationDenied -> {
                        permissionLauncher.launch(Permission.LOCATION.permission)
                    }
                    stateViewModel.isLocationPermanentlyDenied ->{
                        showDialog = true
                    }
                }
            }
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Enviar ubicación",
                tint = Color.Black
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            modifier = Modifier
                .size(44.dp)
                .background(Color(0xFFFFD146), CircleShape)
                .border(1.dp, Color(0xFFDCDCDC), CircleShape),
            onClick = onSend
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Enviar",
                tint = Color.Black
            )
        }
    }

    SettingsPermissionDialog(showDialog,Permission.LOCATION,context) {
        showDialog = false
    }
}


@SuppressLint("MissingPermission")
fun getCurrentLocation(context: Context, onLocationReceived: (Double, Double) -> Unit) {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val lat = location.latitude
            val lon = location.longitude

            onLocationReceived(lat, lon)

            locationManager.removeUpdates(this)
        }
    }

    locationManager.requestLocationUpdates(
        LocationManager.GPS_PROVIDER,
        0L,
        0f,
        locationListener
    )
}


@Composable
fun LocationMessageItem(
    message: ChatMessage,
    modifier: Modifier = Modifier,
    bubbleMine: Color,
    bubbleOther: Color,
    borderMine: Color,
    borderOther: Color,
) {
    val context = LocalContext.current
    val lat = message.latitude ?: return
    val lng = message.longitude ?: return

    val isMine = message.isSentByMe
    val alignment = if (isMine) Arrangement.End else Arrangement.Start
    val bubbleColor = if (isMine) bubbleMine else bubbleOther
    val borderColor = if (isMine) borderMine else borderOther

    val cameraPositionState = rememberCameraPositionState  {
        position = CameraPosition.fromLatLngZoom(LatLng(lat, lng), 15f)
    }

    val markerState = remember { MarkerState(position = LatLng(lat, lng)) }
    if (!isMine) Spacer(modifier = Modifier.width(2.dp))

    Row(  modifier = Modifier
        .fillMaxWidth(),
        horizontalArrangement = alignment){
    Box(
        modifier = modifier
            .fillMaxWidth(0.7f) // similar a burbuja de WhatsApp
            .aspectRatio(1.6f)  // rectangular
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, bubbleColor, RoundedCornerShape(16.dp)) // borde
            .clickable {
                // Abrir Google Maps externo
                val uri = Uri.parse("geo:$lat,$lng?q=$lat,$lng")
                val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                    setPackage("com.google.android.apps.maps")
                }
                context.startActivity(intent)
            }
    ) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                scrollGesturesEnabled = false,
                zoomGesturesEnabled = false,
                tiltGesturesEnabled = false,
            )
        ) {
            Marker( state = markerState)
        }
    }
}
}
