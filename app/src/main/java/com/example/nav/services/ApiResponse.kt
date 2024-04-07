package com.example.nav.services

data class ApiResponse(
    val message: String,
    val results: Results,
    val code: Int
)

data class RegisterResponse(
    val message: String,
    val code: Int
)

data class VerifyResponse(
    val message: String,
    val code: Int
)