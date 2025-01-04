//package com.example.apps1.retrofit
//
////import com.example.apps1.response.ResponsePodcast
//import com.example.apps1.response.ResponseSong
//import retrofit2.Call
//import retrofit2.http.GET
//import retrofit2.http.Path
//import retrofit2.http.Query
//
//interface ApiService {
//
//    @GET("song/users")
//    fun song(
//        @Query("song") username: String,
//    ): Call<ResponseSong>
//
//    @GET("podcast/podcast")
//    fun podcast(
//        @Path("username") username: String,
//    ): Call<ResponsePodcast>
//
//
//}