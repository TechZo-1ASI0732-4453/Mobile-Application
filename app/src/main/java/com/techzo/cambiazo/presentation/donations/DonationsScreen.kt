package com.techzo.cambiazo.presentation.donations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.techzo.cambiazo.common.components.ButtonIconHeaderApp
import com.techzo.cambiazo.common.components.CustomInput
import com.techzo.cambiazo.common.components.MainScaffoldApp
import com.techzo.cambiazo.common.components.TextTitleHeaderApp
import com.techzo.cambiazo.data.remote.donations.OngDto

@Composable
fun DonationScreen(
    back: () -> Unit = {},
    onOngClick: (OngDto) -> Unit = {},
    openDonations: ()-> Unit = {}
) {
    val viewModel: DonationsViewModel = hiltViewModel()
    val state = viewModel.ongs.value
    val search = viewModel.searchQuery.value


    val filteredOngs = state.data?.filter {
        it.name.contains(search, ignoreCase = true)
    } ?: emptyList()

    MainScaffoldApp(
        paddingCard = PaddingValues(top = 20.dp),
        contentsHeader = {
            Column(
                Modifier
                    .padding(bottom = 30.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ButtonIconHeaderApp(Icons.Filled.ArrowBack, onClick = { back() })
                TextTitleHeaderApp("ONGs")
            }
        },
        content = {
            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 5.dp)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    CustomInput(
                        value = search,
                        placeHolder = "Buscar",
                        type = "Text",
                        isError = false,
                        messageError = ""
                    ) {
                        viewModel.onSearchQueryChange(it)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
                when {
                    state.isLoading -> {
                        item { Text("Cargando ONGs...", color = Color.Gray) }
                    }
                    !state.message.isNullOrEmpty() -> {
                        item { Text("Error: ${state.message}", color = Color.Red) }
                    }
                    filteredOngs.isEmpty() -> {
                        item { Text("No se encontraron resultados", color = Color.Gray) }
                    }
                    else -> {
                        items(filteredOngs) { ong ->
                            OngCard(ong = ong, onClick = { onOngClick(ong) })
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun OngCard(
    ong: OngDto,
    onClick: () -> Unit
) {
    val addressParts = ong.address.split(",")
    val distrito = addressParts.getOrNull(1)?.trim() ?: ""
    val ciudad = addressParts.getOrNull(2)?.trim() ?: ""

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF9F9FB)),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        onClick = { onClick() }
    ) {
        Row(
            modifier = Modifier
                .background(Color.White)
                .padding(18.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ong.logo,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .background(Color(0xFFF2F2F2), shape = RoundedCornerShape(18.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = ong.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF222222)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF24A19C),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$distrito, $ciudad",
                        fontSize = 14.sp,
                        color = Color(0xFF8B8B8B),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

