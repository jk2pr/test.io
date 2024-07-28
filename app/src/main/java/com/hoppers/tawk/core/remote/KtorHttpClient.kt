package com.hoppers.tawk.core.remote

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.header
import io.ktor.http.ContentType.Application.Json
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.io.IOException
import java.net.UnknownHostException
import kotlin.math.pow
import kotlin.random.Random

/**
 * Timeout duration for network requests in milliseconds.
 */
private const val TIME_OUT = 60_000

/**
 * Ktor HTTP client configured for Android platform with various features installed.
 */

val ktorHttpClient = HttpClient(Android) {
    followRedirects = false

    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }
        )

        engine {
            connectTimeout = TIME_OUT
            socketTimeout = TIME_OUT
        }
    }
    install(Logging) {
        logger = Logger.ANDROID
        level = LogLevel.ALL
    }


    install(ResponseObserver) {
        onResponse { response ->
            Log.d("HTTP Url:", "${response.call.request.url}")
        }
    }

    install(DefaultRequest) {
        header(HttpHeaders.ContentType, Json)
        url("https://api.github.com/users")

    }

    // Install request retry mechanism with exponential backoff


    install(HttpRequestRetry) {
        retryOnExceptionIf(maxRetries = 3) { _, throwable ->
            when (throwable) {
                is UnknownHostException,
                is IOException -> {
                    Log.d("Retry", "Retrying request due to network error: ${throwable.message}")
                    true
                }

                else -> false
            }
        }
        delayMillis { retry ->
            val baseDelay = 2000L
            (baseDelay * 2.0.pow(retry.toDouble())).toLong() + Random.nextLong(baseDelay)

        }
    }
}
