package com.papershift.microya.core

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.net.URLConnection

/**
 * The file part to be included in multipart request.
 * @property file is the file to be include in the multipart request.
 * @property name is the name of the file data part to be sent. Defaults to the nameWithoutExtension of the file.
 * @property mediaType is the media type of the file. Defaults to media type guess from the name.
 */
data class FileDataPart(
    val file: File,
    val name: String = file.nameWithoutExtension,
    val mediaType: MediaType? = URLConnection.guessContentTypeFromName(file.name)
        ?.toMediaTypeOrNull()
) {
    init {
        require(mediaType != null) {
            "Could not determine media type of file. Please explicitly specify the media type of the file."
        }
    }

    fun asRequestBody(): RequestBody = file.asRequestBody(mediaType!!)
}
