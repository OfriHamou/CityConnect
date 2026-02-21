package com.example.cityconnect.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.cityconnect.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private var binding: FragmentMainBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return requireNotNull(binding).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = requireNotNull(binding)

        val navHostFragment = childFragmentManager
            .findFragmentById(binding.mainNavHost.id) as NavHostFragment
        val navController = navHostFragment.navController

        // Minimal + robust: handle selection ourselves
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val currentId = navController.currentDestination?.id
            if (currentId != item.itemId) {
                navController.navigate(item.itemId)
            }
            true
        }

        // Keep bottom nav checked item in sync
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigation.menu.findItem(destination.id)?.isChecked = true
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
