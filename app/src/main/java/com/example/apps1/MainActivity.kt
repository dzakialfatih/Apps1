package com.example.apps1

import android.content.Intent
import android.media.MediaPlayer
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
import com.example.apps1.Activity.PodcastActivity
import com.example.apps1.Adapter.PodcastListAdapter
import com.example.apps1.Adapter.RecentSongAdapter

class MainActivity : AppCompatActivity() {
    data class Song(val title: String, val artist: String)
    data class Podcast(val title: String, val description: String, val name: String)
    data class Podcaster(val title: String, val description: String)


    private var mediaPlayer: MediaPlayer? = null
    private var isPrepared: Boolean = false
    private var isPlaying: Boolean = false
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            progressBar = findViewById(R.id.radioProgressBar)

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
            val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
            recyclerView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL , false)
            recyclerView.adapter = RecentSongAdapter(getDummyDataSong())

            // Inisialisasi RecyclerView untuk daftar podcast
            val recyclerViewNamePodcast: RecyclerView = findViewById(R.id.recyclerViewNamePodcast)
            recyclerViewNamePodcast.layoutManager = LinearLayoutManager(this)
            recyclerViewNamePodcast.adapter = PodcastListAdapter(getDummyData())
        } catch (e: Exception) {
            Log.e("MainActivity", "Error initializing views: ${e.message}")
        }
    }

    // Fungsi untuk preload radio
    private fun preloadRadio(url: String) {
        try {
            progressBar.visibility = View.VISIBLE
            mediaPlayer = MediaPlayer().apply {
                setDataSource(url)
                setOnPreparedListener {
                    isPrepared = true
                    progressBar.visibility = View.GONE
                    Log.d("MainActivity", "Radio is preloaded and ready to play.")
                }
                setOnErrorListener { _, what, extra ->
                    isPrepared = false
                    progressBar.visibility = View.GONE
                    Log.e("MainActivity", "Error during playback: $what, $extra")
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            progressBar.visibility = View.GONE
            Log.e("MainActivity", "Error preloading radio: ${e.message}")
        }
    }

    // Mulai pemutaran radio
    private fun startRadio() {
        try {
            mediaPlayer?.let {
                if (isPrepared && !isPlaying) {
                    it.start()
                    isPlaying = true
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error starting radio: ${e.message}")
        }
    }

    // Hentikan pemutaran radio
    private fun stopRadio() {
        try {
            mediaPlayer?.let {
                if (isPlaying) {
                    it.pause()
                    isPlaying = false
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error stopping radio: ${e.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAndReleaseRadio()
    }

    // Hentikan dan lepaskan MediaPlayer
    private fun stopAndReleaseRadio() {
        try {
            mediaPlayer?.release()
            mediaPlayer = null
            isPlaying = false
            isPrepared = false
        } catch (e: Exception) {
            Log.e("MainActivity", "Error releasing MediaPlayer: ${e.message}")
        }
    }

    // Data dummy untuk lagu
    private fun getDummyDataSong(): List<Song> {
        return listOf(
            Song("Song 1", "Artist 1"),
            Song("Song 2", "Artist 2"),
            Song("Song 3", "Artist 3"),
            Song("Song 4", "Artist 4"),
            Song("Song 5", "Artist 5")
        )
    }

    // Data dummy untuk podcast
    private fun getDummyData(): List<Podcast> {
        return listOf(
            Podcast("Judul Podcast 1", "Description 1", "Nama Podcaster 1"),
            Podcast("Judul Podcast 2", "Description 2", "Nama Podcaster 2"),
            Podcast("Judul Podcast 3", "Description 3", "Nama Podcaster 3"),
            Podcast("Judul Podcast 4", "Description 4", "Nama Podcaster 4"),
            Podcast("Judul Podcast 5", "Description 5", "Nama Podcaster 5")
        )
    }
}
