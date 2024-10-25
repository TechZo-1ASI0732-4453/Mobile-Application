package com.techzo.cambiazo.presentation.explorer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.skydoves.landscapist.glide.GlideImage
import com.techzo.cambiazo.common.components.MainScaffoldApp
import com.techzo.cambiazo.domain.Product

@Composable
fun ExplorerScreen(
    viewModel: ExplorerListViewModel = hiltViewModel(),
    bottomBar: @Composable () -> Unit = {},
    onFilter: () -> Unit = {},
    onProductClick: (String, String) -> Unit) {
    val searcher = viewModel.name.value
    val categories = viewModel.productCategories.value
    val state = viewModel.state.value

    MainScaffoldApp(
        bottomBar = bottomBar,
        paddingCard = PaddingValues(top = 10.dp),
        contentsHeader = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 25.dp, horizontal = 20.dp),
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .shadow(10.dp, RoundedCornerShape(10.dp))
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(10.dp))
                        .border(9.dp, Color.Transparent, RoundedCornerShape(10.dp)),
                    value = searcher,
                    onValueChange = { viewModel.onNameChanged(it) },
                    placeholder = {
                        Row {
                            Text("Buscar",color = Color.Gray,
                                style= MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Normal, fontSize = 20.sp,
                                    fontFamily = FontFamily.SansSerif))
                        }
                    },
                    maxLines = 1,
                    singleLine = true,
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, tint = Color.Gray , contentDescription = null)
                    },
                    shape = RoundedCornerShape(10.dp),
                )

                IconButton(onClick = {onFilter()},
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .background(Color.Black, RoundedCornerShape(10.dp))
                        .shadow(10.dp, RoundedCornerShape(10.dp))
                        .size(53.dp))
                {
                    Icon(imageVector = Icons.Filled.Tune,
                        contentDescription = "Filtro",
                        tint = Color.White)
                }
            }
        }
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            items(categories.data ?: emptyList()) { category ->
                val isSelected = viewModel.categoryId.value == category.id

                Box(
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .height(45.dp)
                        .background(
                            if (isSelected) Color(0xFFFFD146)
                            else Color.White,
                            RoundedCornerShape(10.dp)
                        )
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { viewModel.onProductCategorySelected(category.id) }
                        .border(1.dp, Color(0xFFFFD146), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 25.dp).fillMaxWidth(),
                        text = category.name,
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.SansSerif)
                }
            }
        }

        LazyColumn{
            items(state.data ?: emptyList()) { product ->
                Products(product, onProductClick)
            }

        }
    }
}

@Composable
fun Products(product: Product,
             onProductClick: (String, String) -> Unit,
             icon: ImageVector?=null,
             onClickIcon: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .border(0.dp, Color.Transparent, RoundedCornerShape(15.dp))
            .shadow(elevation = 12.dp, RoundedCornerShape(15.dp))
            .clickable { onProductClick(product.id.toString(), product.user.id.toString()) },
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .background(Color.Transparent)
            ) {
                GlideImage(
                    imageModel = { product.image },
                    modifier = Modifier.fillMaxSize()
                )

                icon?.let {
                    Box(
                        modifier = Modifier.padding(8.dp)
                            .align(Alignment.TopEnd)
                            .background(
                                Color.Black.copy(alpha = 0.6f), RoundedCornerShape(50.dp)
                            )
                    ) {
                        IconButton(onClick = onClickIcon) {
                            Icon(imageVector = icon,
                                contentDescription = null,
                                tint = Color(0xFFFFD146))
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(
                            Color.Black.copy(alpha = 0.7f), RoundedCornerShape(13.dp)
                        )
                        .padding(horizontal = 14.dp)
                        .padding(vertical = 4.dp),
                ) {
                    Text(
                        text = "S/${product.price} valor aprox.",
                        color = Color(0xFFFFD146),
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 27.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Ubicación",
                        tint = Color(0xFFFFD146)
                    )
                    Text(
                        text = "${product.location.districtName}, ${product.location.departmentName}",
                        color = Color(0xFF9F9C9C),
                        modifier = Modifier.padding(start = 1.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = product.description,
                    color = Color.Black
                )
            }
        }
    }
}