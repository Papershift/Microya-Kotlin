package com.papershift.microya.core

import okhttp3.Request
import okhttp3.Response

/**
 * A Plugin receives callbacks to perform side effects wherever a request is sent or received.
 * for example, a plugin may be used to
- log network requests
- hide and show a network activity indicator
- inject additional information into a request (like for authentication)
 */
interface Plugin {
    /**
     * Called to modify a request before sending.
     */
    fun modifyRequest(request: Request, endpoint: Endpoint): Request

    /**
     * Called immediately before a request is sent.
     */
    fun beforeRequest(request: Request)

    /**
     * Called after a response has been received & decoded, but before calling the completion handler.
     */
    fun <T> afterRequest(response: Response, typedResult: T? = null, endpoint: Endpoint)
}
