package com.example.cityconnect.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.cityconnect.R
import com.example.cityconnect.base.SplashDestination
import com.example.cityconnect.databinding.FragmentSplashBinding
import com.example.cityconnect.viewmodel.SplashViewModel

class SplashFragment : Fragment() {

    private var binding: FragmentSplashBinding? = null

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        return requireNotNull(binding).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.destination.observe(viewLifecycleOwner) { destination ->
            when (destination) {
                SplashDestination.ToMain -> {
                    findNavController().navigate(R.id.action_splashFragment_to_mainFragment)
                }

                SplashDestination.ToLogin -> {
                    findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
                }
            }
        }

        viewModel.decideDestination()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}