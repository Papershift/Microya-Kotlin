package com.papershift.microya.supporting


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UploadSuccessResponse(
    @SerialName("account_id")
    val accountId: Int?,
    @SerialName("account_url")
    val accountUrl: String?,
    @SerialName("ad_type")
    val adType: Int?,
    @SerialName("ad_url")
    val adUrl: String?,
    @SerialName("animated")
    val animated: Boolean?,
    @SerialName("bandwidth")
    val bandwidth: Int?,
    @SerialName("datetime")
    val datetime: Int?,
    @SerialName("deletehash")
    val deletehash: String?,
    @SerialName("description")
    val description: String?,
    @SerialName("favorite")
    val favorite: Boolean?,
    @SerialName("height")
    val height: Int?,
    @SerialName("id")
    val id: String?,
    @SerialName("in_gallery")
    val inGallery: Boolean?,
    @SerialName("in_most_viral")
    val inMostViral: Boolean?,
    @SerialName("is_ad")
    val isAd: Boolean?,
    @SerialName("link")
    val link: String?,
    @SerialName("name")
    val name: String?,
    @SerialName("nsfw")
    val nsfw: String?,
    @SerialName("section")
    val section: String?,
    @SerialName("size")
    val size: Int?,
    @SerialName("tags")
    val tags: List<String>?,
    @SerialName("title")
    val title: String?,
    @SerialName("type")
    val type: String?,
    @SerialName("views")
    val views: Int?,
    @SerialName("vote")
    val vote: String?,
    @SerialName("width")
    val width: Int?
)