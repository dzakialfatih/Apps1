package com.example.apps1.Activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apps1.Adapter.PodcastListAdapter
import com.example.apps1.Adapter.PodcasterListAdapter
import com.example.apps1.MainActivity.Podcast
import com.example.apps1.R
import com.example.apps1.response.ResponsePodcast
import com.example.apps1.retrofit.ApiConfig
import com.example.apps1.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PodcastActivity : AppCompatActivity() {
    data class Podcaster(val title: String, val description: String)
    private lateinit var recyclerViewPodcasts: RecyclerView
    private lateinit var podcastAdapter: PodcastListAdapter

    private lateinit var recyclerViewPodcaster: RecyclerView
    private lateinit var podcasterAdapter: PodcastListAdapter
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_podcast)

        // Inisialisasi RecyclerView
        recyclerViewPodcasts = findViewById(R.id.recyclerViewPodcasts)
        recyclerViewPodcasts.layoutManager = LinearLayoutManager(this)
        // Inisialisasi Adapter
        podcastAdapter = PodcastListAdapter(emptyList())
        recyclerViewPodcasts.adapter = podcastAdapter

        fetchListPodcast()

        // Inisialisasi RecyclerView untuk daftar podcaster
        recyclerViewPodcaster = findViewById(R.id.recyclerViewNamePodcaster)
        recyclerViewPodcaster.layoutManager = GridLayoutManager(this, 2) // 2 kolom
        recyclerViewPodcaster.adapter = PodcasterListAdapter(emptyList())

        fetchListPodcaster()
    }

    private fun fetchListPodcast() {
        val apiService = ApiConfig.getApiService()
        apiService.getPodcast().enqueue(object : Callback<List<ResponsePodcast>> {
            override fun onResponse(
                call: Call<List<ResponsePodcast>>,
                response: Response<List<ResponsePodcast>>
            ) {
                if (response.isSuccessful) {
                    val podcastList = response.body() ?: emptyList()

                    // Perbarui adapter dengan data dari API
                    podcastAdapter.updateData(podcastList)
                }
            }

            override fun onFailure(call: Call<List<ResponsePodcast>>, t: Throwable) {
                Log.e("PodcastActivity", "Error: ${t.message}")
            }
        })
    }

    private fun fetchListPodcaster() {
        val apiService = ApiConfig.getApiService()
        apiService.getPodcast().enqueue(object : Callback<List<ResponsePodcast>> {
            override fun onResponse(
                call: Call<List<ResponsePodcast>>,
                response: Response<List<ResponsePodcast>>
            ) {
                if (response.isSuccessful) {
                    val podcastList = response.body() ?: emptyList()

                    // Perbarui adapter dengan data dari API
                    podcasterAdapter.updateData(podcastList)
                }
            }

            override fun onFailure(call: Call<List<ResponsePodcast>>, t: Throwable) {
                Log.e("PodcastActivity", "Error: ${t.message}")
            }
        })
    }
    // Fungsi untuk data dummy podcast
    private fun getDummyData(): List<Podcast> {
        return listOf(
            Podcast("Judul Podcast 1", "Description 1", "Nama Podcaster 1"),
            Podcast("Judul Podcast 2", "Description 2", "Nama Podcaster 2"),
            Podcast("Judul Podcast 3", "Description 3", "Nama Podcaster 3"),
            Podcast("Judul Podcast 4", "Description 4", "Nama Podcaster 4"),
            Podcast("Judul Podcast 5", "Description 5", "Nama Podcaster 5")
        )
    }

    // Fungsi untuk data dummy podcaster
    private fun getDummyDataPodcaster(): List<Podcaster> {
        return listOf(
            Podcaster("Podcaster 1", "Description 1"),
            Podcaster("Podcaster 2", "Description 2"),
            Podcaster("Podcaster 3", "Description 3"),
            Podcaster("Podcaster 4", "Description 4"),
            Podcaster("Podcaster 5", "Description 5")
        )
    }
}
