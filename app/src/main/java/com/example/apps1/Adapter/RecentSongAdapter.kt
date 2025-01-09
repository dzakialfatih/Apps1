package com.example.apps1.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.apps1.MainActivity
import com.example.apps1.R

class RecentSongAdapter(private val songs: List<MainActivity.Song>) :
    RecyclerView.Adapter<RecentSongAdapter.SongViewHolder>() {

    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songTitle: TextView = itemView.findViewById(R.id.tv_item_name)
        val artistName: TextView = itemView.findViewById(R.id.tv_item_artist)
        val imageView: ImageView = itemView.findViewById(R.id.img_item_photo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.song_recent, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.songTitle.text = song.title
        holder.artistName.text = song.artist
        holder.imageView.setImageResource(R.drawable.logo_circle_medium)
    }

    override fun getItemCount() = songs.size
}
