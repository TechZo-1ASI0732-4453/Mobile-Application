package com.techzo.cambiazo.presentation.exchanges

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.skydoves.landscapist.glide.GlideImage
import com.techzo.cambiazo.R
import com.techzo.cambiazo.common.Constants
import com.techzo.cambiazo.common.components.EmptyStateMessage
import com.techzo.cambiazo.common.components.MainScaffoldApp
import com.techzo.cambiazo.common.components.TextTitleHeaderApp
import com.techzo.cambiazo.domain.Exchange
import kotlinx.coroutines.launch


@Composable
fun ExchangeScreen(
    bottomBar: @Composable () -> Unit = {}, viewModel: ExchangeViewModel = hiltViewModel(),
    goToDetailsScreen: (String, String) -> Unit,
) {

    val state = viewModel.state.value
    //val exchangesSend=viewModel.exchangesSend.value
    //val exchangesReceived=viewModel.exchangesReceived.value

    MainScaffoldApp(bottomBar = bottomBar,
        paddingCard = PaddingValues(top = 10.dp),
        contentsHeader = {
            Spacer(modifier = Modifier.height(30.dp))
            TextTitleHeaderApp(text ="Mis intercambios")
            Spacer(modifier = Modifier.height(30.dp))
        }) {
        val pagerState = rememberPagerState(
            pageCount = { 3 }, initialPage = 0
        )


        val coroutineScope = rememberCoroutineScope()

        Row(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(20.dp)
                )
                .background(Color(0xFFE8E8E8))
        ) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                    viewModel.getExchangesByUserOwnId()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (pagerState.currentPage == 0) Color(0xFFFFD146) else Color.Transparent
                )
            ) {
                Text(
                    text = "Enviados",
                    color = if (pagerState.currentPage == 0) Color.Black else Color.Gray
                )
            }
            Button(
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                    viewModel.getExchangesByUserChangeId()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (pagerState.currentPage == 1) Color(0xFFFFD146) else Color.Transparent
                )
            ) {
                Text(
                    text = "Recibidos",
                    color = if (pagerState.currentPage == 1) Color.Black else Color.Gray
                )
            }
            Button(
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(2)
                    }
                    viewModel.getFinishedExchanges()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (pagerState.currentPage == 2) Color(0xFFFFD146) else Color.Transparent
                )
            ) {
                Text(
                    text = "Finalizados",
                    color = if (pagerState.currentPage == 2) Color.Black else Color.Gray
                )
            }
        }

        HorizontalPager(
            state = pagerState, userScrollEnabled = false
        ) {
            if (state.isLoading) {
                EmptyStateMessage(
                    icon = Icons.Default.Info,
                    message = "Cargando...",
                    subMessage = "Por favor espere un momento",
                    modifier = Modifier.padding(20.dp)
                )
            } else if (state.data.isNullOrEmpty()) {
                EmptyStateMessage(
                    icon = Icons.Default.Info,
                    message = "No hay intercambios",
                    subMessage = "No tienes intercambios en esta sección",
                    modifier = Modifier.padding(20.dp)
                )
            } else {
                LazyColumn {
                    items(state.data ?: emptyList()) { exchange ->
                        ExchangeBox(exchange, pagerState.currentPage, goToDetailsScreen)
                    }
                }
            }
        }

    }
}


@Composable
fun ExchangeBox(exchange: Exchange, page: Int, goToDetailsScreen: (String, String) -> Unit) {
    val boolean = exchange.userOwn.id == Constants.user?.id

    val textUpperImage = when (page) {
        0 -> "Quieres"
        1 -> "Quiere"
        else -> "Cambiaste"
    }

    val textUpperImage2 = when (page) {
        0 -> "Ofreces"
        1 -> "Ofrece"
        else -> "Obtuviste"
    }

    val upperUserProfileImage= when (page) {
        0 -> exchange.userChange.profilePicture
        1 -> exchange.userOwn.profilePicture
        else -> if(boolean) exchange.userChange.profilePicture else exchange.userOwn.profilePicture
    }

    val upperUserName= when (page) {
        0 -> exchange.userChange.name
        1 -> exchange.userOwn.name
        else -> if(boolean) exchange.userChange.name else exchange.userOwn.name
    }

    val statusText = {
        if (page == 0 && exchange.status == "Pendiente") "Enviado"
        else if (page == 1) exchange.status
        else "WhatsApp"
    }

    val statusColor= {
        if (page == 0 && exchange.status == "Pendiente") Color.Gray
        else if(page==1) Color(0xFFFFD146)
        else Color.White
    }

    val statusBackgroundColor= {
        if (page == 0 && exchange.status == "Pendiente") Color(0xFFE8E8E8)
        else if(page==1) Color.Black
        else Color(0xFF38B000)
    }

    val firstProductImage= when (page) {
        0 -> exchange.productChange.image
        1 -> exchange.productChange.image
        else -> if(boolean) exchange.productOwn.image else exchange.productChange.image
    }

    val secondProductImage= when (page) {
        0 -> exchange.productOwn.image
        1 -> exchange.productOwn.image
        else -> if(boolean) exchange.productChange.image else exchange.productOwn.image
    }

    val firstProductName= when (page) {
        0 -> exchange.productChange.name
        1 -> exchange.productChange.name
        else -> if(boolean) exchange.productOwn.name else exchange.productChange.name
    }

    val secondProductName= when (page) {
        0 -> exchange.productOwn.name
        1 -> exchange.productOwn.name
        else -> if(boolean) exchange.productChange.name else exchange.productOwn.name
    }


    Column(Modifier.clickable { goToDetailsScreen(exchange.id.toString(), page.toString()) }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                GlideImage(
                    imageModel = { upperUserProfileImage },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = upperUserName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = statusText(),
                color = statusColor(),
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(statusBackgroundColor())
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                fontWeight = FontWeight.Bold
            )

        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(9.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ExchangeProductCard(
                productImageUrl = firstProductImage,
                productName = firstProductName,
                tag = textUpperImage
            )
            Image(
                painter = painterResource(id = R.drawable.exchange_image),
                contentDescription = "exchange image",
                modifier = Modifier.size(50.dp)
            )
            ExchangeProductCard(
                productImageUrl = secondProductImage,
                productName = secondProductName,
                tag = textUpperImage2
            )
        }
        HorizontalDivider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(20.dp))
    }
}

@Composable
fun ExchangeProductCard(productImageUrl: String, productName: String, tag: String) {
    Column(modifier = Modifier.width(150.dp)) {
        Text(
            text = tag,
            color = Color(0xFF6D6D6D),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        GlideImage(
            imageModel = { productImageUrl },
            modifier = Modifier
                .height(150.dp)
                .clip(RoundedCornerShape(10, 10, 0, 0))
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(0, 0, 20, 20),
                    clip = true
                )
                .background(color = Color.White, shape = RoundedCornerShape(0, 0, 10, 10))
        ) {
            Text(
                text = productName,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}





