package com.techzo.cambiazo.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Diamond
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techzo.cambiazo.common.Constants
import com.techzo.cambiazo.common.components.MainScaffoldApp
import com.techzo.cambiazo.common.components.ProfileImage
import com.techzo.cambiazo.common.components.StarRating

@Composable
fun ProfileScreen(
    logOut: () -> Unit = {},
    openMyReviews: () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()) {

    val averageRating = viewModel.averageRating.value
    val countReviews = viewModel.countReviews.value

    MainScaffoldApp(
        bottomBar = bottomBar,
        paddingCard = PaddingValues(top = 15.dp),
        contentsHeader = {
        },
        profileImage = {
            ProfileImage(
                url = Constants.user?.profilePicture ?: Constants.DEFAULT_PROFILE_PICTURE,
                shape = CircleShape,
                size = 120.dp)
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(60.dp))

                Text(
                    text = Constants.user?.name ?: "",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = Constants.user?.username ?: "",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    StarRating(rating = averageRating ?: 0.0, 24.dp)

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
                            text = countReviews?.toString() ?: "0",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                }

                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    ProfileOption(icon = Icons.Outlined.Edit, text = "Editar Perfil")
                    HorizontalDivider(color = Color(0xFFF2F2F2), thickness = 1.5.dp)
                    ProfileOption(icon = Icons.Outlined.FavoriteBorder, text = "Favoritos")
                    HorizontalDivider(color = Color(0xFFF2F2F2), thickness = 1.5.dp)
                    ProfileOption(
                        icon = Icons.Outlined.StarOutline,
                        text = "Mis Reseñas",
                        onClick = { openMyReviews()}
                    )
                    HorizontalDivider(color = Color(0xFFF2F2F2), thickness = 1.5.dp)
                    ProfileOption(icon = Icons.Outlined.Diamond, text = "Mi Suscripción")
                    HorizontalDivider(color = Color(0xFFF2F2F2), thickness = 1.5.dp)
                    ProfileOption(
                        icon = Icons.Outlined.Logout,
                        text = "Cerrar Sesión",
                        onClick = {
                            viewModel.onLogout()
                            logOut()
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun ProfileOption(icon: ImageVector, text: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .padding(16.dp, 22.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(25.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = text,
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = Color(0xFFFFD146),
            modifier = Modifier.size(28.dp)
        )
    }
    }


