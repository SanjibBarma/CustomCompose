package com.example.customcompose.network

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import okhttp3.*
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    private const val BASE_URL = "https://ecrm-prod3.v2.ltd"
    private const val LOG_TAG = "API_LOG"

    private val gson = GsonBuilder().setPrettyPrinting().create()

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(createLoggingInterceptor())
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
            append("│ Token:\n│ $wrappedToken\n")
            append("│ Body:\n")
            formattedRequestBody.split("\n").forEach { line ->
                append(" $line\n")
            }
            append("└─────────────────────────────────────────────────────────────────────\n")
        }
        Log.i(LOG_TAG, requestLog)

        val response = chain.proceed(request)

        val rawBody = response.body?.string() ?: ""
        val formattedResponseBody = try {
            val jsonElement = JsonParser.parseString(rawBody)
            gson.toJson(jsonElement)
        } catch (e: Exception) {
            rawBody
        }

        val responseLog = buildString {
            append("\n\n┌────── Response ────────────────────────────────────────────────────────\n")
            append("│ URL: ${response.request.url}\n")
            append("│ Status Code: ${response.code} / ${response.message}\n")
            append("│ Body:\n")
            formattedResponseBody.split("\n").forEach { line ->
                append(" $line\n")
            }
            append("└─────────────────────────────────────────────────────────────────────\n")
        }
        Log.i(LOG_TAG, responseLog)

        // 🔥 Recreate response body so Retrofit can read it again
        val contentType = response.body?.contentType()
        val newBody = rawBody.toResponseBody(contentType)
        return@Interceptor response.newBuilder().body(newBody).build()
    }

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }



    //====================******************============================//
    private const val MEDIA_URL = "https://ecrm3-nonremovable-uploads.s3.ap-southeast-1.amazonaws.com/"

    private val mediaResponseLogger = Interceptor { chain ->
        val response = chain.proceed(chain.request())

        val responseLog = buildString {
            append("│ Media: ${response.request.url}\n")
        }
        Log.i(LOG_TAG, responseLog)

        response
    }

    private val mediaClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(mediaResponseLogger)
        .build()

    val mediaService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(MEDIA_URL)
            .client(mediaClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
