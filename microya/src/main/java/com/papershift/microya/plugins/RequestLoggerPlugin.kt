package com.papershift.microya.plugins

import com.papershift.microya.core.Endpoint
import com.papershift.microya.core.Plugin
import okhttp3.Request
import okhttp3.Response

/**
 * Allows to log requests the given way provided by a closure before the requests are sent.
 *  @property loggerCallback is the callback that is called before every request.
 */
class RequestLoggerPlugin(val loggerCallback: (request: Request) -> Unit) : Plugin {
    override fun <T> afterRequest(response: Response, typedResult: T?, endpoint: Endpoint) {
        /** No operation **/
    }

    override fun beforeRequest(request: Request) {
        loggerCallback(request)
    }

    override fun modifyRequest(request: Request, endpoint: Endpoint): Request = request
}
