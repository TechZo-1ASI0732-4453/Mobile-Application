package com.techzo.cambiazo.presentation.exchanges.chat.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun LocationMessageItem(
    latitude: Double,
    longitude: Double,
    isMine: Boolean,
    isLoading: Boolean
) {
    val context = LocalContext.current
    val lat =latitude
    val lng = longitude

    val alignment = if (isMine) Arrangement.End else Arrangement.Start
    val color =  Color(if (isMine) 0xFFFFD146 else 0xFFEDEDED)

    val cameraPositionState = rememberCameraPositionState  {
        position = CameraPosition.fromLatLngZoom(LatLng(lat, lng), 15f)
    }

    val markerState = remember { MarkerState(position = LatLng(lat, lng)) }
    if (!isMine) Spacer(modifier = Modifier.width(2.dp))

    Row(  modifier = Modifier
        .fillMaxWidth(),
        horizontalArrangement = alignment){
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .aspectRatio(1.6f)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, color, RoundedCornerShape(16.dp)) // borde
                .clickable() {
                    val uri = Uri.parse("geo:$lat,$lng?q=$lat,$lng")
                    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                        setPackage("com.google.android.apps.maps")
                    }
                    context.startActivity(intent)
                }
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color(0xFFF5F5F5)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            strokeWidth = 3.dp,
                            color = Color(0xFFFFD146),
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "Enviando ubicación...",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            } else {
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
                    Marker(state = markerState)
                }
            }
        }
    }
}




fun parseLocationMessage(content: String): Pair<Double, Double>? {
    if (!content.startsWith("L0C4t10N:")) return null

    val regex = Regex("""latitud\s*:\s*([0-9.-]+)\s*,\s*longitud\s*:\s*([0-9.-]+)""")
    val match = regex.find(content)
    return if (match != null) {
        val lat = match.groupValues[1].toDoubleOrNull()
        val lng = match.groupValues[2].toDoubleOrNull()
        if (lat != null && lng != null) lat to lng else null
    } else null
}