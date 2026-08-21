package com.example.chinesonline.feature_quiz.ui

import com.example.chinesonline.core.data.local.UserPreferencesRepository
import com.example.chinesonline.feature_quiz.data.QuestionResponse
import com.example.chinesonline.feature_quiz.data.QuizRepository
import com.example.chinesonline.feature_quiz.data.SessionResponse
import com.example.chinesonline.feature_quiz.data.SubmitSessionResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FakeUserPreferencesRepository : UserPreferencesRepository {
    private val _userLevel = MutableStateFlow(1)
    override val userLevel: Flow<Int> = _userLevel.asStateFlow()

    private val _userXp = MutableStateFlow(0)
    override val userXp: Flow<Int> = _userXp.asStateFlow()

    private val _userName = MutableStateFlow<String?>(null)
    override val userName: Flow<String?> = _userName.asStateFlow()

    var savedLevel: Int? = null
    var savedXp: Int? = null

    override suspend fun saveProgress(level: Int, xp: Int) {
        savedLevel = level
        savedXp = xp
        _userLevel.value = level
        _userXp.value = xp
    }

    override suspend fun saveUserName(name: String) {
        _userName.value = name
    }

    override suspend fun clear() {
        _userLevel.value = 1
        _userXp.value = 0
        _userName.value = null
        savedLevel = null
        savedXp = null
    }
}

class FakeQuizRepository : QuizRepository {
    var submitResponseToReturn: SubmitSessionResponse? = null
    var totalScoreToReturn: Int = 0

    override suspend fun getNewSession(level: Int, gameType: String): SessionResponse {
        return SessionResponse("mock_session", totalScoreToReturn, level, listOf(
            QuestionResponse(1, "mock", "mock", "mock", "mock", "mock")
        ))
    }

    override suspend fun submitSession(sessionId: String, answers: Map<String, String>): SubmitSessionResponse {
        return submitResponseToReturn ?: SubmitSessionResponse(10, 10, true, false, 1)
    }

    override suspend fun updateLocalStat(question: QuestionResponse, gameType: String, isCorrect: Boolean) {
        // Mock
    }

    override suspend fun clearLocalData() {
        // Mock
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class QuizViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when submit session returns leveledUp, repository should save new level and xp`() = runTest {
        val prefs = FakeUserPreferencesRepository()
        val repo = FakeQuizRepository()
        val viewModel = QuizViewModel(repo, prefs)

        // Simula que a resposta do backend foi: level 2, totalScore 150, leveledUp = true
        repo.submitResponseToReturn = SubmitSessionResponse(
            score = 10,
            totalScore = 150,
            isValid = true,
            leveledUp = true,
            currentLevel = 2
        )

        viewModel.startGame()
        advanceUntilIdle() // Espera o startGame carregar a sessão

        // Força a submissão de uma resposta que encerrará o quiz de 1 pergunta
        viewModel.submitAnswer("mock_answer")
        advanceUntilIdle() // Espera o delay e a chamada de rede do submitSession

        // Assert
        assertEquals(2, prefs.savedLevel)
        assertEquals(150, prefs.savedXp)
    }

    @Test
    fun `viewModel should initialize with values from preferences`() = runTest {
        val prefs = FakeUserPreferencesRepository()
        prefs.saveProgress(3, 300) // Salva estado histórico

        val repo = FakeQuizRepository()
        repo.totalScoreToReturn = 300 // Simula o backend retornando o valor correto
        val viewModel = QuizViewModel(repo, prefs)
        
        viewModel.startGame() // Agora sim a inicialização pega as preferências
        advanceUntilIdle() 

        assertEquals(3, viewModel.currentLevel.value)
        assertEquals(300, viewModel.currentXp.value)
    }

    @Test
    fun `viewModel should expose correct points per answer`() {
        val prefs = FakeUserPreferencesRepository()
        val repo = FakeQuizRepository()
        val viewModel = QuizViewModel(repo, prefs)
        
        assertEquals(10, viewModel.pointsPerCorrectAnswer)
    }
}
