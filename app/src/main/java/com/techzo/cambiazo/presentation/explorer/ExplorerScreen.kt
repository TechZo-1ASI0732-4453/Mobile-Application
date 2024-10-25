package com.techzo.cambiazo.presentation.explorer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.skydoves.landscapist.glide.GlideImage
import com.techzo.cambiazo.common.components.FieldTextApp
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

    val availableProducts = state.data?.filter { it.available } ?: emptyList()


    MainScaffoldApp(
        bottomBar = bottomBar,
        paddingCard = PaddingValues(top = 10.dp),
        contentsHeader = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 29.dp, horizontal = 20.dp),
            ) {
                BasicTextField(
                    value = searcher,
                    onValueChange = { viewModel.onNameChanged(it) },
                    modifier = Modifier
                        .shadow(10.dp, RoundedCornerShape(10.dp))
                        .weight(1f)
                        .height(50.dp)
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .border(9.dp, Color.Transparent, RoundedCornerShape(8.dp)),
                    singleLine = true,
                    maxLines = 1,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Normal,
                        fontSize = 18.sp,
                        color = Color.Black
                    ),
                    decorationBox = { innerTextField ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                tint = Color.Gray,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (searcher.isEmpty()) {
                                    Text(
                                        "Buscar",
                                        color = Color.Gray,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 18.sp
                                        )
                                    )
                                }
                                innerTextField()
                            }
                        }
                    }
                )
                IconButton(onClick = {onFilter()},
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .background(Color.Black, RoundedCornerShape(8.dp))
                        .shadow(10.dp, RoundedCornerShape(10.dp))
                        .size(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Tune,
                        contentDescription = "Filtro",
                        tint = Color.White
                    )
                }
            }
        }
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 15.dp, bottom = 15.dp)
        ) {
            items(categories.data ?: emptyList()) { category ->
                val isSelected = viewModel.categoryId.value == category.id

                Box(
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .height(35.dp)
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
                        modifier = Modifier.padding(horizontal = 25.dp),
                        text = category.name,
                        color = Color.Black,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp)
                }
            }
        }

        LazyColumn{
            items(availableProducts) { product ->
                Products(product, onProductClick)
            }
            item { Spacer(modifier = Modifier.height(15.dp)) }
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
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .border(0.dp, Color.Transparent, RoundedCornerShape(15.dp))
            .shadow(elevation = 12.dp, RoundedCornerShape(15.dp))
            .clickable { onProductClick(product.id.toString(), product.user.id.toString()) },
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .background(Color.Transparent)
            ) {
                GlideImage(
                    imageModel = { product.image },
                    modifier = Modifier.fillMaxSize()
                )

                icon?.let {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.TopEnd)
                            .size(45.dp)
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
                        text = "S/${product.price} aprox.",
                        color = Color(0xFFFFD146),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(15.dp)
            ) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    lineHeight = 24.sp,
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Ubicación",
                        tint = Color(0xFFFFD146),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "${product.location.districtName}, ${product.location.departmentName}",
                        color = Color(0xFF9F9C9C),
                        modifier = Modifier.padding(start = 1.dp),
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = product.description,
                    color = Color.Black,
                    fontSize = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}