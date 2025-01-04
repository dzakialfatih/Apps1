//package com.example.apps1.response
//
//import android.os.Parcelable
//
//@Parcelize
//data class ResponseSongs(
//	val cache: Any? = null,
//	val listeners: Listeners? = null,
//	val nowPlaying: NowPlaying? = null,
//	val songHistory: List<SongHistoryItem?>? = null,
//	val station: Station? = null,
//	val isOnline: Boolean? = null,
//	val live: Live? = null,
//	val playingNext: Any? = null
//) : Parcelable
//
//@Parcelize
//data class Listeners(
//	val total: Int? = null,
//	val current: Int? = null,
//	val unique: Int? = null
//) : Parcelable
//
//@Parcelize
//data class NowPlaying(
//	val shId: Int? = null,
//	val duration: Int? = null,
//	val song: Song? = null,
//	val elapsed: Int? = null,
//	val isRequest: Boolean? = null,
//	val streamer: String? = null,
//	val playlist: String? = null,
//	val playedAt: Int? = null,
//	val remaining: Int? = null
//) : Parcelable
//
//annotation class Parcelize
//
//@Parcelize
//data class MountsItem(
//	val path: String? = null,
//	val listeners: Listeners? = null,
//	val name: String? = null,
//	val format: String? = null,
//	val bitrate: Int? = null,
//	val id: Int? = null,
//	val isDefault: Boolean? = null,
//	val url: String? = null
//) : Parcelable
//
//@Parcelize
//data class Live(
//	val art: Any? = null,
//	val isLive: Boolean? = null,
//	val broadcastStart: Any? = null,
//	val streamerName: String? = null
//) : Parcelable
//
//@Parcelize
//data class SongHistoryItem(
//	val shId: Int? = null,
//	val duration: Int? = null,
//	val song: Song? = null,
//	val isRequest: Boolean? = null,
//	val streamer: String? = null,
//	val playlist: String? = null,
//	val playedAt: Int? = null
//) : Parcelable
//
//@Parcelize
//data class Station(
//	val timezone: String? = null,
//	val playlistM3uUrl: String? = null,
//	val description: String? = null,
//	val mounts: List<MountsItem?>? = null,
//	val hlsListeners: Int? = null,
//	val hlsUrl: Any? = null,
//	val shortcode: String? = null,
//	val listenUrl: String? = null,
//	val publicPlayerUrl: String? = null,
//	val url: String? = null,
//	val hlsEnabled: Boolean? = null,
//	val hlsIsDefault: Boolean? = null,
//	val playlistPlsUrl: String? = null,
//	val name: String? = null,
//	val isPublic: Boolean? = null,
//	val remotes: List<Any?>? = null,
//	val backend: String? = null,
//	val id: Int? = null,
//	val frontend: String? = null
//) : Parcelable
//
//@Parcelize
//data class Song(
//	val art: String? = null,
//	val artist: String? = null,
//	val customFields: List<Any?>? = null,
//	val album: String? = null,
//	val genre: String? = null,
//	val isrc: String? = null,
//	val id: String? = null,
//	val text: String? = null,
//	val title: String? = null,
//	val lyrics: String? = null
//) : Parcelable
