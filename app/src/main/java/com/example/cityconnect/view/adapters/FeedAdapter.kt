package com.example.cityconnect.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cityconnect.databinding.ItemPostBinding
import com.example.cityconnect.model.schemas.Post

class FeedAdapter(
    private val currentUserId: String?,
    private val onEdit: (Post) -> Unit,
    private val onDelete: (Post) -> Unit,
) : ListAdapter<Post, FeedAdapter.PostViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PostViewHolder(
        private val binding: ItemPostBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            binding.tvOwner.text = post.ownerName
            binding.tvText.text = post.text

            val isOwner = !currentUserId.isNullOrBlank() && post.ownerId == currentUserId
            binding.btnEdit.visibility = if (isOwner) View.VISIBLE else View.GONE
            binding.btnDelete.visibility = if (isOwner) View.VISIBLE else View.GONE

            binding.btnEdit.setOnClickListener { onEdit(post) }
            binding.btnDelete.setOnClickListener { onDelete(post) }
        }
    }

    private object Diff : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem == newItem
    }
}
