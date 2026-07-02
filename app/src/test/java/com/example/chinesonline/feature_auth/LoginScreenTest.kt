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
import com.example.chinesonline.R

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
                onNavigateToForgotPassword = {},
                onNavigateToHome = {}
            )
        }

        // Verifica se os placeholders essenciais estão visíveis
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.email_label)).assertExists()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.password_label)).assertExists()
        
        // Verifica se os botões e links existem
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.login_button)).assertExists()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.register_link)).assertExists()
    }

    @Test
    fun loginScreen_inputWorks() {
        composeTestRule.setContent {
            LoginScreen(
                onNavigateToRegister = {},
                onNavigateToForgotPassword = {},
                onNavigateToHome = {}
            )
        }

        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.email_label)).performTextInput("teste@teste.com")
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.password_label)).performTextInput("123456")
        
        // Tenta realizar um click para ativar o ViewModel/Loading
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.login_button)).performClick()
    }
}
