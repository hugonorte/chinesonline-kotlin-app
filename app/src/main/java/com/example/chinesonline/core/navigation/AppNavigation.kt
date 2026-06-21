package com.example.chinesonline.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chinesonline.feature_auth.ui.LoginScreen
import com.example.chinesonline.feature_auth.ui.RegisterScreen
import com.example.chinesonline.feature_auth.ui.SplashScreen
import com.example.chinesonline.feature_quiz.ui.QuizScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(onNavigateToLogin = {
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }
        composable("login") {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                onNavigateToHome = {
                    navController.navigate("quiz") { popUpTo("login") { inclusive = true } }
                }
            )
        }
        composable("register") {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack("login", false)
                },
                onNavigateToHome = {
                    navController.navigate("quiz") { popUpTo("login") { inclusive = true } }
                }
            )
        }
        composable("quiz") {
            QuizScreen(
                onNavigateBack = {
                    navController.navigate("login") { popUpTo("quiz") { inclusive = true } }
                }
            )
        }
    }
}
