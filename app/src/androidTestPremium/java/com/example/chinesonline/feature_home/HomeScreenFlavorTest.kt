package com.example.chinesonline.feature_home

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.runtime.CompositionLocalProvider
import com.example.chinesonline.R
import com.example.chinesonline.core.config.LocalAppConfig
import com.example.chinesonline.core.config.AppConfigProvider
import com.example.chinesonline.feature_home.ui.HomeScreen
import org.junit.Rule
import org.junit.Test

class HomeScreenFlavorTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun testAdvancedSettingsButton_inPremiumFlavor() {
        val activity = composeTestRule.activity

        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalAppConfig provides AppConfigProvider.provide()
            ) {
                HomeScreen(onNavigateToQuiz = {}, onSettingsClick = {})
            }
        }

        val btnText = activity.getString(R.string.advanced_settings)
        composeTestRule.onNodeWithText(btnText).performClick()

        // Verifica se a mensagem correta apareceu (assumindo a msg hardcoded, que o toast de advanced access exibe)
        composeTestRule.onNodeWithText("Abrindo configurações avançadas...").assertIsDisplayed()
    }
}
