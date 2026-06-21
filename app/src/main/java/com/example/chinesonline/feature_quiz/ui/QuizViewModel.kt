package com.example.chinesonline.feature_quiz.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class QuizState {
    LOADING, GAMEPLAY, END_GAME
}

enum class FeedbackState {
    NONE, CORRECT, INCORRECT
}

class QuizViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(QuizState.LOADING)
    val uiState: StateFlow<QuizState> = _uiState.asStateFlow()

    private val _feedbackState = MutableStateFlow(FeedbackState.NONE)
    val feedbackState: StateFlow<FeedbackState> = _feedbackState.asStateFlow()

    private val _currentXp = MutableStateFlow(120)
    val currentXp: StateFlow<Int> = _currentXp.asStateFlow()

    private val _currentScore = MutableStateFlow(0)
    val currentScore: StateFlow<Int> = _currentScore.asStateFlow()

    private val _currentLevel = MutableStateFlow(2)
    val currentLevel: StateFlow<Int> = _currentLevel.asStateFlow()

    private val _roundCount = MutableStateFlow(1)
    val roundCount: StateFlow<Int> = _roundCount.asStateFlow()

    private val _levelUp = MutableStateFlow(false)
    val levelUp: StateFlow<Boolean> = _levelUp.asStateFlow()

    init {
        _uiState.value = QuizState.GAMEPLAY
    }

    fun startGame() {
        viewModelScope.launch {
            _uiState.value = QuizState.LOADING
            _feedbackState.value = FeedbackState.NONE
            _roundCount.value = 1
            _currentScore.value = 0
            _levelUp.value = false
            _uiState.value = QuizState.GAMEPLAY
        }
    }

    fun submitAnswer(answer: String) {
        if (_feedbackState.value != FeedbackState.NONE) return

        viewModelScope.launch {
            // Mock: Se resposta for "hao", é correto. Senão, incorreto.
            val isCorrect = answer.trim().lowercase() == "hao"
            
            if (isCorrect) {
                _feedbackState.value = FeedbackState.CORRECT
                _currentXp.value += 20
                _currentScore.value += 100
            } else {
                _feedbackState.value = FeedbackState.INCORRECT
            }

            delay(2500) // Mostra o feedback por 2.5s

            if (_roundCount.value >= 3) {
                // Fim de jogo
                if (_currentScore.value >= 200) {
                    _levelUp.value = true
                    _currentLevel.value += 1
                }
                _uiState.value = QuizState.END_GAME
            } else {
                // Próxima rodada
                _roundCount.value += 1
                _feedbackState.value = FeedbackState.NONE
            }
        }
    }
}
