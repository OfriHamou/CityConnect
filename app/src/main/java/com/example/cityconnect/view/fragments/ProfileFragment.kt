package com.example.cityconnect.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.example.cityconnect.R
import com.example.cityconnect.databinding.FragmentProfileBinding
import com.example.cityconnect.viewmodel.AuthViewModel

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rootNavController =
            (requireActivity().supportFragmentManager.findFragmentById(R.id.rootNavHost) as NavHostFragment)
                .navController

        binding.btnLogout.setOnClickListener {
            authViewModel.logout()

            val options = NavOptions.Builder()
                .setPopUpTo(R.id.splashFragment, true)
                .build()

            rootNavController.navigate(R.id.loginFragment, null, options)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}