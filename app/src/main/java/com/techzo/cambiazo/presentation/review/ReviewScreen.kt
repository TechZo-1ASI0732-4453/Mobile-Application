package com.techzo.cambiazo.presentation.review

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Info
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.techzo.cambiazo.common.components.CustomTabs
import com.techzo.cambiazo.common.components.EmptyStateMessage
import com.techzo.cambiazo.common.components.MainScaffoldApp
import com.techzo.cambiazo.common.components.ProfileImage
import com.techzo.cambiazo.common.components.ReviewItem
import com.techzo.cambiazo.common.components.StarRating
import com.techzo.cambiazo.domain.Product
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ReviewScreen(
    userId: Int,
    viewModel: ReviewViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onProductClick: (Product) -> Unit,
    onUserClick: (Int) -> Unit
) {
    val selectedTabIndex = remember { mutableStateOf(0) }

    LaunchedEffect(userId) {
        viewModel.getReviewAverageByUserId(userId)
    }

    val reviewAverageUser = viewModel.reviewAverageUser.value
    val reviewsState = viewModel.reviews.value
    val articlesState = viewModel.articles.value
    val errorMessage = viewModel.errorMessage.value


    MainScaffoldApp(
        paddingCard = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        contentsHeader = {
            Box(modifier = Modifier.fillMaxWidth().padding(start = 16.dp)) {
                IconButton(onClick = { onBack() }) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null)
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
                    size = 100.dp
                )
            }
        },
        content = {
            if (errorMessage != null) {
                Text(text = errorMessage, color = Color.Red)
            } else if (reviewAverageUser != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val user = reviewsState.data?.firstOrNull()?.userReceptor
                        ?: articlesState.data?.firstOrNull()?.user

                    Text(
                        text = user?.name ?: "Usuario",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val formattedDate = user?.createdAt?.let { dateFormat.format(it) }

                    Text(
                        text = "Registrado el ${formattedDate ?: "Fecha desconocida"}",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val averageRating = reviewAverageUser.averageRating ?: 0.0
                        StarRating(rating = averageRating, size = 28.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(Color.Black, shape = RoundedCornerShape(50))
                                .padding(horizontal = 12.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${reviewsState.data?.size ?: 0}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                CustomTabs(
                    selectedTabIndex = selectedTabIndex.value,
                    itemTabs = listOf("Artículos", "Reseñas"),
                    onTabSelected = { index -> selectedTabIndex.value = index }
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedTabIndex.value == 0) {
                    if (articlesState.data != null && articlesState.data!!.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            itemsIndexed(articlesState.data!!.chunked(2)) { _, rowProducts ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rowProducts.forEach { product ->
                                        ProductItem(
                                            product = product,
                                            onClick = onProductClick,
                                            modifier = Modifier.weight(1f)
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
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            items(reviewsState.data!!) { review ->
                                ReviewItem(review, onUserClick)
                            }
                        }
                    } else if (reviewsState.data != null && reviewsState.data!!.isEmpty()) {
                        EmptyStateMessage(
                            icon = Icons.Filled.Info,
                            message = "Este usuario no tiene reseñas disponibles",
                            subMessage = "Sé el primero en dejar una reseña."
                        )
                    }
                }
            }
        }
    )
}



@Composable
fun ProductItem(
    product: Product,
    modifier: Modifier = Modifier,
    onClick: (Product) -> Unit
) {
    Card(
        modifier = modifier
            .clickable { onClick(product) }
            .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.background(Color(0xFFF0F0F0))
        ) {
            AsyncImage(
                model = product.image,
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(Color.White)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}