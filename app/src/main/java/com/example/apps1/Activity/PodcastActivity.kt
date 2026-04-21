package com.example.apps1.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apps1.Adapter.PodcastListAdapter
import com.example.apps1.Adapter.PodcasterListAdapter
import com.example.apps1.databinding.ActivityPodcastBinding
import com.example.apps1.response.ResponsePodcast
import com.example.apps1.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PodcastActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPodcastBinding

    private lateinit var podcastAdapter: PodcastListAdapter
    private lateinit var podcasterAdapter: PodcasterListAdapter

    // switch dummy
    private val useDummyData = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPodcastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        if (useDummyData) {
            loadDummy()
        } else {
            fetchPodcastData()
        }

        binding.imageView4.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {

        podcastAdapter = PodcastListAdapter(emptyList()) { openDetail(it) }
        podcasterAdapter = PodcasterListAdapter(emptyList()) { openDetail(it) }

        binding.recyclerViewPodcasts.apply {
            layoutManager =
                LinearLayoutManager(this@PodcastActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = podcastAdapter
            isNestedScrollingEnabled = false
        }

        binding.recyclerViewNamePodcaster.apply {
            layoutManager = GridLayoutManager(this@PodcastActivity, 2)
            adapter = podcasterAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun fetchPodcastData() {
        val apiService = ApiConfig.getApiService()

        apiService.getPodcast().enqueue(object : Callback<List<ResponsePodcast>> {

            override fun onResponse(
                call: Call<List<ResponsePodcast>>,
                response: Response<List<ResponsePodcast>>
            ) {
                if (response.isSuccessful) {
                    val podcastList = response.body() ?: emptyList()

                    podcastAdapter.updateData(podcastList)
                    podcasterAdapter.updateData(podcastList)

                } else {
                    Log.e("PodcastActivity", "Response gagal → pakai dummy")
                    loadDummy()
                }
            }

            override fun onFailure(call: Call<List<ResponsePodcast>>, t: Throwable) {
                Log.e("PodcastActivity", "Error → pakai dummy ${t.message}")
                loadDummy()
            }
        })
    }

    private fun loadDummy() {
        val dummyPodcasts = listOf(
            ResponsePodcast(
                id = 1,
                title = "Prambors Podcast",
                author = "Prambors Radio",
                description = "Podcast hiburan musik dan lifestyle.",
                art = "https://picsum.photos/300?1",
                url = ""
            ),
            ResponsePodcast(
                id = 2,
                title = "Close The Door",
                author = "Deddy Corbuzier",
                description = "Podcast self development.",
                art = "https://picsum.photos/300?2",
                url = ""
            ),
            ResponsePodcast(
                id = 3,
                title = "Temu Kangen",
                author = "Various Host",
                description = "Podcast obrolan santai.",
                art = "https://picsum.photos/300?3",
                url = ""
            ),
            ResponsePodcast(
                id = 4,
                title = "Rintik Sedu",
                author = "Nadhifa Allya Tsana",
                description = "Podcast motivasi.",
                art = "https://picsum.photos/300?4",
                url = ""
            )
        )

        podcastAdapter.updateData(dummyPodcasts)
        podcasterAdapter.updateData(dummyPodcasts)
    }

    private fun openDetail(selectedPodcast: ResponsePodcast) {
        val intent = Intent(this, DetailPodcastActivity::class.java).apply {
            putExtra("PODCAST_ID", selectedPodcast.id)
            putExtra("PODCAST_TITLE", selectedPodcast.author)
            putExtra("PODCAST_IMAGE", selectedPodcast.art)
            putExtra("PODCAST_DESCRIPTION", selectedPodcast.description)
            putExtra("PODCAST_COUNT", selectedPodcast.episodes)
        }
        startActivity(intent)
    }
}