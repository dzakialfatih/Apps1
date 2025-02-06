package com.example.apps1.Activity

import android.annotation.SuppressLint
import android.content.Intent
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
import com.example.apps1.response.ResponseEpisodes
import com.example.apps1.response.ResponsePodcast
import com.example.apps1.retrofit.ApiConfig
import com.example.apps1.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PodcastActivity : AppCompatActivity() {
    private lateinit var recyclerViewPodcasts: RecyclerView
    private lateinit var podcastAdapter: PodcastListAdapter
    private lateinit var recyclerViewPodcaster: RecyclerView
    private lateinit var podcasterAdapter: PodcasterListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_podcast)

        recyclerViewPodcasts = findViewById(R.id.recyclerViewPodcasts)
        recyclerViewPodcasts.layoutManager = LinearLayoutManager(this)
        podcastAdapter = PodcastListAdapter(emptyList()) { selectedPodcast ->
            Log.d("PodcastListAdapter", "Selected Podcast ID: ${selectedPodcast.id}") // Log ID

            val intent = Intent(this, DetailPodcastActivity::class.java).apply {
                putExtra("PODCAST_ID", selectedPodcast.id)
                putExtra("PODCAST_TITLE", selectedPodcast.title)
                putExtra("PODCAST_IMAGE", selectedPodcast.art)
                putExtra("PODCAST_DESCRIPTION", selectedPodcast.description)
                putExtra("PODCAST_COUNT", selectedPodcast.episodes)
            }
            startActivity(intent)
        }
        recyclerViewPodcasts.adapter = podcastAdapter
        recyclerViewPodcaster = findViewById(R.id.recyclerViewNamePodcaster)
        recyclerViewPodcaster.layoutManager = LinearLayoutManager(this)
        podcasterAdapter = PodcasterListAdapter(emptyList())
        recyclerViewPodcaster.adapter = podcasterAdapter

        fetchPodcastData()
    }

    private fun fetchPodcastData() {
        val apiService = ApiConfig.getApiService()
        apiService.getPodcast().enqueue(object : Callback<List<ResponsePodcast>> {
            override fun onResponse(call: Call<List<ResponsePodcast>>, response: Response<List<ResponsePodcast>>) {
                if (response.isSuccessful) {
                    val podcastList = response.body() ?: emptyList()
                    podcastAdapter.updateData(podcastList)

                    val podcasterList = response.body() ?: emptyList()
                    podcasterAdapter.updateData(podcasterList)
                }
            }

            override fun onFailure(call: Call<List<ResponsePodcast>>, t: Throwable) {
                Log.e("PodcastActivity", "Error fetching podcasts: ${t.message}")
            }
        })
    }

    private fun sendPodcastIdToApi(podcastId: String) {
        val apiService = ApiConfig.getApiService()
        apiService.getEpisodes(podcastId).enqueue(object : Callback<List<ResponseEpisodes>> {

            override fun onResponse(
                call: Call<List<ResponseEpisodes>>,
                response: Response<List<ResponseEpisodes>>
            ) {
                if (response.isSuccessful) {
                    Log.d("PodcastActivity", "ID $podcastId berhasil dikirim ke API")
                } else {
                    Log.e("PodcastActivity", "Gagal mengirim ID: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<ResponseEpisodes>>, t: Throwable) {
                Log.e("PodcastActivity", "Error mengirim ID: ${t.message}")
            }
        })
    }
}
