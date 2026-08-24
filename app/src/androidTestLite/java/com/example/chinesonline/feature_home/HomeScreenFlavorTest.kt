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
    fun testAdvancedSettingsButton_inLiteFlavor() {
        val activity = composeTestRule.activity
        
        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalAppConfig provides AppConfigProvider.provide()
            ) {
                HomeScreen(onNavigateToQuiz = {}, onSettingsClick = {})
            }
        }

        // TDD: Verifica usando a string internacionalizada em vez de hardcoded
        val btnText = activity.getString(R.string.advanced_settings)
        composeTestRule.onNodeWithText(btnText).performClick()

        val expectedMessage = activity.getString(R.string.lite_blocked_feature)
        composeTestRule.onNodeWithText(expectedMessage).assertIsDisplayed()
    }
}
