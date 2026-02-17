package com.example.cityconnect.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cityconnect.R
import com.example.cityconnect.databinding.FragmentFeedBinding
import com.example.cityconnect.view.adapters.FeedAdapter
import com.example.cityconnect.viewmodel.FeedViewModel

class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FeedViewModel by viewModels()
    private var adapter: FeedAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rootNavController =
            (requireActivity().supportFragmentManager.findFragmentById(R.id.rootNavHost) as NavHostFragment)
                .navController

        adapter = FeedAdapter(
            currentUserId = viewModel.currentUserId,
            onEdit = { post ->
                val action = MainFragmentDirections.actionMainFragmentToPostEditorFragment(post.id)
                rootNavController.navigate(action)
            },
            onDelete = { post ->
                viewModel.delete(post.id) { /* state handled via error LiveData */ }
            },
        )

        binding.rvFeed.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFeed.adapter = adapter

        binding.btnNewPost.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToPostEditorFragment(null)
            rootNavController.navigate(action)
        }

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
        super.onDestroyView()
        binding.rvFeed.adapter = null
        adapter = null
        _binding = null
    }
}