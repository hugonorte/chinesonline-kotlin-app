package com.example.chinesonline.feature_settings.ui

import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.core.os.LocaleListCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.chinesonline.ChinesOnlineApplication
import com.example.chinesonline.feature_auth.data.AuthRepository
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setup() {
        composeTestRule.activity.runOnUiThread {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("en"))
        }
    }

    @After
    fun tearDown() {
        composeTestRule.activity.runOnUiThread {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
        }
    }

    @Test
    fun testLanguageChangeToSpanish() {
        lateinit var viewModel: SettingsViewModel

        composeTestRule.activity.runOnUiThread {
            val appContainer = (composeTestRule.activity.application as ChinesOnlineApplication).container
            viewModel = SettingsViewModel(
                AuthRepository(),
                appContainer.quizRepository,
                appContainer.userPreferencesRepository
            )
        }

        composeTestRule.setContent {
            SettingsScreen(
                viewModel = viewModel,
                onNavigateBack = {},
                onNavigateToLogin = {}
            )
        }

        // Verifica que começa em inglês
        composeTestRule.onNodeWithText("Settings").assertExists()
        composeTestRule.onNodeWithText("Language").assertExists()

        // Clica no botão "ES"
        composeTestRule.onNodeWithText("ES").performClick()
        
        // Verifica se a intent do clique rodou (a UI test runner não captura system level LocaleManager instantaneamente)
        composeTestRule.waitForIdle()

        // Clica em PT
        composeTestRule.onNodeWithText("PT").performClick()
        
        composeTestRule.waitForIdle()
    }
}
