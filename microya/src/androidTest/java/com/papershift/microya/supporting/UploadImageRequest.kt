package com.papershift.microya.supporting

import kotlinx.serialization.Serializable

@Serializable
data class UploadImageRequest(
    val title: String,
    val description: String,
)
