package com.example.chinesonline.feature_auth.data

data class LoginRequest(
    val email: String,
    val pass: String
)

data class LoginResponse(
    val token: String,
    val user: UserDto
)

data class UserDto(
    val id: Int,
    val name: String,
    val email: String
)
