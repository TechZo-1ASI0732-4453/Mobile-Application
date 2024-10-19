package com.techzo.cambiazo.presentation.exchanges

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Tab
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.skydoves.landscapist.glide.GlideImage
import com.techzo.cambiazo.R
import com.techzo.cambiazo.common.components.MainScaffoldApp
import com.techzo.cambiazo.domain.Exchange
import kotlinx.coroutines.launch


@Composable
fun ExchangeScreen(
    bottomBar: @Composable () -> Unit = {}, viewModel: ExchangeViewModel = hiltViewModel(),
    goToDetailsScreen: (String, String) -> Unit,
) {

    val state = viewModel.state.value
    val exchangesSend=viewModel.exchangesSend.value
    val exchangesReceived=viewModel.exchangesReceived.value

    MainScaffoldApp(bottomBar = bottomBar,
        paddingCard = PaddingValues(top = 10.dp),
        contentsHeader = {
            Text(text = "Mis intercambios", fontSize = 32.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 20.dp))
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
            LazyColumn() {
                items(state.data ?: emptyList()) { exchange ->
                    ExchangeBox(exchange, pagerState.currentPage, goToDetailsScreen)
                }
            }
        }

    }
}


@Composable
fun ExchangeBox(exchange: Exchange, page: Int, goToDetailsScreen: (String, String) -> Unit) {
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
                    imageModel = {
                        when (page) {
                            0 -> exchange.userChange.profilePicture
                            1 -> exchange.userOwn.profilePicture
                            else -> exchange.userChange.profilePicture
                        }
                                 },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text =
                        when (page) {
                            0 -> exchange.userChange.name
                            1 -> exchange.userOwn.name
                            else -> exchange.userChange.name
                        }
                    ,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = if (page == 0 && exchange.status == "Pendiente") "Enviado" else exchange.status,
                color = Color.Gray,
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color(0xFFE8E8E8))
                    .padding(horizontal = 20.dp, vertical = 4.dp)
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
                productImageUrl =
                when (page) {
                    0 -> exchange.productChange.image
                    1 -> exchange.productOwn.image
                    else -> exchange.productChange.image
                },
                productName =
                when (page) {
                    0 -> exchange.productChange.name
                    1 -> exchange.productOwn.name
                    else -> exchange.productChange.name
                },
                tag = "Quieres"
            )
            Image(
                painter = painterResource(id = R.drawable.exchange_image),
                contentDescription = "exchange image",
                modifier = Modifier.size(50.dp)
            )
            ExchangeProductCard(
                productImageUrl =
                when (page) {
                    0 -> exchange.productOwn.image
                    1 -> exchange.productChange.image
                    else -> exchange.productOwn.image
                },
                productName =
                when (page) {
                    0 -> exchange.productOwn.name
                    1 -> exchange.productChange.name
                    else -> exchange.productOwn.name
                },
                tag = "Ofreces"
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





