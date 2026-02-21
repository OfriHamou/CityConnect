package com.example.cityconnect.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cityconnect.databinding.ItemPostBinding
import com.example.cityconnect.model.schemas.Post
import com.squareup.picasso.Picasso
import kotlin.math.abs

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

        private fun timeAgo(timeMillis: Long): String {
            if (timeMillis <= 0L) return ""
            val diff = abs(System.currentTimeMillis() - timeMillis)
            val minutes = diff / 60000
            val hours = minutes / 60
            val days = hours / 24

            return when {
                minutes < 60 -> "${minutes}m ago"
                hours < 24 -> "${hours}h ago"
                else -> "${days}d ago"
            }
        }

        fun bind(post: Post) {
            binding.tvOwner.text = post.ownerName
            binding.tvDate.text = timeAgo(post.createdAt)
            binding.tvText.text = post.text

            // Avatar
            val ownerAvatarUrl = post.ownerAvatarUrl
            if (ownerAvatarUrl.isNotBlank()) {
                Picasso.get()
                    .load(ownerAvatarUrl)
                    .placeholder(com.example.cityconnect.R.drawable.ic_launcher_foreground)
                    .error(com.example.cityconnect.R.drawable.ic_launcher_foreground)
                    .fit()
                    .centerCrop()
                    .into(binding.ivAvatar)
            } else {
                binding.ivAvatar.setImageResource(com.example.cityconnect.R.drawable.ic_launcher_foreground)
            }

            // Post image
            val imageUrl = post.imageUrl
            if (!imageUrl.isNullOrBlank()) {
                binding.ivPostImage.visibility = View.VISIBLE
                Picasso.get().load(imageUrl).fit().centerCrop().into(binding.ivPostImage)
            } else {
                binding.ivPostImage.visibility = View.GONE
                binding.ivPostImage.setImageDrawable(null)
            }

            val isOwner = !currentUserId.isNullOrBlank() && post.ownerId == currentUserId
            binding.tvEdit.visibility = if (isOwner) View.VISIBLE else View.GONE
            binding.tvDelete.visibility = if (isOwner) View.VISIBLE else View.GONE

            // Tap edit (owner only)
            binding.tvEdit.setOnClickListener { if (isOwner) onEdit(post) }

            // Tap delete (owner only)
            binding.tvDelete.setOnClickListener { if (isOwner) onDelete(post) }

            // Simple delete gesture (owner only): long-press the card
            binding.root.setOnLongClickListener {
                if (isOwner) {
                    onDelete(post)
                    true
                } else {
                    false
                }
            }
        }
    }

    private object Diff : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem == newItem
    }
}