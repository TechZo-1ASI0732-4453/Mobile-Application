package com.techzo.cambiazo.presentation.navigate

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.techzo.cambiazo.presentation.login.LoginScreen
import com.techzo.cambiazo.presentation.login.SignInViewModel
import com.techzo.cambiazo.presentation.register.SingInScreen

@Composable
fun NavScreen(viewModel: SignInViewModel){
    val navController = rememberNavController()

    val viewModel = viewModel

    NavHost(navController = navController, startDestination = Routes.Login.route ){

        composable(route = Routes.SingIn.route){
            SingInScreen(
                back = { navController.popBackStack()},
                openLogin = {navController.navigate(Routes.Login.route)}
            )
        }

        composable(route = Routes.Login.route){
            LoginScreen(
                openRegister = { navController.navigate(Routes.SingIn.route)},
                openApp = {navController.navigate(Routes.MainApp.route)},
                viewModel = viewModel
            )
        }

        composable(route=Routes.MainApp.route){
            MainApp()
        }
    }
}


sealed class Routes(val route: String){
    data object Login: Routes("LoginScreen")
    data object SingIn: Routes("SingInScreen")
    data object MainApp: Routes("MainApp")
}