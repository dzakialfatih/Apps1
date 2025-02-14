package com.example.apps1

import android.animation.ObjectAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apps1.Activity.BaseActivity
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
import com.google.android.material.navigation.NavigationView
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

class MainActivity : BaseActivity() {
    override fun getLayoutResource(): Int {
        return R.layout.activity_main
    }

    private lateinit var exoPlayer: ExoPlayer
    private var isPrepared: Boolean = false
    private var isPlaying: Boolean = false
    private lateinit var progressBar: ProgressBar
    private var rotateAnimation: ObjectAnimator? = null

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var recyclerViewPodcasts: RecyclerView
    private lateinit var podcastAdapter: PodcastListAdapter
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]

        // Inisialisasi DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout)
        val actionMenu = findViewById<ImageView>(R.id.action_menu)
        val navigationView = findViewById<NavigationView>(R.id.navigation_view)

        // Memastikan ikon tetap berwarna asli
        navigationView.itemIconTintList = null

        // Buka Sidebar saat Menu Diklik
        actionMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Menangani klik pada menu sidebar
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_youtube -> openUrl("https://www.youtube.com/@PramborsRadioOfficial")
                R.id.nav_instagram -> openUrl("https://www.instagram.com/prambors/")
                R.id.nav_x -> openUrl("https://x.com/Prambors")
                R.id.nav_facebook -> openUrl("https://www.facebook.com/prambors")
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        try {
            // ProgressBar untuk loading radio
            progressBar = findViewById(R.id.radioProgressBar)
            progressBar.visibility = View.VISIBLE

            // Preload radio saat aplikasi diluncurkan
            preloadRadio("https://s1.cloudmu.id/listen/prambors_fm/radio.mp3")

            // Inisialisasi tombol Play/Pause untuk radio
//            val radioButton: ImageButton = findViewById(R.id.radioButton)
//            radioButton.setOnClickListener {
//                if (isPlaying) {
//                    stopRadio()
//                } else if (isPrepared) {
//                    startRadio()
//                } else {
//                    Toast.makeText(this, "Radio is not ready yet. Please wait.", Toast.LENGTH_SHORT).show()
//                }
//            }

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
                        if (songHistoryItems.isNotEmpty()) {
                            sharedViewModel.updateSongTitle(songHistoryItems[0].song.title) // Ambil lagu pertama
                        }
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

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
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
     fun startRadio() {
        try {
            val radioButton: ImageButton = findViewById(R.id.radioButton) // Ambil ImageButton

            if (isPrepared) {
                exoPlayer.play()
                isPlaying = true

                // Buat animasi rotasi hanya jika belum dibuat
                if (rotateAnimation == null) {
                    rotateAnimation = ObjectAnimator.ofFloat(radioButton, View.ROTATION, 0f, 360f)
                    rotateAnimation?.duration = 3000 // 1 detik per putaran
                    rotateAnimation?.repeatCount = ObjectAnimator.INFINITE // Ulang terus-menerus
                    rotateAnimation?.interpolator = LinearInterpolator() // Putaran mulus
                    rotateAnimation?.start()
                }

                Toast.makeText(this, "Radio Diputar", Toast.LENGTH_SHORT).show()
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
    fun stopRadio() {
        try {
            exoPlayer.pause()  // Hentikan pemutaran
            isPlaying = false

            val radioButton: ImageButton = findViewById(R.id.radioButton)
            // Hentikan animasi dengan membatalkan semua animasi yang berjalan
            rotateAnimation?.cancel()
            rotateAnimation = null // Reset agar bisa diputar ulang nanti
            radioButton.clearAnimation() // (Opsional, untuk berjaga-jaga)

            Toast.makeText(this, "Radio Berhenti", Toast.LENGTH_SHORT).show()

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
