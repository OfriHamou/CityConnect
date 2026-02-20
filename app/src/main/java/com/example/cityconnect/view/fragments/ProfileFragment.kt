package com.example.cityconnect.view.fragments
//test for amit
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

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()

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

        binding.btnEditProfile.setOnClickListener {
            // Navigate to ROOT EditProfile destination from INNER tab
            rootNavController.navigate(R.id.action_mainFragment_to_editProfileFragment)
        }

        binding.btnLogout.setOnClickListener {
            authViewModel.logout()

            val options = NavOptions.Builder()
                .setPopUpTo(R.id.splashFragment, true)
                .build()

            rootNavController.navigate(R.id.loginFragment, null, options)
        }

        profileViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        profileViewModel.user.observe(viewLifecycleOwner) { user ->
            binding.tvFullName.text = user?.fullName ?: ""
            binding.tvEmail.text = user?.email ?: ""

            // Always keep avatar visible (mockup style)
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
                            // no-op
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
            // Legacy hidden header stats
            binding.tvStatsPosts.text = "Posts: $count"
            // New Account Details card
            binding.tvPostsCreatedValue.text = count.toString()
        }

        profileViewModel.memberSince.observe(viewLifecycleOwner) { date ->
            // Legacy hidden header stats
            binding.tvStatsMemberSince.text = if (date.isBlank()) "" else "Member since: $date"
            // New Account Details card
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
        super.onDestroyView()
        _binding = null
    }
}