package com.example.cityconnect.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.example.cityconnect.R
import com.example.cityconnect.databinding.FragmentProfileBinding
import com.example.cityconnect.viewmodel.AuthViewModel
import com.example.cityconnect.viewmodel.ProfileViewModel
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {

    private var binding: FragmentProfileBinding? = null

    private val authViewModel: AuthViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return requireNotNull(binding).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = requireNotNull(binding)

        val rootNavController =
            (requireActivity().supportFragmentManager.findFragmentById(R.id.rootNavHost) as NavHostFragment)
                .navController

        binding.btnEditProfile.setOnClickListener {
            rootNavController.navigate(R.id.action_mainFragment_to_editProfileFragment)
        }

        binding.btnLogout.setOnClickListener {
            authViewModel.logout()

            val options = NavOptions.Builder()
                .setPopUpTo(R.id.splashFragment, true)
                .build()

            rootNavController.navigate(R.id.loginFragment, null, options)
        }
        binding.tvPostsCreatedValue.setOnClickListener {
            rootNavController.navigate(R.id.action_global_myPostsFragment)
        }

        profileViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        profileViewModel.user.observe(viewLifecycleOwner) { user ->
            binding.tvFullName.text = user?.fullName ?: ""
            binding.tvEmail.text = user?.email ?: ""
            binding.ivAvatar.visibility = View.VISIBLE

            val avatarUrl = user?.avatarUrl
            if (!avatarUrl.isNullOrBlank()) {
                Picasso.get()
                    .load(avatarUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .resize(240, 240)
                    .centerCrop()
                    .into(binding.ivAvatar, object : Callback {
                        override fun onSuccess() {
                        }

                        override fun onError(e: Exception?) {
                            Log.e("ProfileFragment", "Failed to load avatarUrl=$avatarUrl", e)
                        }
                    })
            } else {
                binding.ivAvatar.setImageResource(R.drawable.ic_launcher_foreground)
            }
        }

        profileViewModel.totalPosts.observe(viewLifecycleOwner) { count ->
            binding.tvStatsPosts.text = "Posts: $count"
            binding.tvPostsCreatedValue.text = count.toString()
        }

        profileViewModel.memberSince.observe(viewLifecycleOwner) { date ->
            binding.tvStatsMemberSince.text = if (date.isBlank()) "" else "Member since: $date"
            binding.tvMemberSinceValue.text = date
        }

        profileViewModel.error.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        profileViewModel.loadProfile()
    }

    override fun onResume() {
        super.onResume()
        profileViewModel.loadProfile()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}