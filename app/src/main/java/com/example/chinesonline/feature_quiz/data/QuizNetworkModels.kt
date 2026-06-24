package com.example.chinesonline.feature_quiz.data

import com.google.gson.annotations.SerializedName

data class QuestionResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("character") val character: String,
    @SerializedName("salt") val salt: String,
    @SerializedName("hash") val hash: String,
    @SerializedName("pinyin") val pinyin: String,
    @SerializedName("translation") val translation: String
)

data class SessionResponse(
    @SerializedName("session_id") val sessionId: Int,
    @SerializedName("total_score") val totalScore: Int,
    @SerializedName("level") val level: Int,
    @SerializedName("questions") val questions: List<QuestionResponse>
)

data class SubmitSessionRequest(
    @SerializedName("answers") val answers: Map<String, String>
)

data class SubmitSessionResponse(
    @SerializedName("score") val score: Int,
    @SerializedName("total_score") val totalScore: Int,
    @SerializedName("is_valid") val isValid: Boolean,
    @SerializedName("leveled_up") val leveledUp: Boolean,
    @SerializedName("current_level") val currentLevel: Int
)
