package com.example.cityconnect.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cityconnect.databinding.ItemPlaceBinding
import com.example.cityconnect.model.schemas.Place
import com.squareup.picasso.Picasso

class DirectoryAdapter : ListAdapter<Place, DirectoryAdapter.PlaceVH>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceVH {
        val binding = ItemPlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaceVH(binding)
    }

    override fun onBindViewHolder(holder: PlaceVH, position: Int) {
        holder.bind(getItem(position))
    }

    class PlaceVH(private val binding: ItemPlaceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(place: Place) {
            binding.tvName.text = place.name.ifBlank { "(No name)" }
            binding.tvAddress.text = place.address

            if (place.phone.isNotBlank()) {
                binding.tvPhone.visibility = View.VISIBLE
                binding.tvPhone.text = place.phone
            } else {
                binding.tvPhone.visibility = View.GONE
            }

            val url = place.imageUrl
            if (!url.isNullOrBlank()) {
                binding.ivPlace.visibility = View.VISIBLE
                Picasso.get().load(url).fit().centerCrop().into(binding.ivPlace)
            } else {
                binding.ivPlace.visibility = View.GONE
                binding.ivPlace.setImageDrawable(null)
            }
        }
    }

    private object Diff : DiffUtil.ItemCallback<Place>() {
        override fun areItemsTheSame(oldItem: Place, newItem: Place): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Place, newItem: Place): Boolean = oldItem == newItem
    }
}
