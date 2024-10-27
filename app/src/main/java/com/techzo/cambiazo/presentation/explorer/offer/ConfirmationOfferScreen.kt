package com.techzo.cambiazo.presentation.explorer.offer

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.techzo.cambiazo.common.components.ArticleExchange
import com.techzo.cambiazo.common.components.ButtonApp
import com.techzo.cambiazo.common.components.ButtonIconHeaderApp
import com.techzo.cambiazo.common.components.DialogApp
import com.techzo.cambiazo.common.components.MainScaffoldApp
import com.techzo.cambiazo.domain.Product

@Composable
fun ConfirmationOfferScreen(
    navController: NavController,
    desiredProduct: Product,
    offeredProduct: Product,
    viewModel: OfferViewModel = hiltViewModel()
) {
    var showDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    MainScaffoldApp(
        paddingCard = PaddingValues(horizontal = 20.dp, vertical = 15.dp),
        contentsHeader = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                ButtonIconHeaderApp(
                    iconVector = Icons.Filled.ArrowBack,
                    onClick = { navController.popBackStack() },
                    iconSize = 35.dp,
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 30.dp)
                ) {
                    Text(
                        text = "¿Listo para enviar",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "tu oferta?",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ArticleExchange(
                    productLeft = desiredProduct,
                    productRight = offeredProduct,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                ButtonApp(
                    text = "Listo",
                    onClick = {
                        viewModel.makeOffer(
                            desiredProduct = desiredProduct,
                            offeredProduct = offeredProduct,
                            onSuccess = {
                                showDialog = true
                            },
                            onFailure = { message ->
                                errorMessage = message
                            }
                        )
                    }
                )

                if (showDialog) {
                    DialogApp(
                        message = "¡Oferta Enviada!",
                        description = "Te notificaremos el estado de tu solicitud. Ya sea que el otro usuario acepte o decline tu oferta.",
                        labelButton1 = "Volver",
                        onClickButton1 = {
                            showDialog = false
                            navController.popBackStack()
                        }
                    )
                }

                errorMessage?.let { message ->
                    Text(
                        text = message,
                        color = Color.Red,
                        modifier = Modifier.padding(top = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    )
}
