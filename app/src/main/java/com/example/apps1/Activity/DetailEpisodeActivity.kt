package com.example.apps1.Activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.apps1.Adapter.EpisodesListAdapter
import com.example.apps1.Adapter.MediaListAdapter
import com.example.apps1.R
import com.example.apps1.response.ResponseEpisodes
import com.example.apps1.response.ResponseMedia
import com.example.apps1.response.ResponsePodcast
import com.example.apps1.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailEpisodeActivity : AppCompatActivity() {
    private lateinit var imgItemPhoto: ImageView
    private lateinit var imageViewBack: ImageView
    private lateinit var tvItemName: TextView
    private lateinit var tvItemTotal: TextView
    private lateinit var tvItemDesc: TextView

    private lateinit var recyclerViewMedia: RecyclerView
    private lateinit var mediaListAdapter: MediaListAdapter


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_podcast_episodes)

        // Inisialisasi View
        imgItemPhoto = findViewById(R.id.img_item_photo)
        tvItemName = findViewById(R.id.tv_item_name)
        tvItemDesc = findViewById(R.id.tv_item_description)
        tvItemTotal = findViewById(R.id.tv_item_total)
        imageViewBack = findViewById(R.id.imageView4)

        // Inisialisasi RecyclerView dan Adapter
        recyclerViewMedia = findViewById(R.id.recyclerViewEpisodes)
        recyclerViewMedia.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mediaListAdapter = MediaListAdapter(emptyList())
        recyclerViewMedia.adapter = mediaListAdapter

        // Ambil data dari Intent
        intent?.let {
            val id = it.getStringExtra("EPISODE_ID") // Ambil ID dengan aman
            val podcastId = it.getStringExtra("PODCAST_ID")

            // Pastikan ID tidak null sebelum memanggil API
            podcastId?.let { id ->
                Log.d("Media ID", "Podcast ID: $id") // Cetak ID ke Logcat
                fetchEpisodeData(podcastId, id) // Kirim ke API
            } ?: Log.e("DetailPodcastActivity", "Podcast ID is null") // Tampilkan error jika null

        }

        // Tombol kembali ke halaman sebelumnya
        imageViewBack.setOnClickListener { onBackPressed() }
    }

    private fun fetchEpisodeData(podcastId: String, id: String) {
        val apiService = ApiConfig.getApiService()
        apiService.getDetailEpisode(podcastId, id).enqueue(object : Callback<List<ResponseMedia>> {

            override fun onResponse(
                call: Call<List<ResponseMedia>>,
                response: Response<List<ResponseMedia>>
            ) {
                if (response.isSuccessful) {
                    val episodesList = response.body() ?: emptyList()
                    mediaListAdapter.updateData(episodesList) // Perbarui data di RecyclerView
                } else {
                    Log.e("DetailPodcastActivity", "Gagal mendapatkan episode: ${response.errorBody()?.string()}")
                }            }

            override fun onFailure(call: Call<List<ResponseMedia>>, t: Throwable) {
                Log.e("DetailPodcastActivity", "Error mengambil episode: ${t.message}")
            }
        })
    }

}