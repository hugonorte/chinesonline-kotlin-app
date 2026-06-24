package com.example.chinesonline.data.network

import com.example.chinesonline.BuildConfig
import com.google.android.gms.tasks.Tasks
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.auth.FirebaseAuth
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            var requestBuilder = chain.request().newBuilder()
            
            try {
                val appCheckTokenResult = Tasks.await(FirebaseAppCheck.getInstance().getAppCheckToken(false))
                val appCheckToken = appCheckTokenResult.token
                if (appCheckToken.isNotEmpty()) {
                    requestBuilder = requestBuilder.header("X-Firebase-AppCheck", appCheckToken)
                }
            } catch (e: Exception) {
                // Ignore
            }

            try {
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null) {
                    val authResult = Tasks.await(currentUser.getIdToken(false))
                    val authToken = authResult.token
                    if (!authToken.isNullOrEmpty()) {
                        requestBuilder = requestBuilder.header("Authorization", "Bearer $authToken")
                    }
                }
            } catch (e: Exception) {
                // Ignore
            }

            chain.proceed(requestBuilder.build())
        }
        .build()

    val api: ChinesOnlineApi by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ChinesOnlineApi::class.java)
    }
}
