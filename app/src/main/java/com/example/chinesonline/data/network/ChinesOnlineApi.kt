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

interface ChinesOnlineApi {
    @POST("users/sync")
    suspend fun syncUser(
        @Header("Authorization") token: String,
        @Body request: SyncRequest
    )

    @POST("auth/login")
    suspend fun login(
        @Header("Authorization") token: String
    )

    @retrofit2.http.GET("sessions/new")
    suspend fun getNewSession(
        @Header("Authorization") token: String,
        @retrofit2.http.Query("level") level: Int,
        @retrofit2.http.Query("game_type") gameType: String?
    ): com.example.chinesonline.feature_quiz.data.SessionResponse

    @POST("sessions/{id}/submit")
    suspend fun submitSession(
        @Header("Authorization") token: String,
        @retrofit2.http.Path("id") sessionId: String,
        @Body request: com.example.chinesonline.feature_quiz.data.SubmitSessionRequest
    ): com.example.chinesonline.feature_quiz.data.SubmitSessionResponse
}
