package com.example.chinesonline.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chinesonline.feature_auth.ui.LoginScreen
import com.example.chinesonline.feature_auth.ui.RegisterScreen
import com.example.chinesonline.feature_auth.ui.SplashScreen
import com.example.chinesonline.feature_home.ui.HomeScreen
import com.example.chinesonline.feature_quiz.ui.QuizScreen
import com.google.firebase.auth.FirebaseAuth

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
                onNavigateToForgotPassword = {
                    navController.navigate("forgot_password")
                },
                onNavigateToHome = {
                    navController.navigate("home") { popUpTo("login") { inclusive = true } }
                }
            )
        }
        composable("register") {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack("login", false)
                }
            )
        }
        composable("forgot_password") {
            com.example.chinesonline.feature_auth.ui.ForgotPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = "reset_password?mode={mode}&oobCode={oobCode}",
            deepLinks = listOf(
                androidx.navigation.navDeepLink {
                    uriPattern = "https://chinesonline-prod.firebaseapp.com/__/auth/action?mode={mode}&oobCode={oobCode}"
                }
            )
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode")
            val oobCode = backStackEntry.arguments?.getString("oobCode")
            
            if (mode == "resetPassword" && oobCode != null) {
                com.example.chinesonline.feature_auth.ui.ResetPasswordScreen(
                    oobCode = oobCode,
                    onNavigateToLogin = {
                        navController.navigate("login") { popUpTo(0) }
                    }
                )
            } else {
                androidx.compose.runtime.LaunchedEffect(Unit) {
                    navController.navigate("login") { popUpTo(0) }
                }
            }
        }
        composable("home") {
            HomeScreen(
                onNavigateToQuiz = {
                    navController.navigate("quiz")
                },
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("login") { popUpTo(0) }
                }
            )
        }
        composable("quiz") {
            QuizScreen(
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("login") { popUpTo(0) }
                }
            )
        }
    }
}
