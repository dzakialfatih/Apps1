package com.example.apps1.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.apps1.HeaderLoader
import com.example.apps1.R
import com.example.apps1.response.ResponseEpisodes
import com.example.apps1.response.ResponseMedia
import com.example.apps1.response.ResponsePodcast


class MediaListAdapter(private var media: List<ResponseMedia>,

                       ) :
    RecyclerView.Adapter<MediaListAdapter.MediaViewHolder>() {

    inner class MediaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tv_item_name)
        val desc: TextView = itemView.findViewById(R.id.tv_item_description)
        val imageView: ImageView = itemView.findViewById(R.id.img_item_podcast)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.episodes_list, parent, false)
        return MediaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MediaListAdapter.MediaViewHolder, position: Int) {
        val podcast = media[position]
        holder.title.text = podcast.title
        val maxLength = 50 // Panjang maksimum karakter yang diinginkan
        val fullDescription = podcast.description
        if (fullDescription != null) {
            holder.desc.text = if (fullDescription.length > maxLength) {
                "${fullDescription?.substring(0, maxLength)}..."
            } else {
                fullDescription
            }
        }
        Log.d("link", "${podcast.links?.art}")
        Glide.with(holder.itemView.context)
            .load(HeaderLoader.getUrlWithHeaders(podcast.art.toString()))
            .error(R.drawable.logo_circle_medium)
            .into(holder.imageView)

    }


    override fun getItemCount() = media.size

    fun updateData(newMedia: List<ResponseMedia>) {
        media = newMedia
        notifyDataSetChanged()
    }
}
