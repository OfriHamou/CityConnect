package com.example.cityconnect.view.adapters

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cityconnect.R
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

            val categoryRes = when (place.category) {
                "Restaurants" -> R.drawable.restaurant
                "Businesses" -> R.drawable.business
                "Services" -> R.drawable.services
                else -> R.drawable.restaurant
            }

            // Requested: system icon while loading
            val loadingRes = android.R.drawable.ic_menu_gallery

            val targetHeightPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                170f,
                binding.root.resources.displayMetrics
            ).toInt()
            val targetWidthPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                360f,
                binding.root.resources.displayMetrics
            ).toInt()

            val request = if (!place.imageUrl.isNullOrBlank()) {
                Picasso.get().load(place.imageUrl)
            } else {
                // Show category images (restaurants/business/services) but decoded safely.
                Picasso.get().load(categoryRes)
            }

            request
                .placeholder(loadingRes)
                .error(categoryRes)
                .resize(targetWidthPx, targetHeightPx)
                .centerCrop()
                .into(binding.ivPlace)
        }
    }

    private object Diff : DiffUtil.ItemCallback<Place>() {
        override fun areItemsTheSame(oldItem: Place, newItem: Place): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Place, newItem: Place): Boolean = oldItem == newItem
    }
}
