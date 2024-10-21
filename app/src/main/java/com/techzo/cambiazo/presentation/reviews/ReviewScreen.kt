package com.techzo.cambiazo.presentation.reviews

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.techzo.cambiazo.R
import com.techzo.cambiazo.common.components.MainScaffoldApp
import com.techzo.cambiazo.common.components.ProfileImage
import com.techzo.cambiazo.common.components.StarRating
import com.techzo.cambiazo.domain.Product
import com.techzo.cambiazo.domain.Review
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ReviewScreen(
    userId: Int,
    viewModel: ReviewViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val selectedTabIndex = remember { mutableStateOf(0) }

    LaunchedEffect(userId) {
        viewModel.getReviewAverageByUserId(userId)
    }

    val reviewAverageUser = viewModel.reviewAverageUser.value
    val userState = viewModel.user.value
    val reviewsState = viewModel.reviews.value
    val articlesState = viewModel.articles.value
    val errorMessage = viewModel.errorMessage.value

    MainScaffoldApp(
        paddingCard = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        contentsHeader = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp) // Fija la altura del contenedor del icono
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(69.dp)
                        .align(Alignment.TopStart)
                        .padding(start = 16.dp, top = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        },
        profileImage = {
            userState.data?.let { user ->
                ProfileImage(
                    url = user.profilePicture,
                    shape = CircleShape,
                    size = 120.dp
                )
            }
        },
        content = {
            if (errorMessage != null) {
                Text(text = errorMessage, color = Color.Red)
            } else if (reviewAverageUser != null && userState.data != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = userState.data!!.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val formattedDate = dateFormat.format(userState.data!!.createdAt)

                    Text(
                        text = "Registrado el $formattedDate",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val averageRating = reviewAverageUser.averageRating ?: 0.0
                        StarRating(rating = averageRating, size = 32.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(Color.Black, shape = RoundedCornerShape(50))
                                .padding(horizontal = 16.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "${reviewsState.data?.size ?: 0}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                CustomTabs(
                    selectedTabIndex = selectedTabIndex.value,
                    onTabSelected = { index -> selectedTabIndex.value = index }
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (selectedTabIndex.value == 0) {
                    if (articlesState.data != null) {
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
                                            modifier = Modifier
                                                .weight(1f)
                                        )
                                    }
                                    if (rowProducts.size < 2) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    } else if (articlesState.message != null) {
                        Text(text = articlesState.message!!, color = Color.Red)
                    }
                } else if (selectedTabIndex.value == 1) {
                    if (reviewsState.data != null) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            items(reviewsState.data!!) { review ->
                                ReviewItem(review = review, viewModel = viewModel)
                                Divider()
                            }
                        }
                    } else if (reviewsState.message != null) {
                        Text(text = reviewsState.message!!, color = Color.Red)
                    }
                }
            }
        }
    )
}

@Composable
fun CustomTabs(selectedTabIndex: Int, onTabSelected: (Int) -> Unit) {
    val tabs = listOf("Artículos", "Reseñas")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp), // Ajustar el padding para que coincida con el contenido
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(16.dp)),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            tabs.forEachIndexed { index, title ->
                val isSelected = selectedTabIndex == index
                Button(
                    onClick = { onTabSelected(index) },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Color(0xFFFFD146) else Color(0xFFF0F0F0),
                        contentColor = if (isSelected) Color.Black else Color.Gray
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.5.dp)
                        .padding(vertical = 3.dp)
                        .height(48.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
fun ReviewItem(review: Review, viewModel: ReviewViewModel) {
    val authorFlow = remember(review.userAuthorId) { viewModel.getUserById(review.userAuthorId) }
    val authorState by authorFlow.collectAsState()
    val author = authorState.data

    if (author != null) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val profileUrl = author.profilePicture
                AsyncImage(
                    model = profileUrl,
                    contentDescription = "Foto de perfil de ${author.name}",
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.default_user_image)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = author.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    StarRating(
                        rating = review.rating.toDouble(),
                        size = 28.dp
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = review.message,
                fontSize = 18.sp,
                lineHeight = 22.sp
            )
        }
    }
}


@Composable
fun ProductItem(product: Product, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(Color(0xFFF0F0F0))
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
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp) // Establecer altura fija
                    .background(Color.White)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center // Centrar el contenido
            ) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis, // Añadir puntos suspensivos si el texto es muy largo
                    textAlign = TextAlign.Center // Centrar el texto
                )
            }
        }
    }
}