package com.papershift.microya.supporting

import com.papershift.microya.BuildConfig
import com.papershift.microya.core.ApiProvider
import com.papershift.microya.plugins.HttpAuthPlugin
import com.papershift.microya.core.EmptyBodyResponse
import com.papershift.microya.core.Endpoint
import com.papershift.microya.core.MockingBehaviour
import com.papershift.microya.plugins.RequestLoggerPlugin
import com.papershift.microya.plugins.ResponseLoggerPlugin
import okhttp3.OkHttpClient
import java.lang.IllegalArgumentException
import kotlin.time.Duration

val sampleApiProvider = ApiProvider.Builder().baseUrl("https://postman-echo.com").client(OkHttpClient())
    .plugins(listOf(HttpAuthPlugin(HttpAuthPlugin.Scheme.BASIC, "abc123"), RequestLoggerPlugin {
        TestDataStore.request = it
    }, ResponseLoggerPlugin {
        TestDataStore.response = it
    })).requestJsonFormatter(Serializer.requestJsonFormatter).responseJsonFormatter(Serializer.responseJsonFormatter).build()

val mockedImmediateApiProvider = ApiProvider.Builder().baseUrl("https://postman-echo.com").client(OkHttpClient())
    .plugins(listOf(HttpAuthPlugin(HttpAuthPlugin.Scheme.BASIC, "abc123"), RequestLoggerPlugin {
        TestDataStore.request = it
    }, ResponseLoggerPlugin {
        TestDataStore.response = it
    })).requestJsonFormatter(Serializer.requestJsonFormatter).responseJsonFormatter(Serializer.responseJsonFormatter)
    .mockingBehaviour(MockingBehaviour(Duration.ZERO)).build()

val mockedWithCustomResponseApiProvider = ApiProvider.Builder().baseUrl("https://postman-echo.com").client(OkHttpClient())
    .plugins(listOf(HttpAuthPlugin(HttpAuthPlugin.Scheme.BASIC, "abc123"), RequestLoggerPlugin {
        TestDataStore.request = it
    }, ResponseLoggerPlugin {
        TestDataStore.response = it
    })).requestJsonFormatter(Serializer.requestJsonFormatter).responseJsonFormatter(Serializer.responseJsonFormatter)
    .mockingBehaviour(MockingBehaviour(Duration.ZERO) { endpoint: Endpoint ->
        when (endpoint) {
            is PostmanEchoEndpoint.Index -> {
                endpoint.mockResponseObject(200, EmptyBodyResponse())
            }
            is PostmanEchoEndpoint.Post -> {
                null
            }
            is PostmanEchoEndpoint.Get -> {
                endpoint.mockResponseObject(200, FooBar("Sakata", "Gintoki"))
            }

            is PostmanEchoEndpoint.Patch -> {
                endpoint.mockResponseObject(200, FooBar("test", "1234"))
            }
            else -> throw  IllegalArgumentException("Endpoint doesn't exist.")
        }
    }).build()

val uploadFileSampleApiProvider = ApiProvider.Builder().baseUrl("https://api.imgur.com").client(OkHttpClient())
    .plugins(listOf(HttpAuthPlugin(HttpAuthPlugin.Scheme.CUSTOM, "Client-ID ${BuildConfig.IMGUR_API_KEY}"), RequestLoggerPlugin {
        TestDataStore.request = it
    }, ResponseLoggerPlugin {
        TestDataStore.response = it
    })
    ).requestJsonFormatter(Serializer.requestJsonFormatter).responseJsonFormatter(Serializer.responseJsonFormatter).build()