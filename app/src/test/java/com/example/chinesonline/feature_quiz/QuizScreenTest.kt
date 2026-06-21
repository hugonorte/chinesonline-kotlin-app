package com.example.chinesonline.feature_quiz

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.chinesonline.feature_quiz.ui.QuizScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class QuizScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun quizScreen_gameplayRendersCorrectly() {
        composeTestRule.setContent {
            QuizScreen(onNavigateBack = {})
        }

        // Aguarda os 1500ms do Loading (simulação de delay) e verifica renderização inicial do Gameplay
        // NOTA: Para testes robustos de delay seria necessário manipular o TestDispatcher do CoroutineContext,
        // mas o composeTestRule aguarda a UI ficar "Idle" se configurado corretamente.

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodes(androidx.compose.ui.test.hasText("SCORE")).fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("SCORE").assertExists()
        composeTestRule.onNodeWithText("NÍVEL").assertExists()
        composeTestRule.onNodeWithText("Que ideograma é esse?").assertExists()
        composeTestRule.onNodeWithText("好").assertExists()
    }

    @Test
    fun quizScreen_submitCorrectAnswerShowsFeedback() {
        composeTestRule.setContent {
            QuizScreen(onNavigateBack = {})
        }

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodes(androidx.compose.ui.test.hasText("SCORE")).fetchSemanticsNodes().isNotEmpty()
        }

        // Input
        composeTestRule.onNodeWithText("Digite aqui o pin yin").performTextInput("hao")
        composeTestRule.onNodeWithText("Enviar").performClick()

        // Após Enviar, o card de feedback deve aparecer
        composeTestRule.onNodeWithText("Correto!").assertExists()
        composeTestRule.onNodeWithText("+ 20 pts").assertExists()
    }
}
