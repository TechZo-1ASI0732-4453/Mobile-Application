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
import com.techzo.cambiazo.presentation.articles.ArticlesScreen
import com.techzo.cambiazo.presentation.articles.PublishScreen
import com.techzo.cambiazo.presentation.details.ProductDetailsScreen
import com.techzo.cambiazo.presentation.exchanges.ExchangeDetailsScreen
import com.techzo.cambiazo.presentation.exchanges.ExchangeScreen
import com.techzo.cambiazo.presentation.explorer.ExplorerScreen
import com.techzo.cambiazo.presentation.filter.FilterScreen
import com.techzo.cambiazo.presentation.login.SignInScreen
import com.techzo.cambiazo.presentation.profile.ProfileScreen
import com.techzo.cambiazo.presentation.profile.editprofile.EditProfileScreen
import com.techzo.cambiazo.presentation.profile.favorites.FavoritesScreen
import com.techzo.cambiazo.presentation.profile.myreviews.MyReviewsScreen
import com.techzo.cambiazo.presentation.register.SignUpScreen
import com.techzo.cambiazo.presentation.register.TermsAndConditionsScreen
import com.techzo.cambiazo.presentation.review.ReviewScreen

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
        title = "Mis Artículos",
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

sealed class Routes(val route: String) {
    object SignUp : Routes("SignUpScreen")
    object SignIn : Routes("SignInScreen")
    object Filter : Routes("FilterScreen")
    object Explorer : Routes("ExplorerScreen")
    object Article : Routes("ArticleScreen")
    object Donation : Routes("DonationScreen")
    object Profile : Routes("ProfileScreen")
    object Exchange : Routes("ExchangeScreen")
    object TermsAndConditions : Routes("TermsAndConditionsScreen")
    object ExchangeDetails : Routes("ExchangeDetailsScreen/{exchangeId}/{page}") {
        fun createExchangeDetailsRoute(exchangeId: String, page: String) = "ExchangeDetailsScreen/$exchangeId/$page"
    }
    object ProductDetails : Routes("ProductDetailsScreen/{productId}/{userId}") {
        fun createProductDetailsRoute(productId: String, userId: String) = "ProductDetailsScreen/$productId/$userId"
    }
    object Reviews : Routes("ReviewsScreen/{userId}") {
        fun createRoute(userId: String) = "ReviewsScreen/$userId"
    }
    object EditProfile : Routes("EditProfileScreen")
    object MyReviews : Routes("MyReviewsScreen")
    object Publish : Routes("PublishScreen")
    object Favorites : Routes("FavoritesScreen")
}

