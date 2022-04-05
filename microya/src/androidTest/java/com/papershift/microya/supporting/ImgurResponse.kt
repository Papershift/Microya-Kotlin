package com.papershift.microya.supporting


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImgurResponse<T>(
    @SerialName("data")
    val data: T,
    @SerialName("status")
    val status: Int?,
    @SerialName("success")
    val success: Boolean?
)