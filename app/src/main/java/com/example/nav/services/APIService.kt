package com.example.nav.services

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

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

    @Headers("Content-Type: application/json")
    @GET("user/badge/{username}")
    suspend fun getUserBadges(
        @Header("Authorization") token: String,
        @Path("username") username: String
    ): Response<List<BadgesResponse>>

    @Headers("Content-Type: application/json")
    @POST("user/add-badge")
    suspend fun addBadge(
        @Header("Authorization") token: String,
        @Body requestBody: JsonObject
    )

    @Headers("Content-Type: application/json")
    @GET("programming-language")
    suspend fun getProgrammingLanguages(
        @Header("Authorization") token: String
    ): Response<List<ProgrammingLanguage>>

    @Headers("Content-Type: application/json")
    @POST("user/progress-chapter-assessment/create")
    suspend fun createProgressChapterAssessment(
        @Header("Authorization") token: String,
        @Body requestBody: JsonObject
    )

    @Headers("Content-Type: application/json")
    @POST("user/progress-exam/create")
    suspend fun createProgressExam(
        @Header("Authorization") token: String,
        @Body requestBody: JsonObject
    )

    @Headers("Content-Type: application/json")
    @GET("chapter_assessment")
    suspend fun getChapterAssessment(
        @Header("Authorization") token: String
    ) : Response<ChapterAssessmentResponseMain>


}