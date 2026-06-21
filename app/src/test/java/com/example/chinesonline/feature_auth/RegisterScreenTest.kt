package com.example.chinesonline.feature_auth

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.chinesonline.feature_auth.ui.RegisterScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RegisterScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @org.junit.Before
    fun setup() {
        if (com.google.firebase.FirebaseApp.getApps(androidx.test.core.app.ApplicationProvider.getApplicationContext()).isEmpty()) {
            com.google.firebase.FirebaseApp.initializeApp(androidx.test.core.app.ApplicationProvider.getApplicationContext())
        }
    }

    @Test
    fun registerScreen_rendersAllFormFields() {
        composeTestRule.setContent {
            RegisterScreen(
                onNavigateToLogin = {},
                onNavigateToHome = {}
            )
        }

        // Verifica se o título aparece
        composeTestRule.onNodeWithText("Cadastrar").assertExists()

        // Verifica os campos básicos
        composeTestRule.onNodeWithText("Nome").assertExists()
        composeTestRule.onNodeWithText("E-mail").assertExists()
        composeTestRule.onNodeWithText("Senha").assertExists()
        composeTestRule.onNodeWithText("Confirmar Senha").assertExists()
        
        // Verifica Data de Nascimento (por ser um clickable field com Text default)
        composeTestRule.onNodeWithText("Data de Nascimento").assertExists()

        // O Dropdown de País por padrão exibe Brasil
        composeTestRule.onNodeWithText("🇧🇷 Brasil").assertExists()
    }
}
