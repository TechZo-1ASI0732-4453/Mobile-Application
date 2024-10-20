package com.techzo.cambiazo.presentation.exchanges

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.skydoves.landscapist.glide.GlideImage
import com.techzo.cambiazo.common.components.MainScaffoldApp

@Composable
fun ExchangeDetailsScreen(goBack: () -> Unit, viewModel: ExchangeViewModel = hiltViewModel(), exchangeId:Int, page: Int) {

    LaunchedEffect(Unit) {
        viewModel.getExchangeById(exchangeId)
    }

    val exchange = viewModel.exchange.value


    MainScaffoldApp(
        paddingCard = PaddingValues(top = 10.dp),
        contentsHeader = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 0.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { goBack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }

                Text(
                    text = "Detalle del\nIntercambio",
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 16.dp)
                )
                Spacer(modifier = Modifier.width(48.dp))
            }
        }
    ) {
        exchange.data?.let{ exchange ->
            val  profilePicture= when (page) {
                0 -> exchange.userChange.profilePicture
                1 -> exchange.userOwn.profilePicture
                else -> exchange.userChange.profilePicture
            }

            val userName= when (page) {
                0 -> exchange.userChange.name
                1 -> exchange.userOwn.name
                else -> exchange.userChange.name
            }

            val productName= when (page) {
                0 -> exchange.productChange.name
                1 -> exchange.productOwn.name
                else -> exchange.productChange.name
            }

            val productName2= when (page) {
                0 -> exchange.productOwn.name
                1 -> exchange.productChange.name
                else -> exchange.productOwn.name
            }

            val price= when (page) {
                0 -> exchange.productChange.price
                1 -> exchange.productOwn.price
                else -> exchange.productChange.price
            }

            val price2= when (page) {
                0 -> exchange.productOwn.price
                1 -> exchange.productChange.price
                else -> exchange.productOwn.price
            }

            val status= if (page == 0 && exchange.status == "Pendiente") "Enviado"
            else if(page==1) exchange.status
            else "WhatsApp"

            val description= when (page) {
                0 -> exchange.productChange.description
                1 -> exchange.productOwn.description
                else -> exchange.productChange.description
            }

            val location= when (page) {
                0 -> viewModel.getLocationString(exchange.productChange.districtId)
                1 -> viewModel.getLocationString(exchange.productOwn.districtId)
                else -> viewModel.getLocationString(exchange.productChange.districtId)
            }

            val productImage= when (page) {
                0 -> exchange.productChange.image
                1 -> exchange.productOwn.image
                else -> exchange.productChange.image
            }

            val productImage2= when (page) {
                0 -> exchange.productOwn.image
                1 -> exchange.productChange.image
                else -> exchange.productOwn.image
            }

            val textUnderImage = when (page) {
                0 -> "Quieres"
                1 -> "Ofrece"
                else -> null
            }

            val textUnderImage2 = when (page) {
                0 -> "Ofreces"
                1 -> "Quiere"
                else -> "Hiciste cambio por:"
            }


            Column{
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        GlideImage(
                            imageModel = { profilePicture},
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = userName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = status,
                        color =
                        if (page == 0 && exchange.status == "Pendiente") Color.Gray
                        else if(page==1) Color(0xFFFFD146)
                        else Color.White,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(
                                if (page == 0 && exchange.status == "Pendiente") Color(0xFFE8E8E8)
                                else if(page==1) Color.Black
                                else Color(0xFF38B000)
                            )
                            .padding(horizontal = 20.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(modifier = Modifier.fillMaxWidth()) {
                    GlideImage(
                        imageModel = {productImage},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .height(250.dp)
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(20.dp)
                            .background(
                                Color.Black.copy(alpha = 0.7f), RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "S/${price} valor aprox.",
                            color = Color(0xFFFFD146),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
                Box(modifier=Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 10.dp)){
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)){
                        if(textUnderImage!=null){
                            Text(textUnderImage)
                        }
                        Text(productName, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = "Ubicación",
                                tint = Color(0xFFFFD146)
                            )
                            Text(
                                text = location,
                                color = Color(0xFF9F9C9C),
                                modifier = Modifier.padding(start = 1.dp)
                            )
                        }
                        Text(description)
                    }
                }
                HorizontalDivider()
                BoxUnderExchange(textUnderImage2,productImage2, productName2, price2.toString(), page)
            }
        }
    }
}

@Composable
fun BoxUnderExchange(textUnderImage:String,image:String, productName: String, price: String, page: Int){
    if(page == 0){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(10.dp)
                .clip(RoundedCornerShape(10.dp))
                .padding(10.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(textUnderImage)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    GlideImage(
                        imageModel = { image },
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .height(150.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.fillMaxWidth(1f).padding(horizontal = 5.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(productName, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("S/${price} valor aprox.", textAlign = TextAlign.Center, fontSize = 16.sp, color = Color(0xFFFFD146), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
    if(page == 1){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(10.dp)
                .clip(RoundedCornerShape(10.dp))

        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(textUnderImage, modifier = Modifier.padding(5.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlideImage(
                        imageModel = { image },
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .height(100.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.padding(8.dp),horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(productName, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("S/${price} valor aprox.", textAlign = TextAlign.Center, fontSize = 16.sp, color = Color(0xFFFFD146), fontWeight = FontWeight.Bold)
                    }
                }
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)){
                    Button(onClick = { },
                        modifier = Modifier.weight(0.5f)
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFD146),
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp))
                    {
                        Text(text = "Aceptar",
                            fontSize = 15.sp,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.SansSerif)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { },
                        modifier = Modifier.weight(0.5f)
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Gray,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp))
                    {
                        Text(text = "Rechazar",
                            fontSize = 15.sp,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.SansSerif)
                        )
                    }
                }
            }
        }
    }
    if(page == 2){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(10.dp)
                .clip(RoundedCornerShape(10.dp))
                .padding(10.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(textUnderImage, modifier = Modifier.padding(5.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlideImage(
                        imageModel = { image },
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .height(100.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            productName,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            "S/${price} valor aprox.",
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            color = Color(0xFFFFD146),
                            fontWeight = FontWeight.Bold
                        )
                        Button(onClick = { },
                            modifier = Modifier.fillMaxWidth()
                                .height(40.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFD146),
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(10.dp))
                        {
                            Text(text = "Dejar Reseña",
                                fontSize = 15.sp,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.SansSerif)
                            )
                        }
                    }
                }
            }
        }
    }

    /*
    Box(modifier = Modifier.fillMaxWidth()){
        Column {
            Text(textUnderImage2)
            Row {
                GlideImage(
                    imageModel = {productImage2},
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
                Column {
                    Text(productName2)
                    Text("S/${price2} valor aprox.")
                }
            }
        }
    }
     */
}