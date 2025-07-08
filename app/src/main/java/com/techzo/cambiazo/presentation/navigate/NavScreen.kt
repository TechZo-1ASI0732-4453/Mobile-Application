package com.techzo.cambiazo.presentation.navigate

import android.net.Uri
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.techzo.cambiazo.MainActivity
import com.techzo.cambiazo.domain.Product
import com.techzo.cambiazo.presentation.articles.ArticlesScreen
import com.techzo.cambiazo.presentation.articles.publish.PublishScreen
import com.techzo.cambiazo.presentation.auth.changepassword.ChangePasswordScreen
import com.techzo.cambiazo.presentation.auth.changepassword.newpasswordscreen.NewPasswordScreen
import com.techzo.cambiazo.presentation.auth.changepassword.otpcodeverificationscreen.OtpCodeVerificationScreen
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
import com.techzo.cambiazo.presentation.donations.DonationScreen
import com.techzo.cambiazo.presentation.explorer.review.ReviewScreen
import com.techzo.cambiazo.presentation.profile.settings.SettingsScreen
import com.techzo.cambiazo.presentation.profile.subscription.MySubscriptionScreen
import com.techzo.cambiazo.presentation.profile.subscription.PaymentScreen
import com.techzo.cambiazo.presentation.profile.subscription.PlansScreen

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
    object Donations : Routes("DonationsScreen")
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

    object Payment : Routes("PaymentScreen/{planId}") {
        fun createSubscriptionPaymentRoute(planId: String ) = "PaymentScreen/$planId"
    }

    object EditProfile : Routes("EditProfileScreen")
    object Settings : Routes("SettingsScreen")
    object MyReviews : Routes("MyReviewsScreen")
    object Publish : Routes("PublishScreen")
    object Favorites : Routes("FavoritesScreen")
    object MySubscription : Routes("MySubscriptionScreen")
    object Plans : Routes("PlansScreen")
    object ChangePassword : Routes("ChangePasswordScreen")
    object OtpCodeVerification : Routes("OtpCodeVerificationScreen/{email}/{codeGenerated}") {
        fun createRoute(email: String, codeGenerated: String): String {
            val encodedEmail = Uri.encode(email)
            val encodedCode = Uri.encode(codeGenerated)
            return "OtpCodeVerificationScreen/$encodedEmail/$encodedCode"
        }
    }
    object NewPassword:Routes("NewPasswordScreen/{email}"){
        fun createRoute(email: String): String {
            val encodedEmail = Uri.encode(email)
            return "NewPasswordScreen/$encodedEmail"
        }
    }
}

@Composable
fun NavScreen(
    activity: FragmentActivity,
    navViewModel: NavigationViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ""

    LaunchedEffect(navViewModel.redirectToSubscription.value) {
        if (navViewModel.redirectToSubscription.value) {
            navController.navigate(Routes.Profile.route) {
                popUpTo(0) { inclusive = true }
            }
            navViewModel.resetRedirect()
        }
    }

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
                openApp = { navController.navigate(Routes.Explorer.route) },
                openForgotPassword = { navController.navigate(Routes.ChangePassword.route) }
            )
        }

        composable(route = Routes.ChangePassword.route) {
            ChangePasswordScreen(
                goBack = { navController.popBackStack() },
                goOtpCodeVerificationScreen = { email, codeGenerated ->
                    navController.navigate(Routes.OtpCodeVerification.createRoute(email,
                        codeGenerated.toString()
                    ))
                }
            )
        }

        composable(
            route = Routes.OtpCodeVerification.route,
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("codeGenerated") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val codeGenerated = backStackEntry.arguments?.getString("codeGenerated") ?: ""
            OtpCodeVerificationScreen(
                email = email,
                codeGenerated = codeGenerated,
                goBack = {
                    navController.popBackStack()
                },
                goNewPassword = { userEmail  ->
                    navController.navigate(Routes.NewPassword.createRoute(userEmail))
                }
            )
        }


        composable(route=Routes.NewPassword.route,
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
            )){ backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            NewPasswordScreen(
                email = email,
                goBack = { navController.popBackStack() },
                goSignIn = { navController.navigate(Routes.SignIn.route){
                    popUpTo(0) { inclusive = true }
                } }

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

        composable(route = Routes.Donations.route) {
            DonationScreen(
                back = { navController.popBackStack() },
                onOngClick = { /* lo que desees hacer al tocar una ONG */ },
                openDonations = { navController.navigate(Routes.Donations.route) }
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
                bottomBar = BottomBarNavigation(items, currentRoute),
                goToDetailsScreen = { exchangeId, page ->
                    navController.navigate(
                        Routes.ExchangeDetails.createExchangeDetailsRoute(
                            exchangeId,
                            page
                        )
                    )
                },
                page = navController.previousBackStackEntry?.savedStateHandle?.get<Int>("page") ?: 0,
                goToReviewScreen = { userId ->
                    navController.navigate(Routes.Reviews.createRoute(userId.toString()))
                }            )
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
                    goBack = {page->
                        navController.currentBackStackEntry?.savedStateHandle?.set("page", page)
                        navController.navigate(Routes.Exchange.route)
                             },
                    goToReviewScreen = { userId ->
                        navController.navigate(Routes.Reviews.createRoute(userId.toString()))
                    },
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
                openConfiguration = { navController.navigate(Routes.Settings.route) },
                openFavorites = { navController.navigate(Routes.Favorites.route) },
                bottomBar = BottomBarNavigation(items,currentRoute),
                openSubscription = { navController.navigate(Routes.MySubscription.route) },
                openDonationsScreen = { navController.navigate(Routes.Donations.route) }
            )
        }

        composable(route = Routes.TermsAndConditions.route) {
            TermsAndConditionsScreen(back = { navController.popBackStack() })
        }

        composable(route = Routes.MyReviews.route) {
            MyReviewsScreen(
                back = { navController.popBackStack() },
                onUserClick = { userId ->
                    navController.navigate(Routes.Reviews.createRoute(userId.toString()))
                }
            )
        }
        composable(route = Routes.EditProfile.route) {
            EditProfileScreen(
                back = { navController.popBackStack() }
            )
        }

        composable(route = Routes.Settings.route) {
            SettingsScreen(
                deleteAccount = {
                    navController.navigate(Routes.SignIn.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                changePassword = { userEmail  ->
                    navController.navigate(Routes.NewPassword.createRoute(userEmail))
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
                openSubscription = {navController.navigate(Routes.MySubscription.route)},
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

        composable(route = Routes.MySubscription.route) {
            MySubscriptionScreen(back = {navController.navigate(Routes.Profile.route)}, openPlans = {navController.navigate(Routes.Plans.route)})
        }

        composable(route = Routes.Plans.route) {
            PlansScreen(
                back = {navController.popBackStack()},
                onPlanClick = { planId ->
                    navController.navigate(
                        Routes.Payment.createSubscriptionPaymentRoute(
                            planId
                        )
                    )
                },
                goToMySubscription = {navController.navigate(Routes.MySubscription.route){
                    popUpTo(0) { inclusive = true }
                } },
                activity = activity,

            )
        }

        composable(route = Routes.Payment.route) {
            PaymentScreen(
                back = {navController.popBackStack()},
                goToMySubscription = {navController.navigate(Routes.MySubscription.route){
                    popUpTo(0) { inclusive = true }
                } })
        }
    }
}

