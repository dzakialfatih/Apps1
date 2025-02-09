package com.example.apps1.Activity

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.apps1.BuildConfig
import android.view.animation.LinearInterpolator
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.apps1.Adapter.EpisodesListAdapter
import com.example.apps1.Adapter.MediaListAdapter
import com.example.apps1.Adapter.RecentSongAdapter
import com.example.apps1.CircleTransform
import com.example.apps1.HeaderLoader
import com.example.apps1.R
import com.example.apps1.databinding.ActivityPodcastEpisodesBinding
import com.example.apps1.response.ResponseEpisodes
import com.example.apps1.response.ResponseMedia
import com.example.apps1.response.ResponsePodcast
import com.example.apps1.response.SongHistoryItem
import com.example.apps1.retrofit.ApiConfig
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailEpisodeActivity : AppCompatActivity() {

    private lateinit var exoPlayer: ExoPlayer
    private var isPrepared: Boolean = false
    private var isPlaying: Boolean = false
    private var link: String = ""


    private lateinit var progressBar: ProgressBar

    private lateinit var recyclerViewEpisodes: RecyclerView
    private lateinit var episodesAdapter: EpisodesListAdapter
    private var rotateAnimation: ObjectAnimator? = null

    private lateinit var binding: ActivityPodcastEpisodesBinding

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPodcastEpisodesBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        Log.d("ViewCheck", "ImageView: ${binding.imgItemPhoto}")
//        Log.d("ViewCheck", "TextView Name: ${binding.tvItemName}")
//        Log.d("ViewCheck", "TextView Description: ${binding.tvItemTotal}")
//        Log.d("ViewCheck", "TextView Total: ${binding.tvItemCount}")
//        Log.d("ViewCheck", "ImageView Back: ${binding.imageView4}")

        val podcastId: String? = intent?.getStringExtra("PODCAST_ID")
        val episodeId: String? = intent?.getStringExtra("EPISODE_ID")
        val episodes: String? = intent?.getStringExtra("PODCAST_COUNT")


        binding.tvItemCount.text = "Total Episodes : $episodes "

        progressBar = binding.progressBar

//        Log.d("ViewCheck", "podcast Id : ${podcastId}")
//        Log.d("ViewCheck", "episode id: ${episodeId}")


        val apiService = ApiConfig.getApiService()
        if (podcastId != null && episodeId != null) {
            apiService.getDetailEpisode(podcastId, episodeId).enqueue(object : Callback<ResponseMedia> {

                override fun onResponse(
                    call: Call<ResponseMedia>,
                    response: Response<ResponseMedia>
                ) {
                    if (response.isSuccessful) {
                        try {
                            binding.tvItemName.text = response.body()?.title ?: "Tidak ada judul"
                            binding.tvItemTotal.text = response.body()?.description ?: "Tidak ada deskripsi"
                            link = response.body()?.links!!.media
                            Log.d("link", "link: $link")
                            preloadRadio(link, BuildConfig.KEY)
                            Glide.with(this@DetailEpisodeActivity)
                                .load(HeaderLoader.getUrlWithHeaders(response.body()!!.art))
                                .error(R.drawable.logo_circle_medium)
                                .transform(CircleTransform())
                                .into(binding.imgItemPhoto)

                        } catch (e: Exception) {
                            Log.e("ParsingError", "Error parsing response", e)
                        }
                    } else {
                        Log.e("Response", "Response failed with code: ${response.code()}")
                    }                }

                override fun onFailure(call: Call<ResponseMedia>, t: Throwable) {
                    Log.e("API Error", "Error fetching episode details", t)
                }
            })


            //Inisialisasi tombol Play/Pause untuk podcast
            val playButton: ImageButton = findViewById(R.id.imageButton)
            playButton.setOnClickListener {
                Log.d("MainActivity", "Button clicked - isPlaying: $isPlaying, isPrepared: $isPrepared")

                if (isPlaying) {
                    stopPodcast()
                } else if (isPrepared) {
                    startPodcast()
                } else {
                    Toast.makeText(this, "Podcast is not ready yet. Please wait.", Toast.LENGTH_SHORT).show()
                }
            }

        }

        progressBar.visibility = View.GONE // Sembunyikan progressBar jika semua berhasil


        // Inisialisasi RecyclerView dan Adapter
        recyclerViewEpisodes = findViewById(R.id.recyclerViewEpisodes)
        recyclerViewEpisodes.layoutManager =  LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        episodesAdapter = EpisodesListAdapter(emptyList())
        { episode ->
            if (episode.id != null && podcastId != null) {
//                Log.d("episodeId", "episodeId: ${episode.id}")
//                Log.d("podcastId", "podcastId: $podcastId")

                val intent = Intent(this, DetailEpisodeActivity::class.java).apply {
                    putExtra("EPISODE_ID", episode.id)
                    putExtra("PODCAST_ID", podcastId)
                    putExtra("PODCAST_COUNT", episodes)

                }
                startActivity(intent)
            } else {
                Log.e("EpisodesAdapter", "Episode ID or Podcast ID is null")
            }
        }
        recyclerViewEpisodes.adapter = episodesAdapter

        if (podcastId != null) {
            fetchEpisodesData(podcastId)
        }

        // Tombol kembali ke halaman sebelumnya
        binding.imageView4.setOnClickListener { onBackPressed() }
    }

    private fun preloadRadio(url: String, apiToken: String) {
        try {
            if (url.isEmpty()) {
                Log.e("ExoPlayer", "Error: URL kosong")
                return
            }

            Log.d("ExoPlayer", "Loading URL: $url")
            progressBar.visibility = View.VISIBLE

            // 1️⃣ Inisialisasi ExoPlayer
            exoPlayer = ExoPlayer.Builder(this).build()

            // 2️⃣ Tambahkan Headers untuk Token API
            val dataSourceFactory = DefaultHttpDataSource.Factory().setDefaultRequestProperties(
                mapOf("Authorization" to "Bearer $apiToken")
            )

            // 3️⃣ Buat MediaItem dengan DataSource yang sudah dikonfigurasi
            val mediaItem = MediaItem.Builder()
                .setUri(url)
                .setMediaMetadata(MediaMetadata.Builder().setTitle("Podcast").build())
                .build()

            // 4️⃣ Pasang MediaSource dengan DataSource yang sudah memiliki token
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaItem)

            // 5️⃣ Masukkan MediaSource ke ExoPlayer dan Persiapkan
            exoPlayer.setMediaSource(mediaSource)
            exoPlayer.prepare()

            // 6️⃣ Tambahkan Listener untuk Mengecek Status
            exoPlayer.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_READY -> {
                            isPrepared = true
                            progressBar.visibility = View.GONE
                            Log.d("ExoPlayer", "Radio siap diputar.")
                        }
                        Player.STATE_ENDED -> Log.d("ExoPlayer", "Playback selesai.")
                        Player.STATE_BUFFERING -> Log.d("ExoPlayer", "Buffering...")
                        Player.STATE_IDLE -> Log.e("ExoPlayer", "Player dalam keadaan IDLE!")
                    }
                }
            })

        } catch (e: Exception) {
            Log.e("ExoPlayer", "Error memuat radio: ${e.message}")
        }
    }



    private fun fetchEpisodesData(podcastId: String) {
        val apiService = ApiConfig.getApiService()
        apiService.getEpisodes(podcastId).enqueue(object : Callback<List<ResponseEpisodes>> {
            override fun onResponse(call: Call<List<ResponseEpisodes>>, response: Response<List<ResponseEpisodes>>) {
                if (response.isSuccessful) {
                    val episodesList = response.body() ?: emptyList()
                    episodesAdapter?.updateData(episodesList) // Perbarui data di RecyclerView
                } else {
                    Log.e("DetailPodcastActivity", "Gagal mendapatkan episode: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<ResponseEpisodes>>, t: Throwable) {
                Log.e("DetailPodcastActivity", "Error mengambil episode: ${t.message}")
            }
        })
    }
    fun startPodcast() {
        try {
            val playButton: ImageButton = findViewById(R.id.imageButton)

            if (isPrepared) {
                exoPlayer.play()
                isPlaying = true
                val image = binding.imgItemPhoto
                playButton.setImageResource(R.drawable.ic_pause)
                // Buat animasi rotasi hanya jika belum dibuat
                if (rotateAnimation == null) {
                    rotateAnimation = ObjectAnimator.ofFloat(image, View.ROTATION, 0f, 360f)
                    rotateAnimation?.duration = 3000 // 1 detik per putaran
                    rotateAnimation?.repeatCount = ObjectAnimator.INFINITE // Ulang terus-menerus
                    rotateAnimation?.interpolator = LinearInterpolator() // Putaran mulus
                    rotateAnimation?.start()
                }
                Toast.makeText(this, "Podcast Playing", Toast.LENGTH_SHORT).show()
                Log.d("MainActivity", "Podcast started playing.")
            } else {
                Log.d("MainActivity", "Podcast is not ready yet.")
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error starting Podcast: ${e.message}")
        }
    }

    fun stopPodcast() {
        val playButton: ImageButton = findViewById(R.id.imageButton)

        try {
            exoPlayer.pause()  // Hentikan pemutaran
            isPlaying = false
            playButton.setImageResource(R.drawable.play_icon_foreground)
            rotateAnimation?.cancel()
            rotateAnimation = null // Reset agar bisa diputar ulang nanti
            binding.imgItemPhoto.clearAnimation() // (Opsional, untuk berjaga-jaga)

            Toast.makeText(this, "Podcast Stopped", Toast.LENGTH_SHORT).show()

            Log.d("MainActivity", "Podcast stopped and player released.")
        } catch (e: Exception) {
            Log.e("MainActivity", "Podcast stopping error: ${e.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()  // Lepaskan ExoPlayer

    }

  }