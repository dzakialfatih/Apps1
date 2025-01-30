package com.example.apps1.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideContext
import com.example.apps1.MainActivity
import com.example.apps1.R
import com.example.apps1.response.Song
import com.example.apps1.response.SongHistoryItem
import retrofit2.Call

class RecentSongAdapter(private val songs: List<SongHistoryItem>) :
    RecyclerView.Adapter<RecentSongAdapter.SongViewHolder>() {

    inner class SongViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val songTitle: TextView = itemView.findViewById(R.id.tv_item_name)
        val artistName: TextView = itemView.findViewById(R.id.tv_item_artist)
        val imageView: ImageView = itemView.findViewById(R.id.img_item_photo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.song_recent, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val songHistoryItem = songs[position] // Ambil item SongHistoryItem berdasarkan posisi
        val song = songHistoryItem.song       // Ambil objek Song dari SongHistoryItem

        // Set nilai ke dalam view
        holder.songTitle.text = song.title
        holder.artistName.text = song.artist
        Glide.with(holder.itemView.context)
            .load(song.art)
            .placeholder(R.drawable.logo_circle_medium)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = songs.size
}
