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
import com.example.chinesonline.R
import com.example.chinesonline.core.data.local.AppDatabase
import com.example.chinesonline.core.data.local.LocalIdeogramStat
import com.example.chinesonline.core.utils.HashUtils
import kotlinx.coroutines.runBlocking

@RunWith(RobolectricTestRunner::class)
class QuizScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @org.junit.Before
    fun setupDb() {
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        if (com.google.firebase.FirebaseApp.getApps(context).isEmpty()) {
            com.google.firebase.FirebaseApp.initializeApp(context)
        }
        val db = AppDatabase.getDatabase(context)
        val dao = db.ideogramStatDao()
        runBlocking {
            for (i in 1..10) {
                dao.upsertStat(
                    LocalIdeogramStat(
                        id = "${i}_pinyin_without_tone",
                        ideogramId = i,
                        gameType = "pinyin_without_tone",
                        character = "好",
                        pinyin = "hao",
                        translation = "good",
                        salt = "abc",
                        hash = HashUtils.sha256("haoabc"),
                        correctAttempts = 0,
                        wrongAttempts = 0,
                        lastReviewed = 0L,
                        interval = 0,
                        easeFactor = 2.5f,
                        nextReviewAt = 0L
                    )
                )
            }
        }
    }

    @Test
    fun quizScreen_gameplayRendersCorrectly() {
        composeTestRule.setContent {
            QuizScreen(onNavigateBack = {})
        }

        // Aguarda os 1500ms do Loading (simulação de delay) e verifica renderização inicial do Gameplay
        // NOTA: Para testes robustos de delay seria necessário manipular o TestDispatcher do CoroutineContext,
        // mas o composeTestRule aguarda a UI ficar "Idle" se configurado corretamente.

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodes(androidx.compose.ui.test.hasText(composeTestRule.activity.getString(R.string.score_label))).fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.score_label)).assertExists()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.level_label)).assertExists()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.question_title)).assertExists()
        composeTestRule.onNodeWithText("好").assertExists()
    }

    @Test
    fun quizScreen_submitCorrectAnswerShowsFeedback() {
        composeTestRule.setContent {
            QuizScreen(onNavigateBack = {})
        }

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodes(androidx.compose.ui.test.hasText(composeTestRule.activity.getString(R.string.score_label))).fetchSemanticsNodes().isNotEmpty()
        }

        // Input
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.pinyin_placeholder)).performTextInput("hao")
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.submit_button)).performClick()

        // Após Enviar, o card de feedback deve aparecer
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.correct_feedback)).assertExists()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.points_earned, 20)).assertExists()
    }
}
