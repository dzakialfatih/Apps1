package com.example.apps1.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class ResponseEpisodes(
    val id: String? = null,
    val title: String? = null,
    val link: String? = null,
    val description: String? = null,
    val descriptionShort: String? = null,
    val explicit: Boolean? = null,
    val seasonNumber: Int? = null,
    val episodeNumber: Int? = null,
    val createdAt: Long? = null,
    val publishAt: Long? = null,
    val isPublished: Boolean? = null,
    val hasMedia: Boolean? = null,
    val media: Media? = null,
    val hasCustomArt: Boolean? = null,
    val art: String? = null,
    val artUpdatedAt: Long? = null,
    val links: EpLinks? = null
) : Parcelable

@Parcelize
data class Media(
    val id: String? = null,
    val originalName: String? = null,
    val length: Double? = null,
    val lengthText: String? = null,
    val path: String? = null
) : Parcelable

@Parcelize
data class EpLinks(
    val self: String? = null,
    val public: String? = null,
    val download: String? = null,
    val art: String? = null,
    val media: String? = null
) : Parcelable