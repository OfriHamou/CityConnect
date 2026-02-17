package com.example.cityconnect.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.cityconnect.databinding.FragmentPostEditorBinding
import com.example.cityconnect.viewmodel.FeedViewModel

class PostEditorFragment : Fragment() {

    private var _binding: FragmentPostEditorBinding? = null
    private val binding get() = _binding!!

    private val args: PostEditorFragmentArgs by navArgs()
    private val viewModel: FeedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostEditorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val postId = args.postId

        if (!postId.isNullOrBlank()) {
            viewModel.observeLocalPost(postId).observe(viewLifecycleOwner) { post ->
                if (binding.etText.text.isNullOrBlank()) {
                    binding.etText.setText(post?.text.orEmpty())
                }
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnSave.isEnabled = !isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { msg ->
            if (!msg.isNullOrBlank()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSave.setOnClickListener {
            val text = binding.etText.text?.toString()?.trim().orEmpty()

            if (!postId.isNullOrBlank()) {
                viewModel.update(postId, text) { result ->
                    if (result.isSuccess) {
                        findNavController().popBackStack()
                    }
                }
            } else {
                viewModel.create(text) { result ->
                    if (result.isSuccess) {
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}