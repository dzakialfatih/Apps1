package com.example.apps1.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apps1.Adapter.PodcastListAdapter
import com.example.apps1.Adapter.PodcasterListAdapter
import com.example.apps1.MainActivity.Podcast
import com.example.apps1.R

class PodcastActivity : AppCompatActivity() {
    data class Podcaster(val title: String, val description: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_podcast)

        // Inisialisasi RecyclerView untuk daftar podcast
        val recyclerViewPodcasts = findViewById<RecyclerView>(R.id.recyclerViewPodcasts)
        recyclerViewPodcasts.layoutManager = LinearLayoutManager(this)
        recyclerViewPodcasts.adapter = PodcastListAdapter(getDummyData())

        // Inisialisasi RecyclerView untuk daftar podcaster
        val recyclerViewPodcaster: RecyclerView = findViewById(R.id.recyclerViewNamePodcast)
        recyclerViewPodcaster.layoutManager = GridLayoutManager(this, 2) // 2 kolom
        recyclerViewPodcaster.adapter = PodcasterListAdapter(getDummyDataPodcaster())

    }

    // Fungsi untuk data dummy podcast
    private fun getDummyData(): List<Podcast> {
        return listOf(
            Podcast("Judul Podcast 1", "Description 1", "Nama Podcaster 1"),
            Podcast("Judul Podcast 2", "Description 2", "Nama Podcaster 2"),
            Podcast("Judul Podcast 3", "Description 3", "Nama Podcaster 3"),
            Podcast("Judul Podcast 4", "Description 4", "Nama Podcaster 4"),
            Podcast("Judul Podcast 5", "Description 5", "Nama Podcaster 5")
        )
    }

    // Fungsi untuk data dummy podcaster
    private fun getDummyDataPodcaster(): List<Podcaster> {
        return listOf(
            Podcaster("Podcaster 1", "Description 1"),
            Podcaster("Podcaster 2", "Description 2"),
            Podcaster("Podcaster 3", "Description 3"),
            Podcaster("Podcaster 4", "Description 4"),
            Podcaster("Podcaster 5", "Description 5")
        )
    }
}
