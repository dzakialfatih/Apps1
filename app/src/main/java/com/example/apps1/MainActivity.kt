package com.example.apps1

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apps1.Adapter.PodcastListAdapter
import com.example.apps1.Adapter.RecentSongAdapter

class MainActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            // Preload radio
            preloadRadio("https://s1.cloudmu.id/listen/prambors_fm/radio.mp3")

            // Inisialisasi tombol ImageButton untuk Play/Pause
            val radioButton: ImageButton = findViewById(R.id.radioButton)
            radioButton.setOnClickListener {
                if (isPlaying) {
                    stopRadio()
                    Toast.makeText(this, "Radio Stopped", Toast.LENGTH_SHORT).show()
                } else {
                    startRadio()
                    Toast.makeText(this, "Radio Playing", Toast.LENGTH_SHORT).show()
                }
            }

            // Inisialisasi tombol Button untuk podcast
            val button2: Button = findViewById(R.id.buttonPodcast)
            button2.setOnClickListener {
                Toast.makeText(this, "Button 2 Clicked!", Toast.LENGTH_SHORT).show()
            }

            // Inisialisasi RecyclerView untuk menampilkan daftar lagu
            val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            recyclerView.adapter = RecentSongAdapter(getDummyDataSong())

            // Inisialisasi RecyclerView untuk menampilkan nama podcast
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
            mediaPlayer = MediaPlayer().apply {
                setDataSource(url)
                setOnPreparedListener {
                    Log.d("MainActivity", "Radio is preloaded and ready to play.")
                    // Pastikan radio dapat diputar saat sudah siap
                    startRadio()
                }
                setOnErrorListener { mp, what, extra ->
                    Log.e("MainActivity", "Error during playback: $what, $extra")
                    true
                }
                prepareAsync() // Preload secara asinkron
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error preloading radio: ${e.message}")
        }
    }

    // Mulai pemutaran radio setelah mediaPlayer siap
    private fun startRadio() {
        try {
            mediaPlayer?.start()
            isPlaying = true
        } catch (e: Exception) {
            Log.e("MainActivity", "Error starting radio: ${e.message}")
        }
    }

    // Fungsi untuk menghentikan pemutaran radio
    private fun stopRadio() {
        try {
            mediaPlayer?.pause() // Pause untuk menjaga preload tetap terjaga
            isPlaying = false
        } catch (e: Exception) {
            Log.e("MainActivity", "Error stopping radio: ${e.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAndReleaseRadio() // Lepaskan sumber daya MediaPlayer saat Activity dihancurkan
    }

    // Fungsi untuk menghentikan dan melepaskan MediaPlayer sepenuhnya
    private fun stopAndReleaseRadio() {
        try {
            mediaPlayer?.release()
            mediaPlayer = null
            isPlaying = false
        } catch (e: Exception) {
            Log.e("MainActivity", "Error releasing MediaPlayer: ${e.message}")
        }
    }

    // Fungsi untuk mendapatkan data dummy
    private fun getDummyData(): List<String> {
        return listOf(
            "Podcast 1",
            "Podcast 2",
            "Podcast 3",
            "Podcast 4",
            "Podcast 5"
        )
    }

    private fun getDummyDataSong(): List<String> {
        return listOf(
            "Song 1",
            "Song 2",
            "Song 3",
            "Song 4",
            "Song 5"
        )
    }
}
