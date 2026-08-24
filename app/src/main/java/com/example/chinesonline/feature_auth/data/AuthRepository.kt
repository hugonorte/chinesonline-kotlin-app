package com.example.chinesonline.feature_auth.data

import com.example.chinesonline.data.network.ApiClient
import com.example.chinesonline.data.network.ChinesOnlineApi
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

data class AuthResult(val name: String, val level: Int, val xp: Int)

open class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val api: ChinesOnlineApi = com.example.chinesonline.data.network.ApiClient.api
) {
    open suspend fun login(email: String, pass: String): Result<AuthResult> = withContext(Dispatchers.IO) {
        try {
            val result = auth.signInWithEmailAndPassword(email, pass).await()
            val user = result.user
            
            if (user != null && !user.isEmailVerified) {
                auth.signOut()
                return@withContext Result.failure(Exception("email_not_verified"))
            }

            val token = user?.getIdToken(true)?.await()?.token
            if (token != null) {
                // Notificando backend via Go API e extraindo o nome da resposta
                val response = api.login()
                val authResult = if (response.isSuccessful) {
                    val body = response.body()
                    AuthResult(
                        name = body?.name ?: "Player",
                        level = body?.level ?: 1,
                        xp = body?.totalScore ?: 0
                    )
                } else {
                    AuthResult("Player", 1, 0)
                }
                Result.success(authResult)
            } else {
                Result.failure(Exception("Falha ao obter token JWT"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    open suspend fun register(
        name: String,
        email: String,
        pass: String,
        country: Int,
        birthDate: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            val user = result.user
            
            // Send verification email
            user?.sendEmailVerification()?.await()

            val token = user?.getIdToken(true)?.await()?.token
            if (token != null) {
                // Sincronizando com Go Backend
                val request = com.example.chinesonline.data.network.SyncRequest(
                    name = name,
                    email = email,
                    country = country,
                    accountType = 0, // 0 = Padrão (Free)
                    birthDate = birthDate
                )
                api.syncUser(request)
                Result.success(name)
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

    open suspend fun sendPasswordResetEmail(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val actionCodeSettings = com.google.firebase.auth.ActionCodeSettings.newBuilder()
                .setUrl("https://chinesonline-prod.firebaseapp.com/__/auth/action?mode=resetPassword")
                .setHandleCodeInApp(true)
                .setAndroidPackageName(
                    "com.example.chinesonline",
                    true,
                    null
                )
                .build()

            auth.sendPasswordResetEmail(email, actionCodeSettings).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    open suspend fun confirmPasswordReset(code: String, newPassword: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            auth.confirmPasswordReset(code, newPassword).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    open suspend fun deleteAccount(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.deleteAccount()
            if (response.isSuccessful) {
                // Ao excluir a conta, o backend também deverá excluir no Firebase.
                // Mas, como precaução de estado local, fazemos o signout
                auth.signOut()
                Result.success(Unit)
            } else {
                Result.failure(Exception("Erro ao excluir conta no backend: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
