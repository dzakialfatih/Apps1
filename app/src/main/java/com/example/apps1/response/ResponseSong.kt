package com.example.apps1.response

import com.google.gson.annotations.SerializedName

data class ResponseSong(

	@field:SerializedName("cache")
	val cache: Any,

	@field:SerializedName("listeners")
	val listeners: Listeners,

	@field:SerializedName("now_playing")
	val nowPlaying: NowPlaying,

	@field:SerializedName("song_history")
	val songHistory: List<SongHistoryItem>,

	@field:SerializedName("station")
	val station: Station,

	@field:SerializedName("is_online")
	val isOnline: Boolean,

	@field:SerializedName("live")
	val live: Live,

	@field:SerializedName("playing_next")
	val playingNext: Any
)

data class MountsItem(

	@field:SerializedName("path")
	val path: String,

	@field:SerializedName("listeners")
	val listeners: Listeners,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("format")
	val format: String,

	@field:SerializedName("bitrate")
	val bitrate: Int,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("is_default")
	val isDefault: Boolean,

	@field:SerializedName("url")
	val url: String
)

data class Listeners(

	@field:SerializedName("total")
	val total: Int,

	@field:SerializedName("current")
	val current: Int,

	@field:SerializedName("unique")
	val unique: Int
)

data class SongHistoryItem(

	@field:SerializedName("sh_id")
	val shId: Int,

	@field:SerializedName("duration")
	val duration: Int,

	@field:SerializedName("song")
	val song: Song,

	@field:SerializedName("is_request")
	val isRequest: Boolean,

	@field:SerializedName("streamer")
	val streamer: String,

	@field:SerializedName("playlist")
	val playlist: String,

	@field:SerializedName("played_at")
	val playedAt: Int
)

data class NowPlaying(

	@field:SerializedName("sh_id")
	val shId: Int,

	@field:SerializedName("duration")
	val duration: Int,

	@field:SerializedName("song")
	val song: Song,

	@field:SerializedName("elapsed")
	val elapsed: Int,

	@field:SerializedName("is_request")
	val isRequest: Boolean,

	@field:SerializedName("streamer")
	val streamer: String,

	@field:SerializedName("playlist")
	val playlist: String,

	@field:SerializedName("played_at")
	val playedAt: Int,

	@field:SerializedName("remaining")
	val remaining: Int
)

data class Station(

	@field:SerializedName("timezone")
	val timezone: String,

	@field:SerializedName("playlist_m3u_url")
	val playlistM3uUrl: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("mounts")
	val mounts: List<MountsItem>,

	@field:SerializedName("hls_listeners")
	val hlsListeners: Int,

	@field:SerializedName("hls_url")
	val hlsUrl: Any,

	@field:SerializedName("shortcode")
	val shortcode: String,

	@field:SerializedName("listen_url")
	val listenUrl: String,

	@field:SerializedName("public_player_url")
	val publicPlayerUrl: String,

	@field:SerializedName("url")
	val url: String,

	@field:SerializedName("hls_enabled")
	val hlsEnabled: Boolean,

	@field:SerializedName("hls_is_default")
	val hlsIsDefault: Boolean,

	@field:SerializedName("playlist_pls_url")
	val playlistPlsUrl: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("is_public")
	val isPublic: Boolean,

	@field:SerializedName("remotes")
	val remotes: List<Any>,

	@field:SerializedName("backend")
	val backend: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("frontend")
	val frontend: String
)

data class Live(

	@field:SerializedName("art")
	val art: Any,

	@field:SerializedName("is_live")
	val isLive: Boolean,

	@field:SerializedName("broadcast_start")
	val broadcastStart: Any,

	@field:SerializedName("streamer_name")
	val streamerName: String
)

data class Song(

	@field:SerializedName("art")
	val art: String,

	@field:SerializedName("artist")
	val artist: String,

	@field:SerializedName("custom_fields")
	val customFields: List<Any>,

	@field:SerializedName("album")
	val album: String,

	@field:SerializedName("genre")
	val genre: String,

	@field:SerializedName("isrc")
	val isrc: String,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("text")
	val text: String,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("lyrics")
	val lyrics: String
)
