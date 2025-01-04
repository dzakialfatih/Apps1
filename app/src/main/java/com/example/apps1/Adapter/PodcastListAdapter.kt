package com.example.apps1.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class PodcastListAdapter(private val dataList: List<String>) : RecyclerView.Adapter<PodcastListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = dataList[position]
        holder.textView.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Clicked: ${dataList[position]}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}