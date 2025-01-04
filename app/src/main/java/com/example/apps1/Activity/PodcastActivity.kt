package com.example.apps1.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.apps1.R

class PodcastActivity : AppCompatActivity() {

    private lateinit var button2: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_podcast)

        // Initialize views after setContentView
        button2 = findViewById(R.id.button2)

        // Set click listener for the button
        button2.setOnClickListener {
            val intent = Intent(this, NamePodcastActivity::class.java)
            startActivity(intent)
        }

        val rootLayout = findViewById<ConstraintLayout>(R.id.main) // Assuming your root view is a ConstraintLayout with id "main"
        if (rootLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }
    }
}