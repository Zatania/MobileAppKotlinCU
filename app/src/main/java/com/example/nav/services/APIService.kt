package com.example.nav.services

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part
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
    @GET("chapter_assessment")
    suspend fun getChapterAssessment(
        @Header("Authorization") token: String
    ) : Response<ChapterAssessmentResponseMain>

    @Headers("Content-Type: application/json")
    @Multipart
    @POST("user/upload/{username}")
    suspend fun uploadImage(
        @Header("Authorization") token: String,
        @Path("username") username: String,
        @Part image: MultipartBody.Part
    ) : Response<ApiResponse>

    @Headers("Content-Type: application/json")
    @GET("user/profile/{username}")
    suspend fun getProfile(
        @Header("Authorization") token: String,
        @Path("username") username: String,
    ) : Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @GET("user/fetch/inprogress")
    suspend fun getInProgress(
        @Header("Authorization") token: String,
    ) : Response<List<Progress>>


    @Headers("Content-Type: application/json")
    @GET("programming-language/fetch")
    suspend fun fetchData(
        @Header("Authorization") token: String,
    ) : Response<ChaptersData>

    @Headers("Content-Type: application/json")
    @POST("user/progress/create")
    suspend fun createProgress(
        @Header("Authorization") token: String,
        @Body requestBody: JsonObject
    ) : Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("user/progress/getLessonID")
    suspend fun getNextLessonId(
        @Header("Authorization") token: String,
        @Body requestBody: JsonObject
    ) : Response<LessonID>

    @Headers("Content-Type: application/json")
    @POST("user/progress/getChapterID")
    suspend fun getNextChapterId(
        @Header("Authorization") token: String,
        @Body requestBody: JsonObject
    ) : Response<ChapterID>

    @Headers("Content-Type: application/json")
    @POST("user/progress/getLastLessonID")
    suspend fun getLastLessonId(
        @Header("Authorization") token: String,
        @Body requestBody: JsonObject
    ) : Response<LastLessonID>

    @Headers("Content-Type: application/json")
    @POST("user/progress/getFirstLessonID")
    suspend fun getFirstLessonID(
        @Header("Authorization") token: String,
        @Body requestBody: JsonObject
    ) : Response<FirstLessonID>
}