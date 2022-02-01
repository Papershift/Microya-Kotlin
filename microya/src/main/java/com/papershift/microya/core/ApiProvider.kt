package com.papershift.microya.core

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume

/**
 *The API provider class to make the requests on.
 * @property baseUrl The common base URL of the API endpoints.
 * @property client The http client with a default implementation.
 * @property plugins The plugins to apply per request.
 * @property mockingBehaviour The mocking behavior of the provider. Set this to receive mocked data in your tests.
 * Use null to make actual requests to your server (the default).
 * @property requestJsonFormatter The json serializer used to serialise requests.
 * @property responseJsonFormatter The json serializer used to serialise response.
 */
@Suppress("MaxLineLength")
class ApiProvider private constructor(
    val baseUrl: String,
    val client: OkHttpClient = OkHttpClient(),
    val plugins: List<Plugin> = emptyList(),
    val mockingBehaviour: MockingBehaviour<Endpoint>? = null,
    val requestJsonFormatter: Json,
    val responseJsonFormatter: Json
) {
    data class Builder(
        private var baseUrl: String? = null,
        private var client: OkHttpClient? = OkHttpClient(),
        private var plugins: List<Plugin> = emptyList(),
        private var mockingBehaviour: MockingBehaviour<Endpoint>? = null,
        private var requestJsonFormatter: Json? = null,
        private var responseJsonFormatter: Json? = null
    ) {
        /**
         * Sets up the common base URL of the API endpoints.
         * @param url common base URL of the API endpoints.
         */
        fun baseUrl(url: String) = apply { this.baseUrl = url }

        /**
         * Sets up the http client used to make api calls.
         * @param [client] http client used to make api calls.
         */
        fun client(client: OkHttpClient) = apply { this.client = client }

        /**
         * Sets up plugins to be apply to each requests.
         * @param plugins The plugins to apply per request.
         */
        fun plugins(plugins: List<Plugin>) = apply { this.plugins = plugins }

        /**
         * Sets up the mocking behavior of the provider
         * Set this to receive mocked data in your tests.
         * Use null to make actual requests to your server (the default).
         * @param mockingBehaviour is the user defined mocking behaviour of the API provider.
         */
        fun mockingBehaviour(mockingBehaviour: MockingBehaviour<Endpoint>?) =
            apply { this.mockingBehaviour = mockingBehaviour }

        /**
         * Sets up  the json serializer used for serialising json requests.
         * @param json is the json serializer used to serialise requests.
         */
        fun requestJsonFormatter(json: Json) = apply { this.requestJsonFormatter = json }

        /**
         * Sets up the json serializer used for serialising json response.
         * @param json is the json serializer used to serialise response.
         */
        fun responseJsonFormatter(json: Json) = apply { this.responseJsonFormatter = json }

        fun build() = ApiProvider(
            requireNotNull(baseUrl) { "Base URL should not be null." },
            requireNotNull(client) { "Client should not be null." },
            plugins,
            mockingBehaviour,
            requireNotNull(requestJsonFormatter) { "RequestJson Formatter should not be null." },
            requireNotNull(responseJsonFormatter) { "ResponseJson Formatter should not be null." }
        )
    }

    /**
     * Makes the network request using the endpoint provided.
     * @param Success is the type object returned if the api call is successful.
     * @param ClientError is the type [ClientError] returned if the api call fails. This is used to decode the error returned from the server.
     * @return a result containing the [Success] object if successful or a [JsonApiException] in the case of a failure.
     */
    suspend inline fun <reified Success : Any, reified ClientError : Any> performRequest(
        endpoint: Endpoint
    ): Result<Success, JsonApiException> {
        if (mockingBehaviour != null) {
            delay(mockingBehaviour.delay.inWholeMilliseconds)
        }

        var request = endpoint.buildRequest(baseUrl, requestJsonFormatter)
        for (plugin in plugins) {
            // Kotlin doesn't have the cool inout feature in swift. So i am mutating the request here.
            request = plugin.modifyRequest(request, endpoint)
        }
        for (plugin in plugins) {
            plugin.beforeRequest(request)
        }
        // Returns mocked responses if mocking behaviour is turned on.
        if (mockingBehaviour != null) {
            val mockedResponse =
                mockingBehaviour.mockResponseProvider(endpoint)?.httpUrlResponse(baseUrl)
            return if (mockedResponse != null) {
                decodeResponse<Success, ClientError>(mockedResponse, endpoint)
            } else {
                Err(JsonApiException.EmptyMockedResponse)
            }
        } else {
            return suspendCancellableCoroutine { continuation ->
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        if (continuation.isCancelled) return
                        continuation.resume(Err(JsonApiException.NoResponseReceived))
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val result = decodeResponse<Success, ClientError>(response, endpoint)
                        continuation.resume(result)
                    }
                })
            }
        }
    }

    fun <T> onRequestComplete(response: Response, typedResponse: T, endpoint: Endpoint) {
        for (plugin in plugins) {
            plugin.afterRequest(response, typedResponse, endpoint)
        }
    }

    inline fun <reified Success, reified ClientError> decodeResponse(
        response: Response,
        endpoint: Endpoint
    ): Result<Success, JsonApiException> {
        return when (response.code) {
            in 200..299 -> {
                if (response.body != null) {
                    try {
                        val responseBody = response.body!!.string()
                        if (responseBody.isNotEmpty()) {
                            val typedResult: Success =
                                responseJsonFormatter.decodeFromString(responseBody)
                            onRequestComplete(response, typedResult, endpoint)
                            Ok(typedResult)
                        } else {
                            val noResponseBody: Success = EmptyBodyResponse() as Success
                            Ok(noResponseBody)
                        }
                    } catch (serializationException: SerializationException) {
                        Err(
                            JsonApiException.ResponseDataConversionFailed(
                                type = Success::class.toString(),
                                exception = serializationException
                            )
                        )
                    } catch (exception: Exception) {
                        onRequestComplete(response, null, endpoint)
                        Err(JsonApiException.UnexpectedException(exception))
                    }
                } else {
                    onRequestComplete(response, null, endpoint)
                    Err(JsonApiException.NoDataInResponse)
                }
            }
            in 400..499 -> {
                onRequestComplete(response, null, endpoint)
                val errorResponse = response.body!!.string()
                val errorBody = if (errorResponse.isEmpty()) {
                    null
                } else {
                    Json.decodeFromString<ClientError>(errorResponse)
                }
                Err(
                    JsonApiException.ClientError(
                        statusCode = response.code,
                        errorBody = errorBody
                    )
                )
            }
            in 500..599 -> {
                onRequestComplete(response, null, endpoint)
                Err(JsonApiException.ServerError(statusCode = response.code))
            }
            else -> {
                onRequestComplete(response, null, endpoint)
                Err(JsonApiException.UnexpectedStatusCode(statusCode = response.code))
            }
        }
    }
}
