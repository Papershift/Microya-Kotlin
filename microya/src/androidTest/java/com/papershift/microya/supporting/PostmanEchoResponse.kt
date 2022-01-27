package com.papershift.microya.supporting

import kotlinx.serialization.Serializable

@Serializable
class PostmanEchoResponse(
    val url: String,
    val headers: Map<String, String>,
    val args: Map<String, String>
)