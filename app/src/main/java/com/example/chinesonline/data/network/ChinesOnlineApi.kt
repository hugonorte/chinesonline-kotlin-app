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
}
