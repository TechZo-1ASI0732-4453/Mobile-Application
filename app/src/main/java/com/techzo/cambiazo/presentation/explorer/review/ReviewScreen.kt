package com.techzo.cambiazo.presentation.explorer.review

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.techzo.cambiazo.common.Constants.user
import com.techzo.cambiazo.common.components.ArticlesOwn
import com.techzo.cambiazo.common.components.CustomTabs
import com.techzo.cambiazo.common.components.EmptyStateMessage
import com.techzo.cambiazo.common.components.MainScaffoldApp
import com.techzo.cambiazo.common.components.ProfileImage
import com.techzo.cambiazo.common.components.ReviewItem
import com.techzo.cambiazo.common.components.StarRating
import com.techzo.cambiazo.presentation.navigate.Routes
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ReviewScreen(
    navController: NavController,
    viewModel: ReviewViewModel = hiltViewModel()
) {
    val selectedTabIndex = remember { mutableStateOf(0) }
    val context = LocalContext.current
    val reviewsState by viewModel.reviews
    val reviewUser = reviewsState.data?.firstOrNull()?.userReceptor
    val reviewAverageUser by viewModel.reviewAverageUser
    val articlesState by viewModel.articles
    val errorMessage by viewModel.errorMessage

    val onProductClick: (Int, Int) -> Unit = { productId, productUserId ->
        navController.navigate(
            Routes.ProductDetails.createProductDetailsRoute(
                productId.toString(),
                productUserId.toString()
            )
        )
    }

    val onUserClick: (Int) -> Unit = { selectedUserId ->
        navController.navigate(Routes.Reviews.createRoute(selectedUserId.toString()))
    }

    MainScaffoldApp(
        paddingCard = PaddingValues(horizontal = 15.dp, vertical = 8.dp),
        contentsHeader = {
            Box(modifier = Modifier.fillMaxWidth().padding(start = 15.dp)) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        },
        profileImage = {
            val user = reviewsState.data?.firstOrNull()?.userReceptor
                ?: articlesState.data?.firstOrNull()?.user

            user?.let {
                ProfileImage(
                    url = it.profilePicture,
                    shape = CircleShape,
                    size = 110.dp
                )
            }
        },
        content = {
            if (errorMessage != null) {
                Text(text = errorMessage ?: "Unknown error", color = Color.Red)
            } else {
                val localReviewAverageUser = reviewAverageUser
                if (localReviewAverageUser != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 55.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val user = reviewsState.data?.firstOrNull()?.userReceptor
                            ?: articlesState.data?.firstOrNull()?.user

                        Text(
                            text = user?.name ?: "Usuario",
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        )

                        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val formattedDate = user?.createdAt?.let { dateFormat.format(it) }

                        Text(
                            text = "Registrado el ${formattedDate ?: "Fecha desconocida"}",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val averageRating = localReviewAverageUser.averageRating ?: 0.0
                            StarRating(rating = averageRating, size = 24.dp)

                            Spacer(modifier = Modifier.width(4.dp))

                            Box(
                                modifier = Modifier
                                    .background(Color.Black, shape = CircleShape)
                                    .offset(y = (-3).dp)
                                    .height(18.dp)
                                    .padding(horizontal = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${reviewsState.data?.size ?: 0}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    reviewUser?.let { user ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                         Button(
                             onClick = {
                                 val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                     type = "text/plain"
                                     putExtra(Intent.EXTRA_TEXT, "Conoce a ${user.name}: ${user.profilePicture}")
                                 }
                                 val chooser = Intent.createChooser(shareIntent, "Compartir perfil").apply {
                                     addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                 }
                                 context.startActivity(chooser)
                             },
                             colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD146))
                         ) {
                             Icon(imageVector = Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(20.dp))
                             Spacer(Modifier.width(4.dp))
                             Text("Compartir perfil")
                         }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    CustomTabs(
                        selectedTabIndex = selectedTabIndex.value,
                        itemTabs = listOf("Artículos", "Reseñas"),
                        onTabSelected = { index -> selectedTabIndex.value = index }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (selectedTabIndex.value == 0) {
                        val availableArticles = articlesState.data?.filter { it.available } ?: emptyList()
                        if (availableArticles.isNotEmpty()) {
                            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                itemsIndexed(availableArticles.chunked(2)) { _, rowProducts ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        rowProducts.forEach { product ->
                                            ArticlesOwn(
                                                modifier = Modifier.weight(1f),
                                                product = product,
                                                onClick = { productId, userId ->
                                                    onProductClick(productId, userId)
                                                }
                                            )
                                        }
                                        if (rowProducts.size < 2) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        } else {
                            EmptyStateMessage(
                                icon = Icons.Filled.Info,
                                message = "No hay artículos disponibles",
                                subMessage = "Vuelve pronto para ver más artículos."
                            )
                        }
                    }

                    if (selectedTabIndex.value == 1) {
                        if (reviewsState.data != null && reviewsState.data!!.isNotEmpty()) {
                            LazyColumn(
                                modifier = Modifier
                                    .padding(horizontal = 15.dp)
                                    .fillMaxWidth()
                            ) {
                                itemsIndexed(reviewsState.data!!) { index, review ->
                                    ReviewItem(review, onUserClick, dividerUp = false)
                                    if (index != reviewsState.data!!.size - 1) {
                                        HorizontalDivider(color = Color(0xFFDCDCDC), thickness = 1.dp)
                                    }
                                }
                            }
                        } else {
                            EmptyStateMessage(
                                icon = Icons.Filled.Info,
                                message = "Este usuario no tiene reseñas disponibles",
                                subMessage = "Sé el primero en dejar una reseña."
                            )
                        }
                    }
                }
            }
        }
    )
}

