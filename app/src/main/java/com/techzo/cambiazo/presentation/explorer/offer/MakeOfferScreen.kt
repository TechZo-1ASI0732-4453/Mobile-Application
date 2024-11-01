package com.techzo.cambiazo.presentation.explorer.offer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.techzo.cambiazo.common.components.ArticlesOwn
import com.techzo.cambiazo.common.components.ButtonIconHeaderApp
import com.techzo.cambiazo.common.components.DialogApp
import com.techzo.cambiazo.common.components.MainScaffoldApp
import com.techzo.cambiazo.presentation.articles.ArticlesViewModel
import com.techzo.cambiazo.presentation.navigate.Routes
import kotlinx.coroutines.launch

@Composable
fun MakeOfferScreen(
    navController: NavController,
    viewModel: MakeOfferViewModel = hiltViewModel(),
    articlesViewModel: ArticlesViewModel = hiltViewModel()
) {
    val desiredProduct by viewModel.desiredProduct.collectAsState()
    val articlesState by articlesViewModel.products.collectAsState()
    val userProducts = articlesState.data?.filter { it.available } ?: emptyList()

    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

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
                        text = "¿Qué ofreces a",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "cambio?",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        content = {
            Spacer(modifier = Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(0.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(userProducts) { product ->
                    ArticlesOwn(
                        product = product,
                        onClick = { selectedProductId: Int, _: Int ->
                            coroutineScope.launch {
                                val exists = viewModel.checkIfExchangeExists(selectedProductId) ?: false
                                if (exists) {
                                    dialogMessage = "Ya has realizado una oferta con este artículo. Por favor, selecciona otro producto."
                                    showDialog = true
                                } else {
                                    desiredProduct?.let {
                                        navController.navigate(
                                            Routes.ConfirmationOffer.createConfirmationOfferRoute(
                                                it.id.toString(),
                                                selectedProductId.toString()
                                            )
                                        )
                                    } ?: run {
                                        dialogMessage = "Producto deseado no encontrado."
                                        showDialog = true
                                    }
                                }
                            }
                        }
                    )
                }
                item {
                    AddProductButton(onPublish = { navController.navigate(Routes.Publish.route) })
                }
            }

            if (showDialog) {
                DialogApp(
                    message = "Oferta ya realizada",
                    description = dialogMessage,
                    labelButton1 = "Aceptar",
                    onClickButton1 = {
                        showDialog = false
                    }
                )
            }
        }
    )
}

@Composable
fun AddProductButton(onPublish: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(10.dp)
            .height(120.dp)
            .fillMaxWidth()
            .clickable(onClick = onPublish),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFD146))
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Agregar",
                tint = Color.Black,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}