package com.example.apps1.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.apps1.MainActivity
import com.example.apps1.R

class PodcastListAdapter(private val podcasts: List<MainActivity.Podcast>) :
    RecyclerView.Adapter<PodcastListAdapter.PodcastViewHolder>() {

    data class Podcast(val title: String)

    inner class PodcastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val podcastTitle: TextView = itemView.findViewById(R.id.tv_item_name)
        val podcastNameTitle: TextView = itemView.findViewById(R.id.tv_item_name_podcast)
        val podcastDescTitle: TextView = itemView.findViewById(R.id.tv_item_description)
        val imageView: ImageView = itemView.findViewById(R.id.img_item_photo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PodcastViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.podcast_list, parent, false)
        return PodcastViewHolder(view)
    }

    override fun onBindViewHolder(holder: PodcastViewHolder, position: Int) {
        val podcast = podcasts[position]
        holder.podcastTitle.text = podcast.title
        holder.podcastNameTitle.text = podcast.name
        holder.podcastDescTitle.text = podcast.description
        holder.imageView.setImageResource(R.drawable.logo_circle_medium)
    }

    override fun getItemCount() = podcasts.size
}
