package com.example.apps1.Activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.apps1.MainActivity
import com.example.apps1.R
import com.example.apps1.SharedViewModel

abstract class BaseActivity : AppCompatActivity() {
    companion object {
        var isPlayingPodcast = false
        var isPlayingRadio = false
    }

    private lateinit var sharedViewModel: SharedViewModel
    protected lateinit var miniPlayer: View
    protected lateinit var imgMiniPlayer: ImageView
    protected lateinit var tvMiniPlayerTitle: TextView
    protected lateinit var btnPlayPause: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResource())  // Pastikan layout tidak null sebelum digunakan

        setupMiniPlayer()

    }

    // Wajib diimplementasikan oleh semua child Activity
    abstract fun getLayoutResource(): Int


    private fun setupMiniPlayer() {
        val miniPlayer = findViewById<View?>(R.id.miniPlayer)
        val imgMiniPlayer = findViewById<ImageView?>(R.id.imgMiniPlayer)
        val tvMiniPlayerTitle = findViewById<TextView?>(R.id.tvMiniPlayerTitle)
        val btnPlayPause = findViewById<ImageButton?>(R.id.btnPlayPause)


        // Cek apakah miniPlayer ada dalam layout sebelum digunakan
        if (miniPlayer != null && imgMiniPlayer != null && tvMiniPlayerTitle != null && btnPlayPause != null) {
//            tvMiniPlayerTitle.text = "Podcast Episode 1"
            sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]

            // Observasi perubahan data
            sharedViewModel.songTitle.observe(this) { title ->
                tvMiniPlayerTitle.text = title
            }
            Glide.with(this).load(R.drawable.logo_circle_small).into(imgMiniPlayer)

            // Default tombol diatur sebagai "play"
            btnPlayPause.setImageResource(R.drawable.play_icon_foreground)
            btnPlayPause.tag = "paused"

            btnPlayPause.setOnClickListener {
                if (btnPlayPause.tag == "paused") {
                    btnPlayPause.setImageResource(R.drawable.ic_pause)
                    btnPlayPause.tag = "playing"
                    // Panggil startRadio jika activity saat ini adalah MainActivity
                    if (this is MainActivity) {
                        startRadio()
                    }
                } else {
                    btnPlayPause.setImageResource(R.drawable.play_icon_foreground)
                    btnPlayPause.tag = "paused"
                    // Panggil startRadio jika activity saat ini adalah MainActivity
                    if (this is MainActivity) {
                        stopRadio()
                    }
                }
            }
        } else {
            Log.w("MiniPlayer", "MiniPlayer tidak ditemukan dalam layout!")
        }
    }


    private fun playRadio() {

    }

    private fun pausePodcast() {
        Log.d("MiniPlayer", "Podcast Paused...")
    }
}
