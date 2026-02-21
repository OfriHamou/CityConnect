package com.example.cityconnect.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.cityconnect.R
import com.example.cityconnect.base.AuthState
import com.example.cityconnect.databinding.FragmentRegisterBinding
import com.example.cityconnect.viewmodel.AuthViewModel

class RegisterFragment : Fragment() {

    private var binding: FragmentRegisterBinding? = null

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return requireNotNull(binding).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = requireNotNull(binding)

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text?.toString()?.trim().orEmpty()
            val fullName = binding.etFullName.text?.toString()?.trim().orEmpty()
            val pass = binding.etPassword.text?.toString()?.trim().orEmpty()

            authViewModel.register(email, pass, fullName)
        }

        binding.tvGoLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        authViewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                AuthState.Idle -> {
                    binding.progress.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                }

                AuthState.Loading -> {
                    binding.progress.visibility = View.VISIBLE
                    binding.btnRegister.isEnabled = false
                }

                AuthState.Success -> {
                    binding.progress.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                    findNavController().navigate(R.id.action_registerFragment_to_mainFragment)
                }

                is AuthState.Error -> {
                    binding.progress.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}