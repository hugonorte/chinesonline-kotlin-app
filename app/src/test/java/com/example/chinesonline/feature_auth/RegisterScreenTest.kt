package com.example.chinesonline.feature_auth

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.chinesonline.feature_auth.ui.RegisterScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import com.example.chinesonline.R
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst

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
                onNavigateToLogin = {}
            )
        }

        // Verifica se o título aparece
        composeTestRule.onAllNodesWithText(composeTestRule.activity.getString(R.string.register_button)).onFirst().assertExists()

        // Verifica os campos básicos
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.name_label)).assertExists()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.email_label)).assertExists()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.password_label)).assertExists()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.confirm_password_label)).assertExists()
        
        // Verifica Data de Nascimento (por ser um clickable field com Text default)
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.dob_label)).assertExists()

        // O Dropdown de País por padrão exibe Brasil
        composeTestRule.onNodeWithText("🇧🇷 Brasil").assertExists()
    }
}
