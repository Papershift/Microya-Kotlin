package com.papershift.microya.supporting

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ImgurErrorResponse(
    @SerialName("error")
    val error: String,
    @SerialName("method")
    val method: String,
    @SerialName("request")
    val request: String
)