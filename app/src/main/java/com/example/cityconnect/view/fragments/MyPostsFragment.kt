package com.example.cityconnect.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cityconnect.R
import com.example.cityconnect.databinding.FragmentMyPostsBinding
import com.example.cityconnect.view.adapters.FeedAdapter
import com.example.cityconnect.viewmodel.FeedViewModel

class MyPostsFragment : Fragment() {

    private var binding: FragmentMyPostsBinding? = null

    private val viewModel: FeedViewModel by viewModels()
    private var adapter: FeedAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMyPostsBinding.inflate(inflater, container, false)
        return requireNotNull(binding).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = requireNotNull(binding)
        binding.toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        val rootNavController =
            (requireActivity().supportFragmentManager.findFragmentById(R.id.rootNavHost) as NavHostFragment)
                .navController

        adapter = FeedAdapter(
            currentUserId = viewModel.currentUserId,
            onEdit = { post ->
                rootNavController.navigate(
                    R.id.action_myPostsFragment_to_postEditorFragment,
                    Bundle().apply { putString("postId", post.id) }
                )
            },
            onDelete = { post ->
                val dialog = AlertDialog.Builder(requireContext())
                    .setTitle("Delete post?")
                    .setMessage("This can't be undone.")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Delete") { _, _ ->
                        viewModel.delete(post.id) { /* errors via LiveData */ }
                    }
                    .show()

                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.dialog_cancel))
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.dialog_delete))
            },
        )

        binding.rvMyPosts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMyPosts.adapter = adapter
        viewModel.setShowOnlyMine(true)

        viewModel.posts.observe(viewLifecycleOwner) { posts ->
            adapter?.submitList(posts)
        }

        viewModel.error.observe(viewLifecycleOwner) { msg ->
            if (!msg.isNullOrBlank()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.refresh()
    }

    override fun onDestroyView() {
        binding?.rvMyPosts?.adapter = null
        adapter = null
        binding = null
        super.onDestroyView()
    }
}