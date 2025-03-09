package com.example.customcompose.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


object RetrofitInstance {
    private const val BASE_URL = "https://ecrm-prod3.v2.ltd"
    private const val LOG_TAG = "API_LOG"

    private val client = OkHttpClient.Builder()
        .addInterceptor(createLoggingInterceptor())
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private fun createLoggingInterceptor(): Interceptor = Interceptor { chain ->
        val request = chain.request()
        val requestBody = request.body

        // Log Request
        logRequest(request, requestBody)

        val response = chain.proceed(request)

        // Log Response
        logResponse(response)

        response
    }

    private fun logRequest(request: Request, requestBody: RequestBody?) {
        val logBuilder = StringBuilder().apply {
            append("┌────── Request ────────────────────────────────────────────────────────────────────────\n")
            append(" I  │ URL: ${request.url}\n")
            append(" I  │ \n")
            append(" I  │ Method: @${request.method}\n")
            append(" I  │ \n")
        }

        // Log Headers
        request.headers.forEach { header ->
            logBuilder.append(" I  │ $header: ${request.header(header.first)}\n")
        }

        // Log Request Body
        requestBody?.let {
            val buffer = Buffer()
            it.writeTo(buffer)
            val bodyString = buffer.readUtf8()
            logBuilder.append(" I  │ Body:\n")
            bodyString.split("\n").forEach { line ->
                logBuilder.append(" I  │ $line\n")
            }
        }

        logBuilder.append(" I  └────────────────────────────────────────────────────────────────────────────")
        Log.d(LOG_TAG, logBuilder.toString())
    }

    private fun logResponse(response: Response) {
        val logBuilder = StringBuilder().apply {
            append("┌────── Response ───────────────────────────────────────────────────────────────────────\n")
            append(" I  │ URL: ${response.request.url}\n")
            append(" I  │ \n")
            append(" I  │ Status Code: ${response.code} / ${response.message}\n")
            append(" I  │ \n")
            append(" I  │ Headers:\n")
        }

        // Log Response Headers
        response.headers.forEach { header ->
            logBuilder.append(" I  │ ${header.first}: ${header.second}\n")
        }

        // Log Response Body
        response.peekBody(2048).string().let { body ->
            logBuilder.append(" I  │ Body:\n")
            body.split("\n").forEach { line ->
                logBuilder.append(" I  │ $line\n")
            }
        }

        logBuilder.append(" I  └────────────────────────────────────────────────────────────────────────────")
        Log.d(LOG_TAG, logBuilder.toString())
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