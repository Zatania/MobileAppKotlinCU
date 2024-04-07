package com.example.nav.services

data class Results(
    val email: String,
    val token: String,
    val username: String
)

data class RegisterResults(
    val email: String,
    val username: String,
    val password: String,
    val password_confirmation: String
)