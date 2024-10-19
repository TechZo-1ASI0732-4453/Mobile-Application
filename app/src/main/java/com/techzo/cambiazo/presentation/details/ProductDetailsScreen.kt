package com.techzo.cambiazo.presentation.details

import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.techzo.cambiazo.R
import com.techzo.cambiazo.common.UIState
import com.techzo.cambiazo.common.components.ButtonApp
import com.techzo.cambiazo.domain.*

@Composable
fun ProductDetailsScreen(
    viewModel: ProductDetailsViewModel = hiltViewModel(),
    productId: Int?,
    userId: Int?
) {
    LaunchedEffect(productId, userId) {
        if (productId != null && userId != null) {
            viewModel.loadProductDetails(productId, userId)
        }
    }

    val productState = viewModel.product.value
    val userState = viewModel.user.value
    val reviewsState = viewModel.reviews.value
    val productCategoryState = viewModel.productCategory.value
    val districtState = viewModel.district.value
    val departmentState = viewModel.department.value
    val isFavoriteState = viewModel.isFavorite.value

    Scaffold(
        topBar = {},
        content = { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                if (productState.isLoading || userState.isLoading || reviewsState.isLoading) {
                    CircularProgressIndicator()
                } else if (productState.data != null) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        ProductHeader(product = productState.data!!)
                        ProductDetails(
                            product = productState.data!!,
                            userState = userState,
                            reviewsState = reviewsState,
                            productCategoryState = productCategoryState,
                            districtState = districtState,
                            departmentState = departmentState,
                            isFavoriteState = isFavoriteState,
                            onFavoriteToggle = { isCurrentlyFavorite ->
                                viewModel.toggleFavoriteStatus(
                                    productId!!,
                                    isCurrentlyFavorite
                                )
                            }
                        )
                    }
                } else if (productState.message != null) {
                    Text(text = "Error: ${productState.message}", color = Color.Red)
                }
            }
        }
    )
}

@Composable
fun ProductHeader(product: Product) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Image(
            painter = rememberAsyncImagePainter(product.image),
            contentDescription = "Product Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp),
            contentScale = ContentScale.Crop
        )
        IconButton(
            onClick = { /* Handle back action */ },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(50)
                )
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .background(
                    Color.Black.copy(alpha = 0.7f), RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = "S/${product.price} valor aprox.",
                color = Color(0xFFFFD146),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun ProductDetails(
    product: Product,
    userState: UIState<User>,
    reviewsState: UIState<List<Review>>,
    productCategoryState: UIState<ProductCategory>,
    districtState: UIState<District>,
    departmentState: UIState<Department>,
    isFavoriteState: UIState<Boolean>,
    onFavoriteToggle: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {

        if (userState.data != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val imageUrl = userState.data!!.profilePicture
                val painter =
                    if (imageUrl.isNullOrEmpty() || !Patterns.WEB_URL.matcher(imageUrl).matches()) {
                        rememberAsyncImagePainter(R.drawable.default_user_image)
                    } else {
                        rememberAsyncImagePainter(imageUrl)
                    }

                Image(
                    painter = painter,
                    contentDescription = "User Avatar",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = userState.data!!.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) { index ->
                            val tint =
                                if (index < (reviewsState.data?.firstOrNull()?.rating ?: 0)) {
                                    Color(0xFFFFD700)
                                } else {
                                    Color.Gray
                                }
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Star",
                                tint = tint,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(Color.Black, shape = CircleShape)
                                .padding(horizontal = 10.dp, vertical = 2.dp)
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

                // Toggle botón de favoritos
                IconButton(
                    onClick = { onFavoriteToggle(isFavoriteState.data == true) }
                ) {
                    Icon(
                        imageVector = if (isFavoriteState.data == true) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavoriteState.data == true) Color.Yellow else Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Nombre del producto
            Text(text = product.name, fontWeight = FontWeight.Bold, fontSize = 26.sp)
            Spacer(modifier = Modifier.height(6.dp))

            // Categoría del producto
            if (productCategoryState.data != null) {
                Text(
                    text = productCategoryState.data!!.name,
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            } else if (productCategoryState.message != null) {
                Text(text = "Error: ${productCategoryState.message}")
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Descripción del producto
            Text(text = product.description, fontSize = 18.sp)

            Spacer(modifier = Modifier.height(20.dp))

            // Ubicación del intercambio
            if (districtState.data != null) {
                val district = districtState.data!!
                if (departmentState.data != null) {
                    val department = departmentState.data!!
                    Text(
                        text = "¿Dónde puedo intercambiar este objeto?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = "Ubicación",
                            tint = Color(0xFFFFD146),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Disponible en ${district.name}, ${department.name}",
                            fontSize = 18.sp,
                            color = Color.Gray
                        )
                    }
                } else if (departmentState.message != null) {
                    Text(text = "Error: ${departmentState.message}")
                }
            } else if (districtState.message != null) {
                Text(text = "Error: ${districtState.message}")
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Intereses del usuario
            Text(text = "Le interesa:", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = product.desiredObject, fontSize = 18.sp)

            Spacer(modifier = Modifier.weight(1f))

            // Botón de intercambio
            ButtonApp(
                text = "Intercambiar",
                onClick = { /* Handle exchange action */ },
            )
        }
    }
}