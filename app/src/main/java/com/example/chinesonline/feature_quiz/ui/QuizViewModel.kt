package com.example.chinesonline.feature_quiz.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.chinesonline.core.utils.HashUtils
import com.example.chinesonline.feature_quiz.data.QuestionResponse
import com.example.chinesonline.feature_quiz.data.QuizRepository
import com.example.chinesonline.feature_quiz.data.SessionResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.first

enum class QuizState {
    LOADING, GAMEPLAY, END_GAME, ERROR
}

enum class FeedbackState {
    NONE, CORRECT, INCORRECT
}

class QuizViewModel(
    private val repository: QuizRepository,
    private val userPreferences: com.example.chinesonline.core.data.local.UserPreferencesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(QuizState.LOADING)
    val uiState: StateFlow<QuizState> = _uiState.asStateFlow()

    private val _feedbackState = MutableStateFlow(FeedbackState.NONE)
    val feedbackState: StateFlow<FeedbackState> = _feedbackState.asStateFlow()

    private val _currentXp = MutableStateFlow(0)
    val currentXp: StateFlow<Int> = _currentXp.asStateFlow()

    private val _currentScore = MutableStateFlow(0)
    val currentScore: StateFlow<Int> = _currentScore.asStateFlow()

    private val _currentLevel = MutableStateFlow(1)
    val currentLevel: StateFlow<Int> = _currentLevel.asStateFlow()

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName.asStateFlow()

    private val _roundCount = MutableStateFlow(1)
    val roundCount: StateFlow<Int> = _roundCount.asStateFlow()

    private val _levelUp = MutableStateFlow(false)
    val levelUp: StateFlow<Boolean> = _levelUp.asStateFlow()
    
    private var currentSession: SessionResponse? = null
    private var currentQuestions: List<QuestionResponse> = emptyList()
    private val userAnswers = mutableMapOf<String, String>()
    
    private val _currentQuestion = MutableStateFlow<QuestionResponse?>(null)
    val currentQuestion: StateFlow<QuestionResponse?> = _currentQuestion.asStateFlow()
    
    val pointsPerCorrectAnswer = 10

    fun startGame(gameType: String = "pinyin_without_tone") {
        viewModelScope.launch {
            _uiState.value = QuizState.LOADING
            _feedbackState.value = FeedbackState.NONE
            _roundCount.value = 1
            _levelUp.value = false
            userAnswers.clear()
            
            try {
                viewModelScope.launch {
                    userPreferences.userName.collect { name ->
                        _userName.value = name
                    }
                }
                val persistedLevel = userPreferences.userLevel.first()
                val persistedXp = userPreferences.userXp.first()
                _currentLevel.value = persistedLevel
                _currentXp.value = persistedXp

                val session = repository.getNewSession(persistedLevel, gameType)
                currentSession = session
                currentQuestions = session.questions
                _currentScore.value = 0
                _currentXp.value = session.totalScore // O backend pode mandar o atualizado
                _currentLevel.value = session.level
                
                // Sincroniza o que veio do backend para o local
                userPreferences.saveProgress(session.level, session.totalScore)
                
                if (currentQuestions.isNotEmpty()) {
                    _currentQuestion.value = currentQuestions[0]
                    _uiState.value = QuizState.GAMEPLAY
                } else {
                    _uiState.value = QuizState.ERROR
                }
            } catch (e: Exception) {
                android.util.Log.e("QuizViewModel", "Erro ao buscar sessão", e)
                _uiState.value = QuizState.ERROR
            }
        }
    }

    fun submitAnswer(answer: String) {
        if (_feedbackState.value != FeedbackState.NONE) return
        val question = _currentQuestion.value ?: return

        viewModelScope.launch {
            val hashed = HashUtils.sha256(answer.trim() + question.salt)
            val isCorrect = (hashed == question.hash)
            
            userAnswers[question.id.toString()] = answer.trim()

            if (isCorrect) {
                _currentScore.value += pointsPerCorrectAnswer
                _feedbackState.value = FeedbackState.CORRECT
            } else {
                _feedbackState.value = FeedbackState.INCORRECT
            }
            
            val gameType = "pinyin_without_tone"
            repository.updateLocalStat(question, gameType, isCorrect)

            delay(2500)

            if (_roundCount.value >= currentQuestions.size) {
                _uiState.value = QuizState.LOADING
                try {
                    val result = repository.submitSession(currentSession!!.sessionId.toString(), userAnswers)
                    _currentScore.value = result.score
                    _currentXp.value = result.totalScore
                    _currentLevel.value = result.currentLevel
                    _levelUp.value = result.leveledUp
                    
                    // TDD: Persiste a nova pontuação histórica e o nível
                    userPreferences.saveProgress(result.currentLevel, result.totalScore)
                    
                    _uiState.value = QuizState.END_GAME
                } catch (e: Exception) {
                    _uiState.value = QuizState.ERROR
                }
            } else {
                val nextIndex = _roundCount.value
                _currentQuestion.value = currentQuestions[nextIndex]
                _roundCount.value += 1
                _feedbackState.value = FeedbackState.NONE
            }
        }
    }
    
    companion object {
        fun provideFactory(
            repository: QuizRepository,
            userPreferences: com.example.chinesonline.core.data.local.UserPreferencesRepository
        ): ViewModelProvider.Factory = 
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return QuizViewModel(repository, userPreferences) as T
                }
            }
    }
}
