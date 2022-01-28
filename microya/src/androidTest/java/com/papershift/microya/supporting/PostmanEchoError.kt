package com.papershift.microya.supporting

import kotlinx.serialization.Serializable

@Serializable
class PostmanEchoError(val code: Int, val message: String)