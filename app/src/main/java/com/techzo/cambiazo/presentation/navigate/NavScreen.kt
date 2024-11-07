package com.techzo.cambiazo.presentation.navigate

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.techzo.cambiazo.domain.Product
import com.techzo.cambiazo.presentation.articles.ArticlesScreen
import com.techzo.cambiazo.presentation.articles.publish.PublishScreen
import com.techzo.cambiazo.presentation.explorer.productdetails.ProductDetailsScreen
import com.techzo.cambiazo.presentation.exchanges.exchangedetails.ExchangeDetailsScreen
import com.techzo.cambiazo.presentation.exchanges.ExchangeScreen
import com.techzo.cambiazo.presentation.explorer.ExplorerScreen
import com.techzo.cambiazo.presentation.explorer.filter.FilterScreen
import com.techzo.cambiazo.presentation.auth.login.SignInScreen
import com.techzo.cambiazo.presentation.explorer.offer.ConfirmationOfferScreen
import com.techzo.cambiazo.presentation.explorer.offer.MakeOfferScreen
import com.techzo.cambiazo.presentation.profile.ProfileScreen
import com.techzo.cambiazo.presentation.profile.editprofile.EditProfileScreen
import com.techzo.cambiazo.presentation.profile.favorites.FavoritesScreen
import com.techzo.cambiazo.presentation.profile.myreviews.MyReviewsScreen
import com.techzo.cambiazo.presentation.auth.register.SignUpScreen
import com.techzo.cambiazo.presentation.auth.register.TyC.TermsAndConditionsScreen
import com.techzo.cambiazo.presentation.explorer.review.ReviewScreen

sealed class ItemsScreens(val icon: ImageVector,val iconSelected: ImageVector, val title: String,val route: String, val navigate: () -> Unit = {}) {
    data class Explorer(val onNavigate: () -> Unit = {}) : ItemsScreens(
        iconSelected = Icons.Outlined.Search,
        icon = Icons.Filled.Search,
        title = "Explorar",
        navigate = onNavigate,
        route = Routes.Explorer.route

    )

    data class Exchange(val onNavigate: () -> Unit = {}) : ItemsScreens(
        iconSelected = Icons.Filled.SwapHoriz,
        icon = Icons.Outlined.SwapHoriz,
        title = "Intercambios",
        navigate = onNavigate,
        route = Routes.Exchange.route
    )

    data class Publish(val onNavigate: () -> Unit = {}) : ItemsScreens(
        iconSelected = Icons.Filled.AddCircle,
        icon = Icons.Outlined.AddCircleOutline,
        title = "Publicar",
        navigate = onNavigate,
        route = Routes.Publish.route
    )

    data class Articles(val onNavigate: () -> Unit = {}) : ItemsScreens(
        iconSelected = Icons.Filled.Sell,
        icon = Icons.Outlined.Sell,
        title = "Mis Artículos",
        navigate = onNavigate,
        route = Routes.Article.route
    )


    data class Profile(val onNavigate: () -> Unit = {}) : ItemsScreens(
        iconSelected = Icons.Filled.Person,
        icon = Icons.Outlined.Person,
        title = "Perfil",
        navigate = onNavigate,
        route = Routes.Profile.route
    )
}

sealed class Routes(val route: String) {
    object SignUp : Routes("SignUpScreen")
    object SignIn : Routes("SignInScreen")
    object Filter : Routes("FilterScreen")
    object Explorer : Routes("ExplorerScreen")
    object Article : Routes("ArticleScreen")
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

    object MakeOffer : Routes("MakeOfferScreen/{desiredProductId}") {
        fun createMakeOfferRoute(desiredProductId: String) =
            "MakeOfferScreen/$desiredProductId"
    }

    object ConfirmationOffer : Routes("ConfirmationOfferScreen/{desiredProductId}/{offeredProductId}") {
        fun createConfirmationOfferRoute(desiredProductId: String, offeredProductId: String) =
            "ConfirmationOfferScreen/$desiredProductId/$offeredProductId"
    }

    object EditProfile : Routes("EditProfileScreen")
    object MyReviews : Routes("MyReviewsScreen")
    object Publish : Routes("PublishScreen")
    object Favorites : Routes("FavoritesScreen")
}

@Composable
fun NavScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route?:""

    val items = listOf(
        ItemsScreens.Explorer(onNavigate = { navController.navigate(Routes.Explorer.route) }),
        ItemsScreens.Exchange(onNavigate = { navController.navigate(Routes.Exchange.route) }),
        ItemsScreens.Publish(onNavigate = {
            navController.currentBackStackEntry?.savedStateHandle?.set("product", null)
            navController.navigate(Routes.Publish.route)
        }),
        ItemsScreens.Articles(onNavigate = { navController.navigate(Routes.Article.route) }),
        ItemsScreens.Profile(onNavigate = { navController.navigate(Routes.Profile.route) })
    )

    NavHost(navController = navController, startDestination = Routes.SignIn.route) {
        composable(route = Routes.SignUp.route) {
            SignUpScreen(
                back = { navController.popBackStack() },
                openLogin = { navController.navigate(Routes.SignIn.route) },
                navigateToTermsAndConditions = { navController.navigate(Routes.TermsAndConditions.route) },
                openApp = { navController.navigate(Routes.Explorer.route) }
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
                bottomBar =  BottomBarNavigation(items,currentRoute) ,
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
                bottomBar = BottomBarNavigation(items,currentRoute) ,
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
                bottomBar = BottomBarNavigation(items,currentRoute) ,
                editProduct = {
                    navController.currentBackStackEntry?.savedStateHandle?.set("product", it)
                    navController.navigate(Routes.Publish.route)
                            },
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
                bottomBar = BottomBarNavigation(items,currentRoute)
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
                deleteAccount = {
                    navController.navigate(Routes.SignIn.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
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

        composable(route = Routes.Reviews.route) {
            ReviewScreen(navController = navController)
        }

        composable(route = Routes.ProductDetails.route) {
            ProductDetailsScreen(
                navController = navController
            )
        }

        composable(
            route = Routes.Publish.route,
        ){
            PublishScreen(
                back = {navController.popBackStack()},
                openMyArticles = {navController.navigate(Routes.Article.route)},
                product = navController.previousBackStackEntry?.savedStateHandle?.get<Product>("product")
            )
        }

        composable(route = Routes.MakeOffer.route) {
            MakeOfferScreen(
                navController = navController
            )
        }

        composable(route = Routes.ConfirmationOffer.route) {
            ConfirmationOfferScreen(
                navController = navController
            )
        }
    }
}

