package com.example.cityconnect.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cityconnect.databinding.FragmentDirectoryBinding
import com.example.cityconnect.view.adapters.DirectoryAdapter
import com.example.cityconnect.viewmodel.DirectoryViewModel

class DirectoryFragment : Fragment() {

    private var _binding: FragmentDirectoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DirectoryViewModel by viewModels()
    private var adapter: DirectoryAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDirectoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = DirectoryAdapter()
        binding.rvPlaces.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPlaces.adapter = adapter

        binding.btnRestaurants.setOnClickListener {
            viewModel.setCategory("Restaurants")
            viewModel.refresh()
        }
        binding.btnBusinesses.setOnClickListener {
            viewModel.setCategory("Businesses")
            viewModel.refresh()
        }
        binding.btnServices.setOnClickListener {
            viewModel.setCategory("Services")
            viewModel.refresh()
        }

        viewModel.places.observe(viewLifecycleOwner) { places ->
            adapter?.submitList(places)
        }

        viewModel.error.observe(viewLifecycleOwner) { msg ->
            if (!msg.isNullOrBlank()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }

        // initial load
        viewModel.refresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvPlaces.adapter = null
        adapter = null
        _binding = null
    }
}