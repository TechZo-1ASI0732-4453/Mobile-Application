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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Diamond
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techzo.cambiazo.common.Constants
import com.techzo.cambiazo.common.components.DialogApp
import com.techzo.cambiazo.common.components.MainScaffoldApp
import com.techzo.cambiazo.common.components.ProfileImage
import com.techzo.cambiazo.common.components.StarRating
import com.techzo.cambiazo.presentation.profile.editprofile.EditProfileViewModel

@Composable
fun ProfileScreen(
    logOut: () -> Unit = {},
    openMyReviews: () -> Unit = {},
    openEditProfile: () -> Unit = {},
    openConfiguration: () -> Unit = {},
    openFavorites: () -> Unit = {},
    bottomBar: Pair<@Composable () -> Unit, () -> Unit>,
    openSubscription: () -> Unit = {},
    openDonationsScreen: () -> Unit = {},

    viewModel: ProfileViewModel = hiltViewModel()) {

    val averageRating = viewModel.averageRating.value
    val countReviews = viewModel.countReviews.value

    val user = Constants.user
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.isLoggedOut.value) {
        if (viewModel.isLoggedOut.value) {
            logOut()
        }
    }


    if (showLogoutDialog) {
        DialogApp(
            message = "Cerrar Sesión",
            description = "¿Estás seguro de que deseas cerrar sesión?",
            labelButton1 = "Sí",
            labelButton2 = "No",
            onDismissRequest = { showLogoutDialog = false },
            onClickButton1 = {
                viewModel.onLogout()
                showLogoutDialog = false
            },
            onClickButton2 = { showLogoutDialog = false }
        )
    }

    MainScaffoldApp(
        bottomBar = bottomBar,
        paddingCard = PaddingValues(top = 15.dp),
        contentsHeader = {
            Spacer(modifier = Modifier.height(35.dp))
        },
        profileImage = {
                ProfileImage(
                    url = user?.profilePicture ?: "",
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
                        text = user?.name ?: "",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = user?.username ?: "",
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
                        .padding(top = 15.dp)
                        .padding(horizontal = 30.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    ProfileOption(
                        icon = Icons.Outlined.Edit,
                        text = "Editar Perfil",
                        onClick = { openEditProfile() }
                    )

                    if(Constants.user?.isGoogleAccount == false) {
                        ProfileOption(
                            icon = Icons.Outlined.Settings,
                            text = "Configuración",
                            onClick = { openConfiguration() }
                        )
                    }

                    HorizontalDivider(color = Color(0xFFF2F2F2), thickness = 2.dp, modifier = Modifier.padding(vertical = 12.dp))

                    ProfileOption(
                        icon = Icons.Outlined.FavoriteBorder,
                        text = "Favoritos",
                        onClick = { openFavorites() }
                    )

                    ProfileOption(
                        icon = Icons.Outlined.StarOutline,
                        text = "Reseñas",
                        onClick = { openMyReviews()}
                    )

                    ProfileOption(
                        icon = Icons.Outlined.Diamond,
                        text = "Suscripción",
                        onClick = { openSubscription() }
                    )

                    ProfileOption(
                        icon = Icons.Outlined.VolunteerActivism,
                        text = "ONGs Afiliadas",
                        onClick = { openDonationsScreen() }
                    )

                    HorizontalDivider(color = Color(0xFFF2F2F2), thickness = 2.dp, modifier = Modifier.padding(vertical = 12.dp))

                    ProfileOption(
                        icon = Icons.Outlined.Logout,
                        text = "Cerrar Sesión",
                        onClick = {
                            showLogoutDialog = true
                        }
                    )
                }
            }
        }
    )

}

@Composable
fun ProfileOption(icon: ImageVector, text: String, onClick: () -> Unit = {}) {
    val isLogout = text == "Cerrar Sesión" || text == "Eliminar Cuenta"
    val iconTint = if (isLogout) Color.Red else Color.Gray
    val textColor = if (isLogout) Color.Red else Color(0xFF333333)

    Row(
        modifier = Modifier
            .clickable { onClick() }
            .padding(16.dp, 14.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(25.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = text,
            fontSize = 18.sp,
            color = textColor,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(28.dp)
        )
    }
}


