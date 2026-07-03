package com.example.chinesonline.feature_auth.ui

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chinesonline.R
import com.example.chinesonline.core.ui.theme.*

@Composable
fun ResetPasswordScreen(
    viewModel: AuthViewModel = viewModel(),
    oobCode: String,
    onNavigateToLogin: () -> Unit
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val success by viewModel.resetPasswordSuccess.collectAsState()
    val errorState by viewModel.loginState.collectAsState()
    
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorState) {
        errorState?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearState()
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        DisposableEffect(Unit) {
            val window = (view.context as Activity).window
            window.statusBarColor = SplashGradientStart.toArgb()
            onDispose {}
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
            Image(
                painter = painterResource(id = R.drawable.cat_paper),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = (-24).dp),
                contentScale = ContentScale.Fit
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.reset_password),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                if (success) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color.White.copy(alpha = 0.9f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(24.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.password_reset_success),
                            color = Color(0xFF340202),
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Button(
                            onClick = onNavigateToLogin,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2A7FFF),
                                contentColor = Color.White
                            )
                        ) {
                            Text(stringResource(id = R.string.login_button))
                        }
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color.White.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 24.dp)
                    ) {
                        TextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = { Text(stringResource(id = R.string.new_password), color = Color(0xFF340202)) },
                            visualTransformation = PasswordVisualTransformation(),
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = Color(0xFF340202),
                                unfocusedTextColor = Color(0xFF340202),
                                focusedContainerColor = Color.White.copy(alpha = 0.4f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.4f),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(50),
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color(0xFF340202)
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(66.dp)
                                .padding(bottom = 8.dp)
                        )

                        TextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text(stringResource(id = R.string.confirm_password_label), color = Color(0xFF340202)) },
                            visualTransformation = PasswordVisualTransformation(),
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = Color(0xFF340202),
                                unfocusedTextColor = Color(0xFF340202),
                                focusedContainerColor = Color.White.copy(alpha = 0.4f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.4f),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(50),
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color(0xFF340202)
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(66.dp)
                                .padding(bottom = 16.dp)
                        )

                        Button(
                            onClick = {
                                if (newPassword == confirmPassword) {
                                    viewModel.doConfirmPasswordReset(oobCode, newPassword)
                                } else {
                                    // Normally we should show an error string here, but for simplicity we rely on viewmodel or do a quick check
                                }
                            },
                            enabled = !isLoading && newPassword.isNotBlank() && newPassword == confirmPassword,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2A7FFF),
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text(stringResource(id = R.string.reset_password), fontSize = 16.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        TextButton(onClick = onNavigateToLogin) {
                            Text(stringResource(id = R.string.cancel_button), color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
