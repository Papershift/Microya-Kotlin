package com.papershift.microya.supporting

import com.papershift.microya.core.Endpoint
import com.papershift.microya.core.MockedResponse

sealed class ImgurEndpoint : Endpoint() {
    data class UploadImageEndpoint(val body: UploadImageRequest) : ImgurEndpoint()

    override val subpath: String
        get() = when (this) {
            is UploadImageEndpoint -> "3/image"
        }
    override val method: HttpMethod
        get() = when (this) {
            is UploadImageEndpoint -> HttpMethod.Post(body)
        }
    override val headers: Map<String, String>
        get() = emptyMap()
    override val queryParameters: Map<String, String>
        get() = emptyMap()
    override val mockedResponse: MockedResponse?
        get() = null
}