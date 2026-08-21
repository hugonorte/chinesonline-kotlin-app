package com.example.chinesonline.data.network

import retrofit2.http.POST
import retrofit2.http.Header
import retrofit2.http.Body

import com.google.gson.annotations.SerializedName

data class SyncRequest(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("country") val country: Int,
    @SerializedName("account_type") val accountType: Int,
    @SerializedName("birth_date") val birthDate: String
)

data class LoginResponse(
    @SerializedName("name") val name: String,
    @SerializedName("level") val level: Int?,
    @SerializedName("total_score") val totalScore: Int?
)

interface ChinesOnlineApi {
    @POST("users/sync")
    suspend fun syncUser(
        @Body request: SyncRequest
    )

    @POST("auth/login")
    suspend fun login(): retrofit2.Response<LoginResponse>

    @retrofit2.http.GET("sessions/new")
    suspend fun getNewSession(
        @retrofit2.http.Query("level") level: Int,
        @retrofit2.http.Query("game_type") gameType: String?
    ): com.example.chinesonline.feature_quiz.data.SessionResponse

    @POST("sessions/{id}/submit")
    suspend fun submitSession(
        @retrofit2.http.Path("id") sessionId: String,
        @Body request: com.example.chinesonline.feature_quiz.data.SubmitSessionRequest
    ): com.example.chinesonline.feature_quiz.data.SubmitSessionResponse
}
