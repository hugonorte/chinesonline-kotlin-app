package com.example.chinesonline.feature_auth

import com.example.chinesonline.feature_auth.data.AuthRepository
import com.example.chinesonline.feature_auth.ui.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    
    // Fake Repository for testing
    class FakeAuthRepository : AuthRepository() {
        var shouldFail = false
        var shouldFailLogin = false
        var isEmailVerified = true
        var shouldFailRegister = false

        override suspend fun login(email: String, pass: String): Result<String> {
            return if (shouldFailLogin) {
                Result.failure(Exception("Login failed"))
            } else if (!isEmailVerified) {
                Result.failure(Exception("email_not_verified"))
            } else {
                Result.success("Login Realizado com Sucesso")
            }
        }

        override suspend fun register(
            name: String, email: String, pass: String, country: Int, birthDate: String
        ): Result<String> {
            return if (shouldFailRegister) {
                Result.failure(Exception("Register failed"))
            } else {
                Result.success("SUCESSO")
            }
        }

        override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
            return if (shouldFail) {
                Result.failure(Exception("Email not found"))
            } else {
                Result.success(Unit)
            }
        }

        override suspend fun confirmPasswordReset(code: String, newPassword: String): Result<Unit> {
            return if (shouldFail) {
                Result.failure(Exception("Invalid code"))
            } else {
                Result.success(Unit)
            }
        }
    }

    private lateinit var fakeRepo: FakeAuthRepository
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        if (com.google.firebase.FirebaseApp.getApps(androidx.test.core.app.ApplicationProvider.getApplicationContext()).isEmpty()) {
            com.google.firebase.FirebaseApp.initializeApp(androidx.test.core.app.ApplicationProvider.getApplicationContext())
        }
        Dispatchers.setMain(testDispatcher)
        fakeRepo = FakeAuthRepository()
        viewModel = AuthViewModel(repo = fakeRepo)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `doSendPasswordResetEmail success updates state correctly`() = runTest {
        // Arrange
        fakeRepo.shouldFail = false
        
        // Act
        viewModel.doSendPasswordResetEmail("test@example.com")
        testScheduler.advanceUntilIdle() // Wait for coroutines to finish

        // Assert
        assertEquals(true, viewModel.forgotPasswordSuccess.value)
        assertEquals(false, viewModel.isLoading.value)
        assertEquals(null, viewModel.loginState.value)
    }

    @Test
    fun `doSendPasswordResetEmail failure updates error state`() = runTest {
        // Arrange
        fakeRepo.shouldFail = true
        
        // Act
        viewModel.doSendPasswordResetEmail("test@example.com")
        testScheduler.advanceUntilIdle()

        // Assert
        assertEquals(false, viewModel.forgotPasswordSuccess.value)
        assertEquals(false, viewModel.isLoading.value)
        assertEquals("Email not found", viewModel.loginState.value)
    }

    @Test
    fun `doConfirmPasswordReset success updates state correctly`() = runTest {
        // Arrange
        fakeRepo.shouldFail = false
        
        // Act
        viewModel.doConfirmPasswordReset("valid_code", "new_password")
        testScheduler.advanceUntilIdle()

        // Assert
        assertEquals(true, viewModel.resetPasswordSuccess.value)
        assertEquals(false, viewModel.isLoading.value)
        assertEquals(null, viewModel.loginState.value)
    }

    @Test
    fun `doConfirmPasswordReset failure updates error state`() = runTest {
        // Arrange
        fakeRepo.shouldFail = true
        
        // Act
        viewModel.doConfirmPasswordReset("invalid_code", "new_password")
        testScheduler.advanceUntilIdle()

        // Assert
        assertEquals(false, viewModel.resetPasswordSuccess.value)
        assertEquals(false, viewModel.isLoading.value)
        assertEquals("Invalid code", viewModel.loginState.value)
    }
    @Test
    fun `doLogin with unverified email sets loginState to email_not_verified`() = runTest {
        // Arrange
        fakeRepo.isEmailVerified = false
        
        // Act
        viewModel.doLogin("test@example.com", "pass")
        testScheduler.advanceUntilIdle()

        // Assert
        assertEquals("email_not_verified", viewModel.loginState.value)
        assertEquals(false, viewModel.loginSuccess.value)
        assertEquals(false, viewModel.isLoading.value)
    }

    @Test
    fun `doLogin with verified email succeeds`() = runTest {
        // Arrange
        fakeRepo.isEmailVerified = true
        
        // Act
        viewModel.doLogin("test@example.com", "pass")
        testScheduler.advanceUntilIdle()

        // Assert
        assertEquals(null, viewModel.loginState.value)
        assertEquals(true, viewModel.loginSuccess.value)
        assertEquals(false, viewModel.isLoading.value)
    }

    @Test
    fun `doRegister success updates state correctly`() = runTest {
        // Arrange
        fakeRepo.shouldFailRegister = false
        
        // Act
        viewModel.doRegister("Name", "test@example.com", "pass", 1, "2000-01-01")
        testScheduler.advanceUntilIdle()

        // Assert
        assertEquals(true, viewModel.registerSuccess.value)
        assertEquals(false, viewModel.isLoading.value)
        assertEquals(null, viewModel.loginState.value)
    }
}
