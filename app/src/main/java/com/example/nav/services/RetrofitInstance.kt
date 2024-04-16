package com.example.nav.services

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://192.168.1.5/api/"

    // Create an OkHttpClient with interceptors
    private val okHttpClient = OkHttpClient.Builder()
        // Add a logging interceptor for logging requests and responses
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Set logging level to include request and response bodies
        })
        // Add other interceptors if needed
        .build()

    val instance: APIService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Set the custom OkHttpClient with interceptors
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(APIService::class.java)
    }
}