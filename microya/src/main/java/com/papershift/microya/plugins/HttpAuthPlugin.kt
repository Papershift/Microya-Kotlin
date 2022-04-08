package com.papershift.microya.plugins

import com.papershift.microya.core.Endpoint
import com.papershift.microya.core.Plugin
import okhttp3.Request
import okhttp3.Response

/**
 * Provides support for the HTTP "Authorization" header based on the "Basic" or "Bearer" schemes.
 * See also: https://developer.mozilla.org/en-US/docs/Web/HTTP/Authentication
 * @property [scheme] is the authentication scheme.
 * @property [token] the access token
 */
class HttpAuthPlugin(val scheme: Scheme, val token: String) : Plugin {

    enum class Scheme(val info: String) {
        BASIC("Basic"),
        BEARER("Bearer"),
        CUSTOM("")
    }

    override fun modifyRequest(request: Request, endpoint: Endpoint): Request {
        return if (token.isNotBlank()) {
            request.newBuilder().addHeader("Authorization", "${scheme.info} $token").build()
        } else {
            request
        }
    }

    override fun beforeRequest(request: Request) {
        { /** No operation **/ }
    }

    override fun <T> afterRequest(response: Response, typedResult: T?, endpoint: Endpoint) {
        { /** No operation **/ }
    }
}
