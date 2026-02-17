package com.example.cityconnect.view.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.cityconnect.databinding.FragmentEditProfileBinding
import com.example.cityconnect.viewmodel.EditProfileViewModel

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EditProfileViewModel by viewModels()

    private var selectedAvatarUri: Uri? = null

    private val pickAvatar = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedAvatarUri = uri
        if (uri != null) {
            binding.ivAvatarPreview.visibility = View.VISIBLE
            binding.ivAvatarPreview.setImageURI(uri)
        } else {
            binding.ivAvatarPreview.visibility = View.GONE
            binding.ivAvatarPreview.setImageDrawable(null)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnPickAvatar.setOnClickListener {
            pickAvatar.launch("image/*")
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnSave.isEnabled = !isLoading
        }

        viewModel.user.observe(viewLifecycleOwner) { user ->
            if (binding.etFullName.text.isNullOrBlank()) {
                binding.etFullName.setText(user?.fullName.orEmpty())
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.saveSuccess.observe(viewLifecycleOwner) { success ->
            if (success == true) {
                findNavController().popBackStack()
            }
        }

        binding.btnSave.setOnClickListener {
            val fullName = binding.etFullName.text?.toString()?.trim().orEmpty()
            viewModel.save(fullName, selectedAvatarUri)
        }

        viewModel.loadUser()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}