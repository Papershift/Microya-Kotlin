package com.papershift.microya.supporting

import com.papershift.microya.core.FileDataPart
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class UploadImageRequest(
    val title: String,
    val description: String,
    @Transient
    val fileDataParts: List<FileDataPart> = emptyList()
)
