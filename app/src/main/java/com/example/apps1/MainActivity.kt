package com.example.apps1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apps1.Activity.DetailPodcastActivity
import com.example.apps1.Activity.PodcastActivity
import com.example.apps1.Adapter.PodcastListAdapter
import com.example.apps1.Adapter.RecentSongAdapter
import com.example.apps1.response.ResponsePodcast
import com.example.apps1.response.SongHistoryItem
import com.example.apps1.retrofit.ApiConfig
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    data class Podcast(val title: String, val description: String, val name: String)
    data class Podcaster(val title: String, val description: String)
    data class Song(val title: String, val artist: String)


    private lateinit var exoPlayer: ExoPlayer
    private var isPrepared: Boolean = false
    private var isPlaying: Boolean = false
    private lateinit var progressBar: ProgressBar

    private lateinit var recyclerViewPodcasts: RecyclerView
    private lateinit var podcastAdapter: PodcastListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            // ProgressBar untuk loading radio
            progressBar = findViewById(R.id.radioProgressBar)
            progressBar.visibility = View.VISIBLE

            // Preload radio saat aplikasi diluncurkan
            preloadRadio("https://s1.cloudmu.id/listen/prambors_fm/radio.mp3")

            // Inisialisasi tombol Play/Pause untuk radio
            val radioButton: ImageButton = findViewById(R.id.radioButton)
            radioButton.setOnClickListener {
                if (isPlaying) {
                    stopRadio()
                    Toast.makeText(this, "Radio Stopped", Toast.LENGTH_SHORT).show()
                } else if (isPrepared) {
                    startRadio()
                    Toast.makeText(this, "Radio Playing", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Radio is not ready yet. Please wait.", Toast.LENGTH_SHORT).show()
                }
            }

            // Inisialisasi tombol untuk podcast
            val buttonPodcast: Button = findViewById(R.id.buttonPodcast)
            buttonPodcast.setOnClickListener {
                val intent = Intent(this, PodcastActivity::class.java)
                startActivity(intent)
            }

            // Inisialisasi RecyclerView untuk daftar lagu
            val recyclerViewSong: RecyclerView = findViewById(R.id.recyclerView)
            recyclerViewSong.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

            // Fetch data dari API
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val todayDate = dateFormat.format(Date()) // Dapatkan tanggal hari ini
            val apiService = ApiConfig.getApiService()
            apiService.getSongs(todayDate).enqueue(object : Callback<List<SongHistoryItem>> {
                override fun onResponse(
                    call: Call<List<SongHistoryItem>>,
                    response: Response<List<SongHistoryItem>>
                ) {
                    if (response.isSuccessful) {
                        val songHistoryItems = response.body() ?: emptyList()
                        val limitedSongs = songHistoryItems.take(5) // Ambil hanya 5 item pertama

                        // Inisialisasi RecyclerView dan Adapter
                        val adapter = RecentSongAdapter(limitedSongs)
                        recyclerViewSong.adapter = adapter
                        recyclerViewSong.layoutManager = LinearLayoutManager(this@MainActivity)
                    }
                }

                override fun onFailure(call: Call<List<SongHistoryItem>>, t: Throwable) {
                    // Tangani kesalahan jaringan
                    Log.e("API_ERROR", t.message.toString())
                }
            })

            // Inisialisasi RecyclerView
            recyclerViewPodcasts = findViewById(R.id.recyclerViewNamePodcast)
            recyclerViewPodcasts.layoutManager = LinearLayoutManager(this)
            // Inisialisasi Adapter
            podcastAdapter = PodcastListAdapter(emptyList()) { selectedPodcast ->
                val intent = Intent(this, DetailPodcastActivity::class.java)
                intent.putExtra("PODCAST_TITLE", selectedPodcast.title)
                intent.putExtra("PODCAST_IMAGE", selectedPodcast.art)
                intent.putExtra("PODCAST_DESCRIPTION", selectedPodcast.description)
                intent.putExtra("PODCAST_COUNT", selectedPodcast.episodes)

                startActivity(intent)
            }
            recyclerViewPodcasts.adapter = podcastAdapter

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
                    Log.e("API_ERROR", t.message.toString())
                }

            })

            progressBar.visibility = View.GONE // Sembunyikan progressBar jika semua berhasil
        } catch (e: Exception) {
            Log.e("MainActivity", "Error initializing views: ${e.message}")
        }
    }

    // Fungsi untuk preload radio
    private fun preloadRadio(url: String) {
        try {
            progressBar.visibility = View.VISIBLE

            // Inisialisasi ExoPlayer
            exoPlayer = ExoPlayer.Builder(this).build().apply {
                val mediaItem = MediaItem.fromUri(url)
                setMediaItem(mediaItem)

                // Listener untuk mengetahui status siap
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_READY -> {
                                isPrepared = true
                                progressBar.visibility = View.GONE
                                Log.d("MainActivity", "Radio is preloaded and ready to play.")
                            }
                            Player.STATE_ENDED -> Log.d("MainActivity", "Playback ended.")
                            Player.STATE_BUFFERING -> Log.d("MainActivity", "Buffering...")
                            Player.STATE_IDLE -> Log.d("MainActivity", "Player is idle.")
                        }
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        Log.e("MainActivity", "Error during playback: ${error.message}")
                    }
                })
                prepare() // Siapkan media
            }
        } catch (e: Exception) {
            progressBar.visibility = View.GONE
            Log.e("MainActivity", "Error preloading radio: ${e.message}")
        }
    }

    // Mulai pemutaran radio
    private fun startRadio() {
        try {
            if (isPrepared) {
                exoPlayer.play()
                isPlaying = true
                Log.d("MainActivity", "Radio started playing.")
                Log.d("MainActivity", "Radio started.")
            } else {
                Log.d("MainActivity", "Radio is not ready yet.")
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error starting radio: ${e.message}")
        }
    }


    // Hentikan pemutaran radio
    private fun stopRadio() {
        try {
            exoPlayer.pause()  // Hentikan pemutaran
            isPlaying = false
            Log.d("MainActivity", "Radio stopped and player released.")
        } catch (e: Exception) {
            Log.e("MainActivity", "Error stopping radio: ${e.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()  // Lepaskan ExoPlayer

    }

}
