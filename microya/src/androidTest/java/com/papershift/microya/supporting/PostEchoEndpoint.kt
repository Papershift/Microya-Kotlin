package com.papershift.microya.supporting

import com.papershift.microya.core.EmptyBodyResponse
import com.papershift.microya.core.Endpoint
import com.papershift.microya.core.MockedResponse
import java.util.Locale

sealed class PostmanEchoEndpoint : Endpoint() {
    data class Index(val sortedBy: String) : PostmanEchoEndpoint()
    data class Post(val fooBar: FooBar) : PostmanEchoEndpoint()
    data class Get(val fooBarID: String) : PostmanEchoEndpoint()
    data class Patch(val fooBarID: String, val fooBar: FooBar) : PostmanEchoEndpoint()
    object Delete : PostmanEchoEndpoint()

    override val subpath: String
        get() = when (this) {
            is Get -> "get/${fooBarID}"
            is Index -> "get"
            is Patch -> "patch/${fooBarID}"
            is Post -> "post"
            Delete -> "delete"
        }

    override val method: HttpMethod
        get() = when (this) {
            is Get -> HttpMethod.Get
            is Index -> HttpMethod.Get
            is Patch -> HttpMethod.Patch(fooBar)
            is Post -> HttpMethod.Post(fooBar)
            Delete -> HttpMethod.Delete
        }
    override val headers: Map<String, String>
        get() = mapOf(
            "Content-Type" to "application/json",
            "Accept" to "application/json",
            "Accept-Language" to Locale.getDefault().language
        )

    override val queryParameters: Map<String, String>
        get() = when (this) {
            is Index -> mapOf("sortedBy" to sortedBy)
            else -> emptyMap()
        }
    override val mockedResponse: MockedResponse?
        get() = when (this) {
            is Get -> {
                mockResponseObject(200, FooBar("foo${fooBarID}", "bar${fooBarID}"))
            }
            Delete -> {
                mockResponseObject(200, EmptyBodyResponse())
            }
            else -> null
        }
}
