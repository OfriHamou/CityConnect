package com.example.cityconnect.view.fragments

import android.content.res.ColorStateList
import android.graphics.Color
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

    private fun updateChipColors(selected: View) {
        val selectedBg = Color.parseColor("#1E63FF")
        val selectedText = Color.WHITE
        val normalBg = Color.parseColor("#E9F0FF")
        val normalText = Color.parseColor("#1E63FF")

        val chips = listOf(binding.btnRestaurants, binding.btnBusinesses, binding.btnServices)
        chips.forEach { chip ->
            val isSelected = chip === selected
            chip.chipBackgroundColor = ColorStateList.valueOf(if (isSelected) selectedBg else normalBg)
            chip.setTextColor(if (isSelected) selectedText else normalText)
        }
    }

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
        updateChipColors(binding.btnRestaurants)

        binding.btnRestaurants.setOnClickListener {
            updateChipColors(it)
            viewModel.setCategory("Restaurants")
            viewModel.refresh()
        }
        binding.btnBusinesses.setOnClickListener {
            updateChipColors(it)
            viewModel.setCategory("Businesses")
            viewModel.refresh()
        }
        binding.btnServices.setOnClickListener {
            updateChipColors(it)
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
        viewModel.refresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvPlaces.adapter = null
        adapter = null
        _binding = null
    }
}