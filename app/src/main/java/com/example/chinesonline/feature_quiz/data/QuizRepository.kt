package com.example.chinesonline.feature_quiz.data

import com.example.chinesonline.core.data.local.IdeogramStatDao
import com.example.chinesonline.core.data.local.LocalIdeogramStat
import com.example.chinesonline.data.network.ChinesOnlineApi

class QuizRepository(
    private val api: ChinesOnlineApi,
    private val dao: IdeogramStatDao
) {
    suspend fun getNewSession(level: Int, gameType: String): SessionResponse {
        val dueIdeograms = dao.getDueIdeograms(gameType, System.currentTimeMillis(), 10)
        
        val localQuestions = dueIdeograms.map {
            QuestionResponse(
                id = it.ideogramId,
                character = it.character,
                pinyin = it.pinyin,
                translation = it.translation,
                salt = it.salt,
                hash = it.hash
            )
        }

        if (localQuestions.size >= 10) {
            return SessionResponse(
                sessionId = "local_${System.currentTimeMillis()}",
                totalScore = 0,
                level = level,
                questions = localQuestions
            )
        }

        val sessionFromApi = api.getNewSession(level, gameType)
        
        val localIds = localQuestions.map { it.id }.toSet()
        val newQuestions = sessionFromApi.questions.filterNot { localIds.contains(it.id) }
        
        val hybridQuestions = (localQuestions + newQuestions).take(10)
        
        return SessionResponse(
            sessionId = sessionFromApi.sessionId,
            totalScore = sessionFromApi.totalScore,
            level = sessionFromApi.level,
            questions = hybridQuestions
        )
    }

    suspend fun submitSession(sessionId: String, answers: Map<String, String>): SubmitSessionResponse {
        if (sessionId.startsWith("local_")) {
            return SubmitSessionResponse(
                score = answers.size * 10,
                totalScore = 0,
                isValid = true,
                leveledUp = false,
                currentLevel = 0
            )
        }
        return api.submitSession(sessionId, SubmitSessionRequest(answers))
    }

    suspend fun updateLocalStat(question: QuestionResponse, gameType: String, isCorrect: Boolean) {
        val id = "${question.id}_${gameType}"
        val stat = dao.getStatById(id) ?: LocalIdeogramStat(
            id = id,
            ideogramId = question.id,
            gameType = gameType,
            character = question.character,
            pinyin = question.pinyin,
            translation = question.translation,
            salt = question.salt,
            hash = question.hash,
            correctAttempts = 0,
            wrongAttempts = 0,
            lastReviewed = 0L,
            interval = 0,
            easeFactor = 2.5f,
            nextReviewAt = 0L
        )

        val quality = if (isCorrect) 5 else 0
        val srsResult = com.example.chinesonline.feature_quiz.domain.SrsAlgorithm.calculateNextReview(quality, stat.interval, stat.easeFactor)

        if (isCorrect) {
            stat.correctAttempts++
        } else {
            stat.wrongAttempts++
        }

        stat.lastReviewed = System.currentTimeMillis()
        stat.interval = srsResult.interval
        stat.easeFactor = srsResult.easeFactor
        stat.nextReviewAt = srsResult.nextReviewAt

        dao.upsertStat(stat)
    }
}
