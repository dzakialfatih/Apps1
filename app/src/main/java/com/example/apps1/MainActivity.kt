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
import com.example.apps1.response.Song
import com.example.apps1.response.SongHistoryItem
import com.example.apps1.retrofit.ApiConfig
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : BaseActivity() {
    override fun getLayoutResource(): Int {
        return R.layout.activity_main
    }

    private val useDummyData = true
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

            // Tombol untuk buka halaman podcast
            val buttonPodcast: Button = findViewById(R.id.buttonPodcast)
            buttonPodcast.setOnClickListener {
                val intent = Intent(this, PodcastActivity::class.java)
                startActivity(intent)
            }

            // ================
            // 🎵 RecyclerView Lagu
            // ================
            val recyclerViewSong: RecyclerView = findViewById(R.id.recyclerView)
            recyclerViewSong.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

            if (useDummyData) {
                // ✅ Dummy data lagu
                sharedViewModel.updateSongTitle(dummySongs[0].song.title)
                val adapter = RecentSongAdapter(dummySongs.take(5))
                recyclerViewSong.adapter = adapter
            } else {
                // ✅ API data lagu
                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val todayDate = dateFormat.format(Date())
                val apiService = ApiConfig.getApiService()
                apiService.getSongs(todayDate).enqueue(object : Callback<List<SongHistoryItem>> {
                    override fun onResponse(
                        call: Call<List<SongHistoryItem>>,
                        response: Response<List<SongHistoryItem>>
                    ) {
                        if (response.isSuccessful) {
                            val songHistoryItems = response.body() ?: emptyList()
                            if (songHistoryItems.isNotEmpty()) {
                                sharedViewModel.updateSongTitle(songHistoryItems[0].song.title)
                            }
                            val limitedSongs = songHistoryItems.take(5)
                            val adapter = RecentSongAdapter(limitedSongs)
                            recyclerViewSong.adapter = adapter
                        }
                    }

                    override fun onFailure(call: Call<List<SongHistoryItem>>, t: Throwable) {
                        Log.e("API_ERROR", t.message.toString())
                    }
                })
            }

            // ================
            // 🎙 RecyclerView Podcast
            // ================
            recyclerViewPodcasts = findViewById(R.id.recyclerViewNamePodcast)
            recyclerViewPodcasts.layoutManager = LinearLayoutManager(this)

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

            progressBar.visibility = View.GONE
        } catch (e: Exception) {
            Log.e("MainActivity", "Error initializing views: ${e.message}")
        }
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    // ==================
    // 🎧 Fungsi Radio
    // ==================
    private fun preloadRadio(url: String) {
        try {
            progressBar.visibility = View.VISIBLE
            exoPlayer = ExoPlayer.Builder(this).build().apply {
                val mediaItem = MediaItem.fromUri(url)
                setMediaItem(mediaItem)
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
                prepare()
            }
        } catch (e: Exception) {
            progressBar.visibility = View.GONE
            Log.e("MainActivity", "Error preloading radio: ${e.message}")
        }
    }

    fun startRadio() {
        try {
            val radioButton: ImageButton = findViewById(R.id.radioButton)
            if (isPrepared) {
                exoPlayer.play()
                isPlaying = true

                if (rotateAnimation == null) {
                    rotateAnimation = ObjectAnimator.ofFloat(radioButton, View.ROTATION, 0f, 360f)
                    rotateAnimation?.duration = 3000
                    rotateAnimation?.repeatCount = ObjectAnimator.INFINITE
                    rotateAnimation?.interpolator = LinearInterpolator()
                    rotateAnimation?.start()
                }

                Toast.makeText(this, "Radio Diputar", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("MainActivity", "Radio is not ready yet.")
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error starting radio: ${e.message}")
        }
    }

    fun stopRadio() {
        try {
            exoPlayer.pause()
            isPlaying = false
            val radioButton: ImageButton = findViewById(R.id.radioButton)
            rotateAnimation?.cancel()
            rotateAnimation = null
            radioButton.clearAnimation()
            Toast.makeText(this, "Radio Berhenti", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("MainActivity", "Error stopping radio: ${e.message}")
        }
    }

    // ==================
    // 🎵 Dummy Data
    // ==================
    private val dummySongs = listOf(
        SongHistoryItem(
            shId = 1,
            duration = 180,
            song = Song(
                art = "https://via.placeholder.com/150",
                artist = "Eminem",
                customFields = emptyList(),
                album = "Just Lose it",
                genre = "HipHop",
                isrc = "123456",
                id = "1",
                text = "Eminem - Lose Yourself",
                title = "Lose Yourself",
                lyrics = "..."
            ),
            isRequest = false,
            streamer = "",
            playlist = "Top Hits",
            playedAt = 1234567890
        ),
        SongHistoryItem(
            shId = 2,
            duration = 200,
            song = Song(
                art = "https://via.placeholder.com/150",
                artist = "Tuan Tigabelas",
                customFields = emptyList(),
                album = "wew",
                genre = "HipHop",
                isrc = "7891011",
                id = "2",
                text = "Tuan Tigabelas - WeW",
                title = "WeW",
                lyrics = "..."
            ),
            isRequest = false,
            streamer = "",
            playlist = "Top Hits",
            playedAt = 1234567891
        ),
        SongHistoryItem(
            shId = 3,
            duration = 180,
            song = Song(
                art = "https://via.placeholder.com/150",
                artist = "Taylor Swift",
                customFields = emptyList(),
                album = "1989",
                genre = "Pop",
                isrc = "123456",
                id = "3",
                text = "Taylor Swift - Blank Space",
                title = "Blank Space",
                lyrics = "So it's gonna be forever..."
            ),
            isRequest = false,
            streamer = "",
            playlist = "Top Hits",
            playedAt = 1234567890
        ),
        SongHistoryItem(
            shId = 4,
            duration = 200,
            song = Song(
                art = "https://via.placeholder.com/150",
                artist = "Ed Sheeran",
                customFields = emptyList(),
                album = "Divide",
                genre = "Pop",
                isrc = "7891011",
                id = "4",
                text = "Ed Sheeran - Shape of You",
                title = "Shape of You",
                lyrics = "The club isn't the best place..."
            ),
            isRequest = false,
            streamer = "",
            playlist = "Top Hits",
            playedAt = 1234567891
        )

    )

    private val dummyPodcasts = listOf(
        ResponsePodcast(
            id = 4,
            title = "Rintik Sedu",
            description = "Podcast bercerita tentang pengalaman hidup, motivasi, dan renungan sehari-hari yang menyentuh hati.",
            art = "https://via.placeholder.com/150?text=RintikSedu",
            url = "https://example.com/rintiksedu"
        ),
        ResponsePodcast(
            id = 3,
            title = "Temu Kangen",
            description = "Podcast yang menghadirkan cerita-cerita menarik dan candaan segar dari para pembawa acara profesional.",
            art = "https://via.placeholder.com/150?text=TemuKangen",
            url = "https://example.com/temukangen"
        )
    )

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }
}
