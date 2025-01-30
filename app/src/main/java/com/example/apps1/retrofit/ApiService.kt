package com.example.apps1.retrofit//package com.example.apps1.retrofit

//import com.example.apps1.response.ResponsePodcast
import com.example.apps1.response.ResponsePodcast
import com.example.apps1.response.ResponseSong
import com.example.apps1.response.SongHistoryItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("station/18/history")
    fun getSongs(
        @Query("start") start: String
    ): Call<List<SongHistoryItem>>

    @GET("station/18/podcasts")
    fun getPodcast(): Call<List<ResponsePodcast>>

}