package com.demo.pizzanbeer.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.demo.pizzanbeer.databinding.FragmentItemBinding
import com.demo.pizzanbeer.model.Businesses

class MyItemRecyclerViewAdapter(
    private val listOfBusinesses: List<Businesses>
) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {
    lateinit var fragmentItemBinding: FragmentItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        fragmentItemBinding = FragmentItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(fragmentItemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listOfBusinesses[position]
        holder.idView.text = item.name
        holder.contentView.text = item.categories[0].alias
        val url = item.image_url
        Glide.with(fragmentItemBinding.root).load(url).into(fragmentItemBinding.ivPoster);

    }

    override fun getItemCount(): Int = listOfBusinesses.size

    inner class ViewHolder(binding: FragmentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        val contentView: TextView = binding.content
    }
}