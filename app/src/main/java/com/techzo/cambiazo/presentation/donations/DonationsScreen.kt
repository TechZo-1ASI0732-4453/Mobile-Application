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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
    onOngClick: (OngDto) -> Unit = {}
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
                TextTitleHeaderApp("Haz tu Donación")
            }
        },
        content = {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)) {
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

                when {
                    state.isLoading -> {
                        Text("Cargando ONGs...", color = Color.Gray)
                    }

                    !state.message.isNullOrEmpty() -> {
                        Text("Error: ${state.message}", color = Color.Red)
                    }

                    filteredOngs.isEmpty() -> {
                        Text("No se encontraron resultados", color = Color.Gray)
                    }

                    else -> {
                        filteredOngs.forEach { ong ->
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        onClick = { onClick() }
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(15.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = ong.logo,
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.LightGray, shape = RoundedCornerShape(10.dp))
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = ong.type,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = ong.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    Text(
                        text = ong.address,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(180f),
                    tint = Color(0xFFFFD146)
                )
            }
        }
    }
}
