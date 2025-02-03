package com.example.apps1.Adapter

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.Headers
import com.bumptech.glide.request.RequestListener
import com.example.apps1.MainActivity
import com.example.apps1.R
import com.example.apps1.response.ResponsePodcast
import com.bumptech.glide.request.target.Target
import com.example.apps1.HeaderLoader


class PodcastListAdapter(private var podcasts: List<ResponsePodcast>, private val onItemClick: (ResponsePodcast) -> Unit // Tambahkan callback klik
) :
    RecyclerView.Adapter<PodcastListAdapter.PodcastViewHolder>() {

    inner class PodcastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val podcastTitle: TextView = itemView.findViewById(R.id.tv_item_name)
        val podcastNameTitle: TextView = itemView.findViewById(R.id.tv_item_name_podcast)
        val podcastDescTitle: TextView = itemView.findViewById(R.id.tv_item_description)
        val imageView: ImageView = itemView.findViewById(R.id.img_item_podcast)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PodcastViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.podcast_list, parent, false)
        return PodcastViewHolder(view)
    }

    override fun onBindViewHolder(holder: PodcastViewHolder, position: Int) {
        val podcast = podcasts[position]
        holder.podcastTitle.text = podcast.title
        holder.podcastNameTitle.text = podcast.author
        Log.d("link", "${podcast.links?.art}")
        holder.podcastDescTitle.text = podcast.descriptionShort
        Glide.with(holder.itemView.context)
            .load(HeaderLoader.getUrlWithHeaders(podcast.art.toString()))
            .error(R.drawable.logo_circle_medium)
            .into(holder.imageView)

        // Set klik listener
        holder.itemView.setOnClickListener {
            onItemClick(podcast)
        }
    }

    override fun getItemCount() = podcasts.size

    fun updateData(newPodcasts: List<ResponsePodcast>) {
        podcasts = newPodcasts
        notifyDataSetChanged()
    }
}
