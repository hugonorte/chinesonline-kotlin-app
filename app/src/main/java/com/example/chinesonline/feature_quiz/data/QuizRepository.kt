package com.example.chinesonline.feature_quiz.data

import com.example.chinesonline.core.data.local.IdeogramStatDao
import com.example.chinesonline.core.data.local.LocalIdeogramStat
import com.example.chinesonline.data.network.ChinesOnlineApi

class QuizRepository(
    private val api: ChinesOnlineApi,
    private val dao: IdeogramStatDao
) {
    suspend fun getNewSession(level: Int, gameType: String): SessionResponse {
        val session = api.getNewSession(level, gameType)
        return session
    }

    suspend fun submitSession(sessionId: String, answers: Map<String, String>): SubmitSessionResponse {
        return api.submitSession(sessionId, SubmitSessionRequest(answers))
    }

    suspend fun updateLocalStat(ideogramId: Int, gameType: String, isCorrect: Boolean) {
        val id = "${ideogramId}_${gameType}"
        val stat = dao.getStatById(id) ?: LocalIdeogramStat(
            id = id,
            ideogramId = ideogramId,
            gameType = gameType,
            correctAttempts = 0,
            wrongAttempts = 0,
            lastReviewed = System.currentTimeMillis()
        )
        if (isCorrect) {
            stat.correctAttempts++
        } else {
            stat.wrongAttempts++
        }
        stat.lastReviewed = System.currentTimeMillis()
        dao.upsertStat(stat)
    }
}
