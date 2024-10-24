package com.techzo.cambiazo.presentation.exchanges

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.skydoves.landscapist.glide.GlideImage
import com.techzo.cambiazo.common.Constants
import com.techzo.cambiazo.common.components.DialogApp
import com.techzo.cambiazo.common.components.MainScaffoldApp
import com.techzo.cambiazo.presentation.review.ReviewViewModel

@Composable
fun ExchangeDetailsScreen(goBack: () -> Unit, viewModel: ExchangeViewModel = hiltViewModel(), reviewViewModel: ExchangeViewModel=hiltViewModel(), exchangeId:Int, page: Int) {

    LaunchedEffect(Unit) {
        viewModel.getExchangeById(exchangeId)
    }

    val exchange = viewModel.exchange.value
    val boolean = exchange.data?.userOwn?.id == Constants.user?.id

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
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.size(35.dp))                }

                Text(
                    text = "Detalle del\nIntercambio",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 20.dp)
                )
                Spacer(modifier = Modifier.width(50.dp))
            }
        }
    ) {
        exchange.data?.let{ exchange ->
            val  profilePicture= when (page) {
                0 -> exchange.userChange.profilePicture
                1 -> exchange.userOwn.profilePicture
                else -> if(boolean) exchange.userChange.profilePicture else exchange.userOwn.profilePicture
            }

            val userName= when (page) {
                0 -> exchange.userChange.name
                1 -> exchange.userOwn.name
                else -> if(boolean) exchange.userChange.name else exchange.userOwn.name
            }

            val productName= when (page) {
                0 -> exchange.productChange.name
                1 -> exchange.productOwn.name
                else ->if(boolean) exchange.productChange.name else exchange.productOwn.name
            }

            val productName2= when (page) {
                0 -> exchange.productOwn.name
                1 -> exchange.productChange.name
                else -> if(boolean) exchange.productOwn.name else exchange.productChange.name
            }

            val price= when (page) {
                0 -> exchange.productChange.price
                1 -> exchange.productOwn.price
                else -> if(boolean) exchange.productChange.price else exchange.productOwn.price
            }

            val price2= when (page) {
                0 -> exchange.productOwn.price
                1 -> exchange.productChange.price
                else -> if(boolean) exchange.productOwn.price else exchange.productChange.price
            }

            val status= if (page == 0 && exchange.status == "Pendiente") "Enviado"
            else if(page==1) exchange.status
            else "WhatsApp"

            val description= when (page) {
                0 -> exchange.productChange.description
                1 -> exchange.productOwn.description
                else -> if(boolean) exchange.productChange.description else exchange.productOwn.description
            }

            val location= when (page) {
                0 -> viewModel.getLocationString(exchange.productChange.districtId)
                1 -> viewModel.getLocationString(exchange.productOwn.districtId)
                else -> viewModel.getLocationString(exchange.productChange.districtId)
            }

            val productImage= when (page) {
                0 -> exchange.productChange.image
                1 -> exchange.productOwn.image
                else -> if(boolean) exchange.productChange.image else exchange.productOwn.image
            }

            val productImage2= when (page) {
                0 -> exchange.productOwn.image
                1 -> exchange.productChange.image
                else -> if(boolean) exchange.productOwn.image else exchange.productChange.image
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

            val phoneNumber =when (page) {
                0 -> "+51${exchange.userChange.phoneNumber}"
                1 -> "+51${exchange.userOwn.phoneNumber}"
                else -> if(boolean) "+51${exchange.userChange.phoneNumber}" else "+51${exchange.userOwn.phoneNumber}"
            }

            val textColorStatus= when (page) {
                0 -> if (exchange.status == "Pendiente") Color.Gray else Color(0xFF38B000)
                1 -> Color(0xFFFFD146)
                else -> Color.White
            }

            val textBackgroundColor= when (page) {
                0 -> if (exchange.status == "Pendiente") Color(0xFFE8E8E8) else Color(0xFF38B000)
                1 -> Color.Black
                else -> Color(0xFF38B000)
            }

            val context = LocalContext.current

            val authorId= if(boolean) exchange.userOwn.id else exchange.userChange.id
            val receptorId = if(boolean) exchange.userChange.id else exchange.userOwn.id

            Column{
                Column(modifier = Modifier.padding(start = 15.dp, end = 15.dp)) {

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
                                    .size(50.dp)
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
                            color = textColorStatus,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50.dp))
                                .background(textBackgroundColor)
                                .padding(horizontal = 20.dp, vertical = 3.dp)
                                .clickable {
                                    if(page==2){
                                        val formattedNumber = phoneNumber.replace("+", "").replace(" ", "")
                                        val url = "https://wa.me/$formattedNumber"
                                        val intent = Intent(Intent.ACTION_VIEW).apply {
                                            data = Uri.parse(url)
                                        }
                                        context.startActivity(intent)
                                    }
                                }
                            ,
                            fontWeight = FontWeight.Bold, fontSize = 14.sp,
                        )
                    }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        GlideImage(
                            imageModel = {productImage},
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .height(300.dp)
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
                                Text(textUnderImage, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color(0xFF6D6D6D))
                            }
                            Text(productName, fontWeight = FontWeight.Bold, fontSize = 26.sp)
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

                }

                Spacer(modifier = Modifier.weight(1f))

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth(),
                    color = Color(0xFFDCDCDC),
                    thickness = 1.dp
                )

                Column(modifier = Modifier.padding(start = 15.dp, end = 15.dp)) {
                    BoxUnderExchange(textUnderImage2,productImage2, productName2, price2.toString(), page, exchangeId = exchange.id, goBack = goBack, userAuthor = authorId, userReceptor = receptorId)

                }
            }
        }
    }
}

