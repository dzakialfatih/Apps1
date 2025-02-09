package com.example.apps1.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResponseMedia(
    val id: String,
    val title: String,
    val link: String,
    val description: String,
    val description_short: String,
    val explicit: Boolean,
    val season_number: Int,
    val episode_number: Int,
    val created_at: Long,
    val publish_at: Long,
    val is_published: Boolean,
    val has_media: Boolean,
    val playlist_media_id: String?,
    val playlist_media: String?,
    val media: Play? = null,
    val has_custom_art: Boolean,
    val art: String,
    val art_updated_at: Long,
    val links: PlayLink
) : Parcelable

@Parcelize
data class Play(
    val id: String,
    val original_name: String,
    val length: Double,
    val length_text: String,
    val path: String
) : Parcelable

@Parcelize
data class PlayLink(
    val self: String,
    val public: String,
    val download: String,
    val art: String,
    val media: String
) : Parcelable
