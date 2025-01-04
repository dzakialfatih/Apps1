package com.example.apps1.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.apps1.R

class RecentSongAdapter(
    private val context: List<String>
) : RecyclerView.Adapter<RecentSongAdapter.SongViewHolder>() {

    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songImage: ImageView = itemView.findViewById(R.id.img_item_photo)
        val songTitle: TextView = itemView.findViewById(R.id.tv_item_name)
        val songArtist: TextView = itemView.findViewById(R.id.textView3)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.song_recent, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.songTitle.text = song.title
        holder.songArtist.text = song.artist
        Glide.with(context)
            .load(song.imageUrl)
            .placeholder(R.drawable.logo_circle_small) // Placeholder image
            .into(holder.songImage)
    }

    override fun getItemCount(): Int = songs.size
}
