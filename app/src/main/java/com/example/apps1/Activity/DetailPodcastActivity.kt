package com.example.apps1.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.apps1.Adapter.EpisodesListAdapter
import com.example.apps1.R
import com.example.apps1.response.ResponseEpisodes
import com.example.apps1.response.ResponsePodcast
import com.example.apps1.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailPodcastActivity : AppCompatActivity() {
    private lateinit var imgItemPhoto: ImageView
    private lateinit var imageViewBack: ImageView
    private lateinit var tvItemName: TextView
    private lateinit var tvItemTotal: TextView
    private lateinit var tvItemCount: TextView

    private lateinit var recyclerViewEpisodes: RecyclerView
    private lateinit var episodesAdapter: EpisodesListAdapter


    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_podcast)

        // Inisialisasi View
        imgItemPhoto = findViewById(R.id.img_item_photo)
        tvItemName = findViewById(R.id.tv_item_name)
        tvItemTotal = findViewById(R.id.tv_item_total)
        tvItemCount = findViewById(R.id.tv_item_count)
        imageViewBack = findViewById(R.id.imageView4)

        val podcastId: String? = intent?.getStringExtra("PODCAST_ID")
        val total: String? = intent?.getIntExtra("PODCAST_COUNT", 0).toString()
        tvItemCount.text = "Total Episodes : $total"


        // Ambil data dari Intent
        intent?.let {
            val title = it.getStringExtra("PODCAST_TITLE")
            val imageUrl = it.getStringExtra("PODCAST_IMAGE")
            val description = it.getStringExtra("PODCAST_DESCRIPTION")

            Log.d("DetailPodcastActivity", "Podcast ID: $podcastId") // Log ID yang diterima

            // Set data ke UI
            tvItemName.text = title
            tvItemTotal.text = description
            Glide.with(this).load(imageUrl).into(imgItemPhoto)

        }

        // Inisialisasi RecyclerView dan Adapter
        recyclerViewEpisodes = findViewById(R.id.recyclerViewEpisodes)
        recyclerViewEpisodes.layoutManager =  LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        episodesAdapter = EpisodesListAdapter(emptyList())
            { episode ->
            if (episode.id != null && podcastId != null) {
                Log.d("episodeId", "episodeId: ${episode.id}")
                Log.d("podcastId", "podcastId: $podcastId")
                Log.d("episode", "dari Podcast: $total")


                val intent = Intent(this, DetailEpisodeActivity::class.java).apply {
                    putExtra("EPISODE_ID", episode.id)
                    putExtra("PODCAST_ID", podcastId)
                    putExtra("PODCAST_COUNT", total)

                }
                startActivity(intent)
            } else {
                Log.e("EpisodesAdapter", "Episode ID or Podcast ID is null")
            }
        }

        // Set adapter ke RecyclerView
        recyclerViewEpisodes.adapter = episodesAdapter

        if (podcastId != null) {
            fetchEpisodesData(podcastId)
        }

        // Tombol kembali ke halaman sebelumnya
        imageViewBack.setOnClickListener { onBackPressed() }
    }

    private fun fetchEpisodesData(podcastId: String) {
        val apiService = ApiConfig.getApiService()
        apiService.getEpisodes(podcastId).enqueue(object : Callback<List<ResponseEpisodes>> {
            override fun onResponse(call: Call<List<ResponseEpisodes>>, response: Response<List<ResponseEpisodes>>) {
                if (response.isSuccessful) {
                    val episodesList = response.body() ?: emptyList()
                    episodesAdapter.updateData(episodesList) // Perbarui data di RecyclerView
                } else {
                    Log.e("DetailPodcastActivity", "Gagal mendapatkan episode: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<ResponseEpisodes>>, t: Throwable) {
                Log.e("DetailPodcastActivity", "Error mengambil episode: ${t.message}")
            }
        })
    }

}