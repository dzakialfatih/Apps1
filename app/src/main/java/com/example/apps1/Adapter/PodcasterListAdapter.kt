package com.example.apps1.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.apps1.Activity.PodcastActivity
import com.example.apps1.R

class PodcasterListAdapter(private val data: List<PodcastActivity.Podcaster>) :
    RecyclerView.Adapter<PodcasterListAdapter.PodcasterViewHolder>() {

    inner class PodcasterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val podcasterName: TextView = itemView.findViewById(R.id.podcaster)
        val description: TextView = itemView.findViewById(R.id.tv_item_description)
        val imageView: ImageView = itemView.findViewById(R.id.img_item_photo)

        fun bind(podcaster: PodcastActivity.Podcaster) {
            podcasterName.text = podcaster.title
            description.text = podcaster.description
            imageView.setImageResource(R.drawable.logo_circle_medium) // Ganti dengan gambar
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PodcasterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.podcaster_list, parent, false)
        return PodcasterViewHolder(view)
    }

    override fun onBindViewHolder(holder: PodcasterViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size
}
