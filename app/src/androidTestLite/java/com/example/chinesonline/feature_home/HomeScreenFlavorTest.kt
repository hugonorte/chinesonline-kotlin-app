package com.example.chinesonline.feature_home

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.runtime.CompositionLocalProvider
import com.example.chinesonline.core.config.LocalAppConfig
import com.example.chinesonline.core.config.AppConfigProvider
import com.example.chinesonline.feature_home.ui.HomeScreen
import org.junit.Rule
import org.junit.Test

class HomeScreenFlavorTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testAdvancedSettingsButton_inLiteFlavor() {
        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalAppConfig provides AppConfigProvider.provide()
            ) {
                HomeScreen(onNavigateToQuiz = {}, onLogout = {})
            }
        }

        // Clica no botão
        composeTestRule.onNodeWithText("Configurações Avançadas").performClick()

        // Verifica se a mensagem de bloqueio (exclusiva do Lite) apareceu
        composeTestRule.onNodeWithText("Funcionalidade exclusiva da versão Premium").assertIsDisplayed()
    }
}