@Composable
fun BoxUnderExchange(textUnderImage:String, image:String, productName: String, price: String, page: Int, viewModel: ExchangeViewModel = hiltViewModel(), reviewViewModel: ReviewViewModel= hiltViewModel(), exchangeId: Int, goBack: () -> Unit,userAuthor:Int, userReceptor: Int) {
    var showDialog by remember { mutableStateOf(false) }
    var showDialog2 by remember { mutableStateOf(false) }
    var showDialog3 by remember { mutableStateOf(false) }


    if(page == 0){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .padding(bottom = 15.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(textUnderImage, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color(0xFF6D6D6D))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    GlideImage(
                        imageModel = { image },
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .height(140.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Column(modifier = Modifier.fillMaxWidth(1f).padding(horizontal = 5.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(productName, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 20.sp)
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
                .padding(10.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(textUnderImage, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color(0xFF6D6D6D))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlideImage(
                        imageModel = { image },
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .height(110.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Column(modifier = Modifier.fillMaxWidth(1f).padding(horizontal = 5.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(productName, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("S/${price} valor aprox.", textAlign = TextAlign.Center, fontSize = 16.sp, color = Color(0xFFFFD146), fontWeight = FontWeight.Bold)
                    }
                }
                Row(modifier = Modifier.fillMaxWidth().padding(top = 5.dp)){
                    Button(onClick = {
                        showDialog=true
                        viewModel.updateExchangeStatus(exchangeId,"Aceptado")
                    },
                        modifier = Modifier.weight(0.5f)
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFD146),
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp))
                    {
                        Text(text = "Aceptar",
                            fontSize = 16.sp,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.SansSerif)
                        )
                    }
                    if(showDialog){
                        DialogApp(
                            message = "¡Intercambio Aceptado!",
                            description = "¡Felicidades por completar tu CambiaZo! Ahora puedes comunicarte con el otro usuario para concretar el intercambio.",
                            labelButton1 = "Aceptar",
                            onClickButton1 = { viewModel.getExchangesByUserChangeId(); showDialog = false; goBack() }
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(onClick = {
                        showDialog2=true
                    },
                        modifier = Modifier.weight(0.5f)
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFDCDCDC),
                            contentColor = Color(0xFFA4A4A4)
                        ),
                        shape = RoundedCornerShape(10.dp))
                    {
                        Text(text = "Rechazar",
                            fontSize = 16.sp,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.SansSerif)
                        )
                    }
                    if(showDialog2){
                        DialogApp(
                            message = "¿Estás seguro de deseas rechazar la oferta?",
                            description = "Si rechazas la oferta, no podrás volver a verla. ¿Quieres continuar?",
                            labelButton1 = "Rechazar Oferta",
                            labelButton2 = "Cancelar",
                            onClickButton1 = { viewModel.updateExchangeStatus(exchangeId, "Rechazado"); viewModel.getExchangesByUserChangeId(); showDialog2 = false; goBack() },
                            onClickButton2 = { showDialog2 = false; }
                        )
                    }
                }
            }
        }
    }
    if(page == 2){
        var review= ""
        var rating= 0
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .padding(bottom = 20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(textUnderImage, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color(0xFF6D6D6D))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlideImage(
                        imageModel = { image },
                        modifier = Modifier
                            .fillMaxWidth(0.45f)
                            .height(140.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Column(
                        modifier = Modifier.padding(5.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            productName,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            "S/${price} valor aprox.",
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            color = Color(0xFFFFD146),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Button(onClick = {
                            showDialog3=true
                        },
                            modifier = Modifier.fillMaxWidth()
                                .height(40.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFD146),
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(10.dp))
                        {
                            Text(text = "Dejar Reseña",
                                fontSize = 16.sp,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.SansSerif)
                            )
                        }
                        if(showDialog3){
                            DialogApp(
                                message = "Deja tu Reseña",
                                isNewReview = true,
                                labelButton1 = "Enviar",
                                labelButton2 = "Cancelar",
                                onClickButton1 = { showDialog3=false},
                                onClickButton2 = {showDialog3=false},
                                onSubmitReview = { newRating, newReview ->
                                    rating = newRating
                                    review = newReview
                                    reviewViewModel.addReview(review,rating,"Enviado",userAuthor, userReceptor, exchangeId,)
                                    showDialog3 = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
