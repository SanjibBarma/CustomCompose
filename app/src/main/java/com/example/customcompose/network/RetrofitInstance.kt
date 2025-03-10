package com.example.customcompose.network

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import okhttp3.*
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    private const val BASE_URL = "https://ecrm-prod3.v2.ltd"
    private const val LOG_TAG = "API_LOG"

    private val gson = GsonBuilder().setPrettyPrinting().create()

    private val client = OkHttpClient.Builder()
        .addInterceptor(createLoggingInterceptor())
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private fun createLoggingInterceptor(): Interceptor = Interceptor { chain ->
        val request = chain.request()
        val requestBody = request.body

        var formattedRequestBody = "N/A"
        requestBody?.let {
            val buffer = Buffer()
            it.writeTo(buffer)
            val rawJson = buffer.readUtf8()
            formattedRequestBody = try {
                gson.toJson(JsonParser.parseString(rawJson))
            } catch (e: Exception) {
                rawJson
            }
        }

        val token = request.header("Authorization") ?: "N/A"
        val wrappedToken = token.chunked(100).joinToString("\n│ ")

        val requestLog = buildString {
            append("\n\n┌────── Request ────────────────────────────────────────────────────────\n")
            append("│ URL: ${request.url}\n")
            append("│ Method: @${request.method}\n")
            append("│ Token:\n│ $wrappedToken\n") // 🔥 Soft-wrapped token
            append("│ Body:\n")
            formattedRequestBody.split("\n").forEach { line ->
                append("│ $line\n")
            }
            append("└─────────────────────────────────────────────────────────────────────\n")
        }
        Log.i(LOG_TAG, requestLog)

        val response = chain.proceed(request)

        val responseBody = response.peekBody(2048).string()
        val formattedResponseBody = try {
            gson.toJson(JsonParser.parseString(responseBody))
        } catch (e: Exception) {
            responseBody
        }

        val responseLog = buildString {
            append("\n\n┌────── Response ────────────────────────────────────────────────────────\n")
            append("│ URL: ${response.request.url}\n")
            append("│ Status Code: ${response.code} / ${response.message}\n")
            append("│ Body:\n")
            formattedResponseBody.split("\n").forEach { line ->
                append("│ $line\n")
            }
            append("└─────────────────────────────────────────────────────────────────────\n")
        }
        Log.i(LOG_TAG, responseLog)

        response
    }

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
