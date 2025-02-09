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

class EpisodesListAdapter(private var episodes: List<ResponseEpisodes>,
                          private val onItemClick: (ResponseEpisodes) -> Unit // Listener untuk item klik

) :
    RecyclerView.Adapter<EpisodesListAdapter.EpisodesViewHolder>() {

    inner class EpisodesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tv_item_name)
        val desc: TextView = itemView.findViewById(R.id.tv_item_description)
        val imageView: ImageView = itemView.findViewById(R.id.img_item_podcast)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.episodes_list, parent, false)
        return EpisodesViewHolder(view)
    }

    override fun onBindViewHolder(holder: EpisodesListAdapter.EpisodesViewHolder, position: Int) {
        val podcast = episodes[position]
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

        // Jangan panggil listener langsung di sini tanpa interaksi pengguna
        holder.itemView.setOnClickListener {
            onItemClick(podcast) // Listener hanya dipanggil saat item diklik
        }
    }


    override fun getItemCount() = episodes.size

    fun updateData(newEpisodes: List<ResponseEpisodes>) {
        episodes = newEpisodes
        notifyDataSetChanged()
    }
}