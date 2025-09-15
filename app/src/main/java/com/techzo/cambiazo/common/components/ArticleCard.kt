package com.techzo.cambiazo.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.techzo.cambiazo.R
import com.techzo.cambiazo.domain.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ArticlesOwn(
    product: Product,
    modifier: Modifier = Modifier,
    iconActions: Boolean = false,
    deleteProduct: (Int) -> Unit = {},
    editProduct: (Product) -> Unit = {},
    onClick: (Int, Int) -> Unit = { _, _ -> }
) {
    var showDialog by remember { mutableStateOf(false) }
    var showActions by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }


    if (showDialog) {
        DialogApp(
            message = "¿Estás seguro de eliminar esta publicación?",
            description = "Recuerda que una vez eliminada la publicación, no se podrá deshacer.",
            labelButton1 = "Eliminar",
            labelButton2 = "Cancelar",
            isLoading = isLoading,
            onDismissRequest = { showDialog = false; showActions = false },
            onClickButton1 = {
                isLoading = true
                CoroutineScope(Dispatchers.Main).launch {
                    deleteProduct(product.id)
                    delay(1000)
                    showDialog = false
                    showActions = false
                    isLoading = false
                }
            },
            onClickButton2 = { showDialog = false; showActions = false }
        )
    }

    Card(
        modifier = modifier
            .padding(10.dp)
            .height(180.dp)
            .background(Color.White)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(12.dp))
            .clickable { onClick(product.id, product.user.id) },
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
    ) {

        Box(
            modifier = Modifier.weight(2f),
            contentAlignment = Alignment.TopEnd
        ) {
            AsyncImage(
                model = product.image,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
            )

            if (iconActions) {
                if (showActions) {
                    Popup(
                        alignment = Alignment.TopEnd,
                        onDismissRequest = { showActions = false }
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(5.dp)
                                .background(
                                    Color.Black.copy(alpha = 0.6f),
                                    RoundedCornerShape(25.dp)
                                ),
                        ) {
                            IconsAction(icon = Icons.Outlined.Edit, onclick = { editProduct(product); showActions = false })
                            Spacer(modifier = Modifier.size(15.dp))
                            IconsAction(icon = Icons.Outlined.Delete, onclick = { showDialog = true })
                            Spacer(modifier = Modifier.size(15.dp))
                            IconsAction(icon = Icons.Outlined.Close, onclick = { showActions = false })
                        }
                    }
                } else {
                    IconsAction(icon = Icons.Filled.MoreVert, margin = 5.dp, onclick = { showActions = true })
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = product.name,
                maxLines = 2,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun YellowLoader() {
    Popup(
        alignment = Alignment.Center,
        properties = PopupProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(60.dp),
                color = Color(0xFFFFD700) // Yellow color
            )
        }
    }
}

@Composable
fun ArticleExchange(
    productLeft: Product,
    productRight: Product,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Quieres",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                textAlign = TextAlign.Center,
            )
            ArticlesOwn(
                product = productLeft,
                modifier = Modifier
                    .size(width = 400.dp, height = 190.dp)
            )
        }

        Image(
            painter = painterResource(id = R.drawable.exchange_image),
            contentDescription = "exchange image",
            modifier = Modifier.size(50.dp)
        )

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Ofreces",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                textAlign = TextAlign.Center,
            )
            ArticlesOwn(
                product = productRight,
                modifier = Modifier
                    .size(width = 400.dp, height = 190.dp)
            )
        }
    }
}

@Composable
fun IconsAction(icon: ImageVector, margin: Dp = 0.dp, onclick: () -> Unit = {}) {
    val backgroundColor = if (icon == Icons.Filled.MoreVert) {
        Color.Black.copy(alpha = 0.6f)
    } else {
        Color.Transparent
    }

    IconButton(
        onClick = onclick,
        modifier = Modifier
            .padding(margin)
            .background(backgroundColor, RoundedCornerShape(25.dp))
            .size(38.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(25.dp)
        )
    }
}