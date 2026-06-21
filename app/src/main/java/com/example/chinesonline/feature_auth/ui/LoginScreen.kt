package com.example.chinesonline.feature_auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chinesonline.core.ui.theme.*
import androidx.compose.ui.platform.LocalView
import android.app.Activity
import androidx.compose.ui.graphics.toArgb

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = viewModel(),
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val loginState by viewModel.loginState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val loginSuccess by viewModel.loginSuccess.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(loginSuccess) {
        if (loginSuccess) {
            onNavigateToHome()
        }
    }

    LaunchedEffect(loginState) {
        loginState?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearState()
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = SplashGradientStart.toArgb()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            SplashGradientStart,
                            SplashGradientCenter,
                            SplashGradientEnd
                        )
                    )
                )
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "ChinêsOnline",
                    style = MaterialTheme.typography.displayLarge,
                    fontFamily = LobsterFontFamily,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 48.dp)
                )

                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("E-mail", color = Color.White) },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.White
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )

                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Senha", color = Color.White) },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.White
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
                )

                Button(
                    onClick = { viewModel.doLogin(email, password) },
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2A7FFF),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 48.dp, vertical = 12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Entrar", fontSize = 18.sp)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Ainda não possui cadastro? ", color = Color.White, fontSize = 14.sp)
                    Text(
                        text = "Cadastre-se",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        modifier = Modifier.clickable {
                            onNavigateToRegister()
                        }
                    )
                }
            }
        }
    }
}
