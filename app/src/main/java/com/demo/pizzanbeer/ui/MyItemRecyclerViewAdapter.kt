package com.demo.pizzanbeer.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.demo.pizzanbeer.databinding.FragmentItemBinding
import com.demo.pizzanbeer.model.Businesses

class MyItemRecyclerViewAdapter : ListAdapter<Businesses, RecyclerView.ViewHolder>(BusinessesDiffCallback()) {
    lateinit var fragmentItemBinding: FragmentItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        fragmentItemBinding = FragmentItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(fragmentItemBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val business = getItem(position)
        (holder as ViewHolder).bind(business)
//        holder.contentView.text = business.categories[0].alias
//        val url = business.image_url
//        Glide.with(fragmentItemBinding.root).load(url).into(fragmentItemBinding.ivPoster)
    }

//    override fun getItemCount(): Int = listOfBusinesses.size

//    fun updateList(listOfBusinesses: List<Businesses>) {
//        this.listOfBusinesses = listOfBusinesses as MutableList<Businesses>
//        notifyDataSetChanged()
//    }

    inner class ViewHolder(private val binding: FragmentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private var idView: TextView = binding.itemNumber
        private val contentView: TextView = binding.content
        private var imageView: ImageView = binding.ivPoster

        fun bind(business: Businesses) {
            binding.apply {
                idView.text = business.name
                contentView.text = business.categories[0].alias
                Glide.with(fragmentItemBinding.root).load(business.image_url).into(imageView)

            }
        }
    }
}


private class BusinessesDiffCallback : DiffUtil.ItemCallback<Businesses>() {

    override fun areItemsTheSame(oldItem: Businesses, newItem: Businesses): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Businesses, newItem: Businesses): Boolean {
        return oldItem == newItem
    }
}