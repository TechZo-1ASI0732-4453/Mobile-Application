package com.techzo.cambiazo.presentation.navigate


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.techzo.cambiazo.presentation.explorer.ExplorerScreen

sealed class ItemsScreens(val icon: ImageVector, val title: String, val navigate: () -> Unit = {}) {
    data class Explorer(val onNavigate: () -> Unit = {}) : ItemsScreens(
        icon = Icons.Filled.Search,
        title = "Explorar",
        navigate = onNavigate
    )

    data class Exchange(val onNavigate: () -> Unit = {}) : ItemsScreens(
        icon = Icons.Filled.SyncAlt,
        title = "Intercambios",
        navigate = onNavigate
    )

    data class Articles(val onNavigate: () -> Unit = {}) : ItemsScreens(
        icon = Icons.Filled.AddCircle,
        title = "Mis Articulos",
        navigate = onNavigate
    )

    data class Favorite(val onNavigate: () -> Unit = {}) : ItemsScreens(
        icon = Icons.Filled.FavoriteBorder,
        title = "Favoritos",
        navigate = onNavigate
    )

    data class Profile(val onNavigate: () -> Unit = {}) : ItemsScreens(
        icon = Icons.Filled.Person,
        title = "Perfil",
        navigate = onNavigate
    )

}

sealed class Screens(val route: String){
    data object Explorer: Screens("ExplorerScreen")
    data object Exchange: Screens("ExchangeScreen")
    data object Articles: Screens("ArticlesScreen")
    data object Favorite: Screens("FavoriteScreen")
    data object Profile: Screens("ProfileScreen")

}

@Composable
fun MainApp(){

    val navController = rememberNavController()

    val items = listOf(
        ItemsScreens.Explorer(onNavigate = { navController.navigate(Screens.Explorer.route) }),
        ItemsScreens.Exchange(onNavigate = { navController.navigate(Screens.Exchange.route) }),
        ItemsScreens.Articles(onNavigate = { navController.navigate(Screens.Articles.route) }),
        ItemsScreens.Favorite(onNavigate = { navController.navigate(Screens.Favorite.route) }),
        ItemsScreens.Profile(onNavigate = { navController.navigate(Screens.Profile.route) })
    )

    Scaffold(
        bottomBar = { BottomBarNavigation(items) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            NavHost(navController = navController, startDestination = Screens.Explorer.route) {
                composable(route = Screens.Explorer.route) {
                    ExplorerScreen()
                }

            }
        }
    }
}
