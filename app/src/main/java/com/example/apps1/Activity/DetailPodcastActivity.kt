package com.example.apps1.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.apps1.Adapter.EpisodesListAdapter
import com.example.apps1.HeaderLoader
import com.example.apps1.R
import com.example.apps1.response.ResponseEpisodes
import com.example.apps1.response.ResponsePodcast
import com.example.apps1.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * DetailPodcastActivity - Modified untuk integrasi dengan NamePodcastActivity
 *
 * Menerima data podcast dari Parcelable atau Intent extras
 * Fetch episodes dari API menggunakan podcast ID
 * Navigate ke DetailEpisodeActivity saat item episode diklik
 */
class DetailPodcastActivity : AppCompatActivity() {

    private lateinit var imgItemPhoto: ImageView
    private lateinit var imageViewBack: ImageView
    private lateinit var tvItemName: TextView
    private lateinit var tvItemTotal: TextView
    private lateinit var tvItemCount: TextView
    private lateinit var tvItemAuthor: TextView

    private lateinit var recyclerViewEpisodes: RecyclerView
    private lateinit var episodesAdapter: EpisodesListAdapter

    private var podcast: ResponsePodcast? = null
    private var podcastId: String? = null
    private var podcastCount: String? = null

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_podcast)
        enableEdgeToEdge()

        val mainView = findViewById<View>(R.id.main)
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        // Initialize Views
        imgItemPhoto = findViewById(R.id.img_item_photo)
        tvItemName = findViewById(R.id.tv_item_name)
        tvItemTotal = findViewById(R.id.tv_item_total)
        tvItemCount = findViewById(R.id.tv_item_count)
        imageViewBack = findViewById(R.id.imageView4)

        // Initialize RecyclerView
        recyclerViewEpisodes = findViewById(R.id.recyclerViewEpisodes)
        recyclerViewEpisodes.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // Setup Episodes Adapter
        setupEpisodesAdapter()

        // Get data dari Intent (multiple sources support)
        intent?.let { intent ->
            // Method 1: Dari Parcelable (recommended)
            podcast = intent.getParcelableExtra<ResponsePodcast>("podcast_data")

            // Method 2: Dari extras jika belum ada Parcelable
            if (podcast == null) {
                podcastId = intent.getStringExtra("PODCAST_ID")
                val title = intent.getStringExtra("PODCAST_TITLE")
                val imageUrl = intent.getStringExtra("PODCAST_IMAGE")
                val description = intent.getStringExtra("PODCAST_DESCRIPTION")
                val count = intent.getIntExtra("PODCAST_COUNT", 0)

                podcastCount = count.toString()

                Log.d("DetailPodcastActivity", "Data dari Intent extras")
                Log.d("DetailPodcastActivity", "Podcast ID: $podcastId")

                // Set UI dari extras
                tvItemName.text = title ?: "Unknown Podcast"
                tvItemTotal.text = description ?: ""
                tvItemCount.text = "Total Episodes: $count"

                if (imageUrl != null) {
                    Glide.with(this)
                        .load(HeaderLoader.getUrlWithHeaders(imageUrl))
                        .error(R.drawable.logo_circle_medium)
                        .into(imgItemPhoto)
                }
            } else {
                // Set UI dari Parcelable
                setupUIFromParcelable()
            }
        }

        // Fetch episodes dari API
        podcastId = podcastId ?: podcast?.id?.toString()
        podcastCount = podcastCount ?: podcast?.episodes?.toString()

        if (podcastId != null) {
            Log.d("DetailPodcastActivity", "Fetching episodes for podcast: $podcastId")
            fetchEpisodesData(podcastId!!)
        } else {
            Log.e("DetailPodcastActivity", "Podcast ID is null - cannot fetch episodes")
        }

        // Back button listener
        imageViewBack.setOnClickListener {
            Log.d("DetailPodcastActivity", "Back button clicked")
            onBackPressed()
        }
    }

    /**
     * Setup UI menggunakan Parcelable ResponsePodcast data
     */
    private fun setupUIFromParcelable() {
        podcast?.let { p ->
            Log.d("DetailPodcastActivity", "Setting UI from Parcelable - Podcast: ${p.title}")

            tvItemName.text = p.title ?: p.author ?: "Unknown Podcast"
            tvItemTotal.text = p.description ?: p.descriptionShort ?: "No description"
            tvItemCount.text = "Total Episodes: ${p.episodes ?: 0}"

            // Try load image with HeaderLoader, fallback to direct URL
            val imageUrl = p.art
            if (imageUrl != null) {
                Glide.with(this)
                    .load(HeaderLoader.getUrlWithHeaders(imageUrl))
                    .error(R.drawable.logo_circle_medium)
                    .into(imgItemPhoto)
            }

            // Set author if available
            if (::tvItemAuthor.isInitialized) {
                tvItemAuthor.text = p.author ?: "Unknown Author"
            }

            // Set podcast ID dan count untuk episodes navigation
            podcastId = p.id?.toString()
            podcastCount = p.episodes?.toString()
        }
    }

    /**
     * Setup EpisodesListAdapter dengan click handler
     */
    private fun setupEpisodesAdapter() {
        episodesAdapter = EpisodesListAdapter(emptyList()) { episode ->
            handleEpisodeClick(episode)
        }
        recyclerViewEpisodes.adapter = episodesAdapter
    }

    /**
     * Handle episode item click
     * Navigate ke DetailEpisodeActivity
     */
    private fun handleEpisodeClick(episode: ResponseEpisodes) {
        Log.d("DetailPodcastActivity", "Episode clicked - ID: ${episode.id}")

        if (episode.id != null && podcastId != null) {
            Log.d("episodeId", "Episode ID: ${episode.id}")
            Log.d("podcastId", "Podcast ID: $podcastId")
            Log.d("episodeCount", "From Podcast: $podcastCount")

            val intent = Intent(this, DetailEpisodeActivity::class.java).apply {
                putExtra("EPISODE_ID", episode.id?.toString())
                putExtra("PODCAST_ID", podcastId)
                putExtra("PODCAST_COUNT", podcastCount)
            }
            startActivity(intent)
        } else {
            Log.e("DetailPodcastActivity", "Episode ID or Podcast ID is null")
        }
    }

    /**
     * Fetch episodes dari API
     * Gunakan podcast ID untuk get list episodes
     */
    private fun fetchEpisodesData(podcastId: String) {
        Log.d("DetailPodcastActivity", "Starting API call for episodes - Podcast ID: $podcastId")

        val apiService = ApiConfig.getApiService()

        apiService.getEpisodes(podcastId).enqueue(object : Callback<List<ResponseEpisodes>> {
            override fun onResponse(
                call: Call<List<ResponseEpisodes>>,
                response: Response<List<ResponseEpisodes>>
            ) {
                if (response.isSuccessful) {
                    val episodesList = response.body() ?: emptyList()
                    Log.d("DetailPodcastActivity", "Episodes fetched successfully: ${episodesList.size} episodes")
                    episodesAdapter.updateData(episodesList)
                } else {
                    Log.e("DetailPodcastActivity", "API Error: ${response.code()} - ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<ResponseEpisodes>>, t: Throwable) {
                Log.e("DetailPodcastActivity", "Network Error: ${t.message}", t)
            }
        })
    }
}