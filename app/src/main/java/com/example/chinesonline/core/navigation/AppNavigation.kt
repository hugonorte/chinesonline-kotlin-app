package com.example.chinesonline.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.chinesonline.feature_auth.ui.LoginScreen
import com.example.chinesonline.feature_auth.ui.RegisterScreen
import com.example.chinesonline.feature_auth.ui.SplashScreen
import com.example.chinesonline.feature_home.ui.HomeScreen
import com.example.chinesonline.feature_quiz.ui.QuizScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    val appContainer = (context.applicationContext as com.example.chinesonline.ChinesOnlineApplication).container
    val userPrefs = appContainer.userPreferencesRepository
    val quizRepo = appContainer.quizRepository

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
                    scope.launch {
                        userPrefs.clear()
                        quizRepo.clearLocalData()
                    }
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("login") { popUpTo(0) }
                }
            )
        }
        composable("quiz") {
            QuizScreen(
                onLogout = {
                    scope.launch {
                        userPrefs.clear()
                        quizRepo.clearLocalData()
                    }
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("login") { popUpTo(0) }
                }
            )
        }
    }
}
