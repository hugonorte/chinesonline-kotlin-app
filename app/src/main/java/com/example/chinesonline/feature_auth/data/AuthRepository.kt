package com.example.chinesonline.feature_auth.data

import com.example.chinesonline.data.network.ApiClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val api = ApiClient.api

    suspend fun login(email: String, pass: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val result = auth.signInWithEmailAndPassword(email, pass).await()
            val token = result.user?.getIdToken(true)?.await()?.token
            if (token != null) {
                // Notificando backend via Go API
                api.login("Bearer $token")
                Result.success("Login Realizado com Sucesso")
            } else {
                Result.failure(Exception("Falha ao obter token JWT"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(
        name: String,
        email: String,
        pass: String,
        country: Int,
        birthDate: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            val token = result.user?.getIdToken(true)?.await()?.token
            if (token != null) {
                // Sincronizando com Go Backend
                val request = com.example.chinesonline.data.network.SyncRequest(
                    name = name,
                    email = email,
                    country = country,
                    accountType = 0, // 0 = Padrão (Free)
                    birthDate = birthDate
                )
                api.syncUser("Bearer $token", request)
                Result.success("SUCESSO")
            } else {
                Result.failure(Exception("Falha ao obter token JWT"))
            }
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string() ?: e.message()
            Result.failure(Exception("Erro 400 Backend: $errorBody"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
