package com.example.chinesonline.feature_auth

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.chinesonline.feature_auth.ui.LoginScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @org.junit.Before
    fun setup() {
        if (com.google.firebase.FirebaseApp.getApps(androidx.test.core.app.ApplicationProvider.getApplicationContext()).isEmpty()) {
            com.google.firebase.FirebaseApp.initializeApp(androidx.test.core.app.ApplicationProvider.getApplicationContext())
        }
    }

    @Test
    fun loginScreen_rendersInputsAndButton() {
        composeTestRule.setContent {
            LoginScreen(
                onNavigateToRegister = {},
                onNavigateToHome = {}
            )
        }

        // Verifica se os placeholders essenciais estão visíveis
        composeTestRule.onNodeWithText("E-mail").assertExists()
        composeTestRule.onNodeWithText("Senha").assertExists()
        
        // Verifica se os botões e links existem
        composeTestRule.onNodeWithText("Entrar").assertExists()
        composeTestRule.onNodeWithText("Cadastre-se").assertExists()
    }

    @Test
    fun loginScreen_inputWorks() {
        composeTestRule.setContent {
            LoginScreen(
                onNavigateToRegister = {},
                onNavigateToHome = {}
            )
        }

        composeTestRule.onNodeWithText("E-mail").performTextInput("teste@teste.com")
        composeTestRule.onNodeWithText("Senha").performTextInput("123456")
        
        // Tenta realizar um click para ativar o ViewModel/Loading
        composeTestRule.onNodeWithText("Entrar").performClick()
    }
}
