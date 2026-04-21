package com.example.apps1.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apps1.Adapter.PodcastListAdapter
import com.example.apps1.Adapter.PodcasterListAdapter
import com.example.apps1.R
import com.example.apps1.response.ResponsePodcast
import com.example.apps1.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NamePodcastActivity : AppCompatActivity() {

    private lateinit var recyclerViewNamePodcaster: RecyclerView
    private lateinit var searchBar: SearchView
    private lateinit var podcastAdapter: PodcasterListAdapter

    private var allPodcasts = mutableListOf<ResponsePodcast>()
    private val useDummyData = true

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_name_podcast)

        recyclerViewNamePodcaster = findViewById(R.id.recyclerViewNamePodcast)
        recyclerViewNamePodcaster.layoutManager = LinearLayoutManager(this)

        podcastAdapter = PodcasterListAdapter(emptyList()) { selectedPodcast ->
            val intent = Intent(this, DetailPodcastActivity::class.java).apply {
                putExtra("PODCAST_ID", selectedPodcast.id)
                putExtra("PODCAST_TITLE", selectedPodcast.author)
                putExtra("PODCAST_IMAGE", selectedPodcast.art)
                putExtra("PODCAST_DESCRIPTION", selectedPodcast.description)
                putExtra("PODCAST_COUNT", selectedPodcast.episodes)
            }
            startActivity(intent)
        }
        recyclerViewNamePodcaster.adapter = podcastAdapter
        searchBar = findViewById(R.id.searchBar)

        setupRecyclerView()

        if (useDummyData) {
            // ✅ Dummy data podcast
            podcastAdapter.updateData(dummyPodcasts)
        } else {
            // ✅ API data podcast
            val apiService = ApiConfig.getApiService()
            apiService.getPodcast().enqueue(object : Callback<List<ResponsePodcast>> {
                override fun onResponse(
                    call: Call<List<ResponsePodcast>>,
                    response: Response<List<ResponsePodcast>>
                ) {
                    if (response.isSuccessful) {
                        val podcastList = response.body() ?: emptyList()
                        podcastAdapter.updateData(podcastList)
                    }
                }

                override fun onFailure(call: Call<List<ResponsePodcast>>, t: Throwable) {
                    Log.e("API_ERROR", t.message.toString())
                }
            })
        }

        setupSearch()
    }

    private fun setupRecyclerView() {
        recyclerViewNamePodcaster.layoutManager = LinearLayoutManager(this)

        podcastAdapter = PodcasterListAdapter(mutableListOf()) { podcast ->
            Log.d("NamePodcastActivity", "Clicked: ${podcast.author}")
        }

        recyclerViewNamePodcaster.adapter = podcastAdapter
    }

    private fun setupSearch() {
        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                filterPodcasts(newText ?: "")
                return true
            }
        })
    }

    private fun filterPodcasts(query: String) {
        val filtered = if (query.isEmpty()) {
            allPodcasts
        } else {
            val lowerQuery = query.lowercase()
            allPodcasts.filter {
                it.author?.lowercase()?.contains(lowerQuery) == true ||
                        it.description?.lowercase()?.contains(lowerQuery) == true
            }
        }

        podcastAdapter.updateData(filtered)
    }

    private val dummyPodcasts = listOf(
        ResponsePodcast(
            id = 1,
            author = "Prambors Podcast",
            description = "Podcast hiburan musik dan lifestyle.",
            art = "https://picsum.photos/200?1",
            url = ""
        ),
        ResponsePodcast(
            id = 2,
            author = "Deddy Corbuzier",
            description = "Podcast self development.",
            art = "https://picsum.photos/200?2",
            url = ""
        ),
        ResponsePodcast(
            id = 3,
            author = "Temu Kangen",
            description = "Podcast obrolan santai.",
            art = "https://picsum.photos/200?3",
            url = ""
        ),
        ResponsePodcast(
            id = 4,
            author = "Rintik Sedu",
            description = "Podcast motivasi.",
            art = "https://picsum.photos/200?4",
            url = ""
        )
    )
}