@Composable
fun NavScreen() {
    val navController = rememberNavController()

    val items = listOf(
        ItemsScreens.Explorer(onNavigate = { navController.navigate(Routes.Explorer.route) }),
        ItemsScreens.Exchange(onNavigate = { navController.navigate(Routes.Exchange.route) }),
        ItemsScreens.Articles(onNavigate = { navController.navigate(Routes.Article.route) }),
        ItemsScreens.Donation(onNavigate = { navController.navigate(Routes.Donation.route) }),
        ItemsScreens.Profile(onNavigate = { navController.navigate(Routes.Profile.route) })
    )

    NavHost(navController = navController, startDestination = Routes.SignIn.route) {
        composable(route = Routes.SignUp.route) {
            SignUpScreen(
                back = { navController.popBackStack() },
                openLogin = { navController.navigate(Routes.SignIn.route) },
                navigateToTermsAndConditions = { navController.navigate(Routes.TermsAndConditions.route) }
            )
        }

        composable(route = Routes.SignIn.route) {
            SignInScreen(
                openRegister = { navController.navigate(Routes.SignUp.route) },
                openApp = { navController.navigate(Routes.Explorer.route) }
            )
        }

        composable(route = Routes.Explorer.route) {
            ExplorerScreen(
                bottomBar = { BottomBarNavigation(items) },
                onFilter = { navController.navigate(Routes.Filter.route) },
                onProductClick = { productId, userId ->
                    navController.navigate(
                        Routes.ProductDetails.createProductDetailsRoute(
                            productId.toString(),
                            userId.toString()
                        )
                    )
                }
            )
        }

        composable(route = Routes.Filter.route) {
            FilterScreen(
                back = { navController.popBackStack() },
                openExplorer = { navController.navigate(Routes.Explorer.route) }
            )
        }

        composable(route = Routes.Exchange.route) {
            ExchangeScreen(
                bottomBar = { BottomBarNavigation(items) },
                goToDetailsScreen = { exchangeId, page ->
                    navController.navigate(
                        Routes.ExchangeDetails.createExchangeDetailsRoute(
                            exchangeId,
                            page
                        )
                    )
                }
            )
        }

        composable(route = Routes.Article.route) {
            ArticlesScreen(
                bottomBar = { BottomBarNavigation(items) },
                onPublish = {navController.navigate(Routes.Publish.route)}
            )
        }

        composable(route = Routes.ExchangeDetails.route) { backStackEntry ->
            val exchange = backStackEntry.arguments?.getString("exchangeId")?.toIntOrNull()
            val page = backStackEntry.arguments?.getString("page")?.toIntOrNull()
            if (exchange != null && page != null) {
                ExchangeDetailsScreen(
                    goBack = { navController.popBackStack() },
                    exchangeId = exchange,
                    page = page
                )
            }
        }

        composable(route = Routes.Profile.route) {
            ProfileScreen(
                logOut = {
                    navController.navigate(Routes.SignIn.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                openMyReviews = { navController.navigate(Routes.MyReviews.route) },
                openEditProfile = { navController.navigate(Routes.EditProfile.route) },
                openFavorites = { navController.navigate(Routes.Favorites.route) },
                bottomBar = { BottomBarNavigation(items) }
            )
        }

        composable(route = Routes.TermsAndConditions.route) {
            TermsAndConditionsScreen(back = { navController.popBackStack() })
        }

        composable(route = Routes.MyReviews.route) {
            MyReviewsScreen(
                back = { navController.popBackStack() },
                OnUserClick = { userId ->
                    navController.navigate(Routes.Reviews.createRoute(userId.toString()))
                }
            )
        }
        composable(route = Routes.EditProfile.route) {
            EditProfileScreen(
                back = { navController.popBackStack() }
            )
        }

        composable(route = Routes.Favorites.route) {
            FavoritesScreen(
                back = { navController.popBackStack() },
                onProductClick = { productId, userId ->
                    navController.navigate(
                        Routes.ProductDetails.createProductDetailsRoute(
                            productId,
                            userId
                        )
                    )
                }
            )
        }

        composable(route = Routes.Reviews.route) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull()
            if (userId != null) {
                ReviewScreen(
                    userId = userId,
                    onBack = { navController.popBackStack() },
                    onProductClick = { productId,productUserid ->
                        navController.navigate(
                            Routes.ProductDetails.createProductDetailsRoute(
                                productId.toString(),
                                productUserid.toString()
                            )
                        )
                    },
                    onUserClick = { selectedUserId ->
                        navController.navigate(Routes.Reviews.createRoute(selectedUserId.toString()))
                    }
                )
            }
        }

        composable(route = Routes.ProductDetails.route) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull()
            val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull()
            if (productId != null && userId != null) {
                ProductDetailsScreen(
                    productId = productId,
                    userId = userId,
                    onBack = { navController.popBackStack() },
                    onUserClick = { selectedUserId ->
                        navController.navigate(Routes.Reviews.createRoute(selectedUserId.toString()))
                    }
                )
            }
        }

        composable(route = Routes.Publish.route){
            PublishScreen(
                back = {navController.popBackStack()},
                openMyArticles = {navController.navigate(Routes.Article.route)}
            )
        }
    }
}