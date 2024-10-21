package com.techzo.cambiazo.presentation.navigate

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.techzo.cambiazo.presentation.explorer.ExplorerListViewModel
import com.techzo.cambiazo.presentation.explorer.ExplorerScreen
import com.techzo.cambiazo.presentation.filter.FilterScreen
import com.techzo.cambiazo.presentation.login.SignInScreen
import com.techzo.cambiazo.presentation.login.SignInViewModel
import com.techzo.cambiazo.presentation.register.SignUpScreen
import com.techzo.cambiazo.presentation.register.SignUpViewModel
import com.techzo.cambiazo.presentation.register.TermsAndConditionsScreen

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
        icon = Icons.Filled.Label,
        title = "Mis Articulos",
        navigate = onNavigate
    )

    data class Donation(val onNavigate: () -> Unit = {}) : ItemsScreens(
        icon = Icons.Filled.FavoriteBorder,
        title = "Donaciones",
        navigate = onNavigate
    )

    data class Profile(val onNavigate: () -> Unit = {}) : ItemsScreens(
        icon = Icons.Filled.Person,
        title = "Perfil",
        navigate = onNavigate
    )

}


sealed class Routes(val route: String){
    data object SignUp: Routes("SignUpScreen")
    data object SignIn: Routes("SignInScreen")
    data object Filter: Routes("FilterScreen")
    data object Explorer: Routes("ExplorerScreen")
    data object Article: Routes("ArticleScreen")
    data object Donation: Routes("DonationScreen")
    data object Profile: Routes("ProfileScreen")
    data object Exchange: Routes("ExchangeScreen")
    data object TermsAndConditions: Routes("TermsAndConditionsScreen")
}

@Composable
fun NavScreen(viewModelAuth: SignInViewModel, viewModelProduct: ExplorerListViewModel, viewModelSignUp: SignUpViewModel){
    val navController = rememberNavController()

    val items = listOf(
        ItemsScreens.Explorer(onNavigate = { navController.navigate(Routes.Explorer.route) }),
        ItemsScreens.Exchange(onNavigate = { navController.navigate(Routes.Exchange.route) }),
        ItemsScreens.Articles(onNavigate = { navController.navigate(Routes.Article.route) }),
        ItemsScreens.Donation(onNavigate = { navController.navigate(Routes.Donation.route) }),
        ItemsScreens.Profile(onNavigate = { navController.navigate(Routes.Profile.route) })
    )

    val viewModelAuth = viewModelAuth

    val viewModelProduct = viewModelProduct

    val viewModelSignUp = viewModelSignUp


    NavHost(navController = navController, startDestination = Routes.SignIn.route){

        composable(route = Routes.SignUp.route){
            SignUpScreen(
                back = { navController.popBackStack() },
                openLogin = { navController.navigate(Routes.SignIn.route) },
                viewModel = viewModelSignUp,
                navigateToTermsAndConditions = { navController.navigate(Routes.TermsAndConditions.route) }
            )
        }

        composable(route = Routes.SignIn.route){
            SignInScreen(
                    openRegister = { navController.navigate(Routes.SignUp.route)},
                openApp = {navController.navigate(Routes.Explorer.route)},
                viewModel = viewModelAuth
            )
        }

        composable(route=Routes.Explorer.route){
            ExplorerScreen(
                viewModel= viewModelProduct,
                bottomBar = {BottomBarNavigation(items)},
                onFilter = {navController.navigate(Routes.Filter.route)}
            )
        }

        composable(route=Routes.Filter.route){
            FilterScreen(back = {navController.popBackStack()})
        }

        composable(route = Routes.TermsAndConditions.route) {  // Agregamos la pantalla de términos y condiciones
            TermsAndConditionsScreen(back = { navController.popBackStack() })
        }

    }
}

