package com.example.chinesonline.data.network

import com.example.chinesonline.BuildConfig
import com.google.android.gms.tasks.Tasks
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.auth.FirebaseAuth
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private val certificatePinner = CertificatePinner.Builder()
        .add(
            "chinesonline-go-api-80060965106.us-east1.run.app",
            "sha256/brPjwNAroFhk7eCa+s2c7KMcKLDauXj3yvaVsjxewKY=", // Leaf atual
            "sha256/hxqRlPTu1bMS/0DITB1SSu0vd4u/8l8TjPgfaAp63Gc=", // GTS Root R1
            "sha256/Vfd95BwDeSQo+NUZX1EQsrBdZZVBrvsHclznY9vM14E=", // GTS Root R2
            "sha256/QX71XzM1vA94sP7ZfVb8p50h8V8Uj8v5q/V1Yg8aVcw=", // GTS Root R3
            "sha256/5k0FvF5z4x8o+6J8Uf9J7E2X6r5h1ZkOQJj8W5VwX0s="  // GTS Root R4
        )
        .build()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    }

    private val okHttpClient = OkHttpClient.Builder()
        .certificatePinner(certificatePinner)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
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
