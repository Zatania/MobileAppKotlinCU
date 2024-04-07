package com.example.nav.services

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface APIService {
    @Headers("Content-Type: application/json")
    @POST("login")
    suspend fun login(@Body requestBody: JsonObject): Response<ApiResponse>

    @Headers("Content-Type: application/json")
    @POST("register")
    suspend fun register(@Body requestBody: JsonObject): Response<RegisterResponse>

    @Headers("Content-Type: application/json")
    @POST("send-otp")
    suspend fun sendOTP(@Body requestBody: JsonObject): Response<ApiResponse>

    @Headers("Content-Type: application/json")
    @POST("verify-email")
    suspend fun verifyEmail(@Body requestBody: JsonObject): Response<VerifyResponse>
}