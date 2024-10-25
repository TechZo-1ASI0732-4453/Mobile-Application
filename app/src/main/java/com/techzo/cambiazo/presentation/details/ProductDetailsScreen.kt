package com.techzo.cambiazo.presentation.details


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.techzo.cambiazo.common.Constants
import com.techzo.cambiazo.common.UIState
import com.techzo.cambiazo.common.components.ButtonApp
import com.techzo.cambiazo.common.components.StarRating
import com.techzo.cambiazo.domain.*

@Composable
fun ProductDetailsScreen(
    productId: Int,
    userId: Int,
    viewModel: ProductDetailsViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onUserClick: () -> Unit,
    onMakeOffer: (desiredProduct: Product, offeredProduct: Product) -> Unit
) {
    val viewModel: ProductDetailsViewModel = hiltViewModel()
    val productState = viewModel.product.value
    val averageRating = viewModel.averageRating.value
    val countReviews = viewModel.countReviews.value
    val isFavoriteState = viewModel.isFavorite.value

    LaunchedEffect(productId, userId) {
        viewModel.loadProductDetails(productId, userId)
    }

    Scaffold(
        content = { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                val product = productState.data
                if (product != null) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        ProductHeader(product = product, onBack = onBack)
                        ProductDetails(
                            product = product,
                            user = product.user,
                            averageRating = averageRating ?: 0.0,
                            countReviews = countReviews ?: 0,
                            isFavoriteState = isFavoriteState,
                            onFavoriteToggle = { isCurrentlyFavorite ->
                                viewModel.toggleFavoriteStatus(productId, isCurrentlyFavorite)
                            },
                            onUserClick = { onUserClick() },
                            onMakeOffer = onMakeOffer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 360.dp)
                                .clip(RoundedCornerShape(topStart = 36.dp, topEnd = 40.dp))
                                .background(Color.White)
                        )
                    }
                } else if (productState.isLoading) {
                    // Show loading indicator
                } else {
                    Text(
                        text = productState.message ?: "Error al cargar detalles del producto",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    )
}

@Composable
fun ProductHeader(product: Product, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(390.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(product.image),
            contentDescription = "Product Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .clip(RoundedCornerShape(0.dp))
                .offset(y = (0).dp),
            contentScale = ContentScale.Crop
        )
        IconButton(
            onClick = { onBack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(15.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(50)
                )
        ) {
            Icon(
                Icons.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(25.dp)
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .padding(bottom = 20.dp)
                .background(
                    Color.Black.copy(alpha = 0.7f), RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = "S/${product.price} valor aprox.",
                color = Color(0xFFFFD146),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun ProductDetails(
    product: Product,
    user: User,
    averageRating: Double,
    countReviews: Int,
    isFavoriteState: UIState<Boolean>,
    onFavoriteToggle: (Boolean) -> Unit,
    onUserClick: () -> Unit,
    onMakeOffer: (desiredProduct: Product, offeredProduct: Product) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp)
    ) {
        user?.let {
            if (it.name.isNotEmpty() && it.profilePicture.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onUserClick() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(it.profilePicture),
                        contentDescription = "User Avatar",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = it.name,
                            modifier = Modifier.padding(start = 2.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            StarRating(rating = averageRating, size = 22.dp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .background(Color.Black, shape = CircleShape)
                                    .offset(y = (-3).dp)
                                    .height(18.dp)
                                    .padding(horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text ="$countReviews",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = {
                            onFavoriteToggle(isFavoriteState.data ?: false)
                        },
                        modifier = Modifier
                            .background(
                                color = if (isFavoriteState.data == true) Color(0xFFFFD146) else Color(
                                    0xFFDFDFDF
                                ),
                                shape = CircleShape
                            )
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = Color.Black
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        Text(text = product.name, lineHeight = 26.sp,fontWeight = FontWeight.Bold, fontSize = 26.sp)
        Spacer(modifier = Modifier.height(5.dp))

        Text(text = product.description, fontSize = 16.sp, color = Color.Gray)

        Spacer(modifier = Modifier.weight(1f))

        if (product.location.districtName.isNotEmpty() && product.location.departmentName.isNotEmpty()) {
            Text(
                text = "¿Dónde puedo intercambiar este objeto?",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(5.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = "Ubicación",
                    tint = Color(0xFFFFD146),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "Disponible en ${product.location.districtName}, ${product.location.departmentName}",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(text = "Le interesa:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(text = product.desiredObject, fontSize = 16.sp , color = Color.Gray)

        Spacer(modifier = Modifier.weight(6f))

        val currentUserId = Constants.user?.id

        if(product.user.id != currentUserId ) {
            ButtonApp(
                text = "Intercambiar",
                onClick = {
                    onMakeOffer(product, product)
                }
            )
        }

    }
}