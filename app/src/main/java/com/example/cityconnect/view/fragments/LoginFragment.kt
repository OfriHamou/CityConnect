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
import com.example.cityconnect.databinding.FragmentLoginBinding
import com.example.cityconnect.viewmodel.AuthViewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text?.toString()?.trim().orEmpty()
            val pass = binding.etPassword.text?.toString()?.trim().orEmpty()

            authViewModel.login(email, pass)
        }

        binding.tvGoRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        authViewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                AuthState.Idle -> {
                    binding.progress.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                }

                AuthState.Loading -> {
                    binding.progress.visibility = View.VISIBLE
                    binding.btnLogin.isEnabled = false
                }

                AuthState.Success -> {
                    binding.progress.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
                }

                is AuthState.Error -> {
                    binding.progress.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}