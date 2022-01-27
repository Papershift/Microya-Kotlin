package com.papershift.microya.core

import kotlinx.serialization.SerializationException

/** Collection of all possible exception that can be thrown when using `JsonApi`. */
sealed class JsonApiException : Exception() {
    /** The request was sent, but the server response was not received. Typically an issue with the internet connection. */
    object NoResponseReceived : JsonApiException()

    /** The request was sent and the server responded, but the response did not include any body although a body was requested. */
    object NoDataInResponse : JsonApiException()

    /** The request was sent and the server responded with a body, but the conversion of the body to the given type failed. */
    data class ResponseDataConversionFailed(val type: String, val exception: SerializationException) : JsonApiException()

    /** The request was sent and the server responded, but the server reports that something is wrong with the request. */
    data class ClientError(val statusCode: Int, private val errorBody: Any?) : JsonApiException() {
        /**
         * Gets the error body as a concrete type.
         * @param T is the type of the error body to be return.
         * @return T is the error body gotten from the server.
         */
        fun <T> getBody(): T = requireNotNull(errorBody as T) { "Error body must not be null" }
    }

    /** The request was sent and the server responded, but there seems to be an error which needs to be fixed on the server. */
    data class ServerError(val statusCode: Int) : JsonApiException()

    /** The request was sent and the server responded, but with an unexpected status code. */
    data class UnexpectedStatusCode(val statusCode: Int) : JsonApiException()

    /** An unexpected exception was thrown. This should probably be fixed in the `JsonApi` class implementation itself. */
    data class UnexpectedException(val exception: Exception) : JsonApiException()

    /**
     *  The [mockingBehavior] was set to non-nil (for testing) but no `mockedResponse` was provided for the requested endpoint.
     */
    object EmptyMockedResponse : JsonApiException()
}
