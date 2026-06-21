package com.example.chinesonline.feature_auth

import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthRepositoryTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var authApi: com.example.chinesonline.feature_auth.data.AuthApi

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        authApi = retrofit.create(com.example.chinesonline.feature_auth.data.AuthApi::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `login with valid credentials returns token`() = runBlocking {
        // Arrange
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "token": "fake_jwt_token",
                    "user": {
                        "id": 1,
                        "name": "Test User",
                        "email": "test@example.com"
                    }
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        // Act
        val response = authApi.login(com.example.chinesonline.feature_auth.data.LoginRequest("test@example.com", "123456"))

        // Assert
        assertEquals(200, response.code())
        assertEquals("fake_jwt_token", response.body()?.token)
    }

    @Test
    fun `login with invalid credentials returns 401`() = runBlocking {
        // Arrange
        val mockResponse = MockResponse()
            .setResponseCode(401)
            .setBody("""{"error": "Unauthorized"}""")
        mockWebServer.enqueue(mockResponse)

        // Act
        val response = authApi.login(com.example.chinesonline.feature_auth.data.LoginRequest("wrong@email.com", "wrong"))

        // Assert
        assertEquals(401, response.code())
    }
}
