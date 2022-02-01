package com.papershift.microya.core

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody

/**
 *  The mocked response to use for testing purposes.
 * @property subPath is the subPath to be added to the base URL.
 * @property statusCode is the status code to be returned for the mocked response.
 * @property headers is the headers to be returned as part of the mocked response.
 * @property responseBody: is the response to be returned. This will be null if empty.
 */
class MockedResponse {
    var subPath: String
        private set
    var statusCode: Int = 0
        private set
    var responseBody: ResponseBody? = null
        private set
    var headers: Map<String, String> = emptyMap()
        private set

    /**
     * Initializes a mocked response for testing purposes. Requires at least a status code. Headers and body are optional.
     * Consider using the convenience [.mock] factory method in the [Endpoint] type instead,  which will detect the [subPath] automatically.
     */
    constructor(
        subPath: String,
        statusCode: Int,
        responseBody: ResponseBody?,
        headers: Map<String, String> = emptyMap()
    ) {
        this.subPath = subPath
        this.statusCode = statusCode
        this.responseBody = responseBody
        this.headers = headers
    }

    /**
     * Initializes a mocked response for testing purposes. Requires at least a status code. Headers and body are optional.
     * Consider using the convenience [.mock] factory method in the [Endpoint] type instead,  which will detect the [subPath] automatically.
     * Provided json will be converted to a [ResponseBody]
     */
    constructor(
        subPath: String,
        statusCode: Int,
        bodyJson: String?,
        headers: Map<String, String> = emptyMap()
    ) {
        this.subPath = subPath
        this.statusCode = statusCode
        this.responseBody = bodyJson?.toResponseBody()
        this.headers = headers
    }

    fun buildUrl(baseUrl: String): HttpUrl =
        baseUrl.toHttpUrl().newBuilder().addEncodedPathSegments(subPath).build()

    fun httpUrlResponse(url: String): Response = Response.Builder().apply {
        this.code(statusCode)
        this.protocol(Protocol.HTTP_1_1)
        this.message(getStatusMessage(statusCode))
        this.request(Request.Builder().url(buildUrl(url)).build())
        for ((name, value) in headers) {
            this.addHeader(name, value)
        }
        responseBody?.let { this.body(responseBody) }
    }.build()

    private fun getStatusMessage(statusCode: Int): String {
        return when (statusCode) {
            in 100..199 -> "Informational"
            in 200..299 -> "OK"
            in 300..399 -> "Redirection"
            in 400..499 -> "Client Error"
            in 500..599 -> "Server Error"
            else -> throw IllegalArgumentException(
                "Support for this status code doesn't exist yet. " +
                        "To add support for it. Please update the MockedResponse class in the API-Client"
            )
        }
    }
}
