package com.example.apps1.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.apps1.Activity.PodcastActivity
import com.example.apps1.HeaderLoader
import com.example.apps1.R
import com.example.apps1.response.ResponsePodcast

class PodcasterListAdapter(private var podcaster: List<ResponsePodcast>,
                           private val onItemClick: (ResponsePodcast) -> Unit // Tambahkan callback klik
    ) :
    RecyclerView.Adapter<PodcasterListAdapter.PodcasterViewHolder>() {

    inner class PodcasterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val podcasterName: TextView = itemView.findViewById(R.id.podcaster)
        val description: TextView = itemView.findViewById(R.id.tv_item_description)
        val imageView: ImageView = itemView.findViewById(R.id.img_item_podcaster)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PodcasterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.podcaster_list, parent, false)
        return PodcasterViewHolder(view)
    }

    override fun onBindViewHolder(holder: PodcasterViewHolder, position: Int) {
        val podcast = podcaster[position]
        holder.podcasterName.text = podcast.author ?: "Prambors"
        val maxLength = 50 // Panjang maksimum karakter yang diinginkan
        val fullDescription = podcast.description
        if (fullDescription != null) {
            holder.description.text = if (fullDescription.length > maxLength) {
                "${fullDescription?.substring(0, maxLength)}..."
            } else {
                fullDescription
            }
        }
        Glide.with(holder.itemView.context)
            .load(HeaderLoader.getUrlWithHeaders(podcast.art.toString()))
            .error(R.drawable.logo_circle_medium)
            .into(holder.imageView)

        // Set klik listener
        holder.itemView.setOnClickListener {
            onItemClick(podcast)
        }    }

    override fun getItemCount(): Int = podcaster.size

    fun updateData(newPodcaster: List<ResponsePodcast>) {
        podcaster = newPodcaster
        notifyDataSetChanged()
    }


}
