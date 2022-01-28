package com.papershift.microya.plugins

import com.papershift.microya.core.Endpoint
import com.papershift.microya.core.Plugin
import okhttp3.Request
import okhttp3.Response

/**
 *  Allows to log responses the given way provided by a closure after the response has been received.
 *  @property loggerCallback is the callback that is called after every request.
 */
class ResponseLoggerPlugin(val loggerCallback: (response: Response) -> Unit) : Plugin {
    override fun <T> afterRequest(response: Response, typedResult: T?, endpoint: Endpoint) {
        loggerCallback(response)
    }

    override fun beforeRequest(request: Request) {
        /** No operation **/
    }

    override fun modifyRequest(request: Request, endpoint: Endpoint): Request = request
}
