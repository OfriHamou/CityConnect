package com.example.cityconnect.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cityconnect.databinding.FragmentLoadingBinding

class LoadingFragment : Fragment() {

    private var binding: FragmentLoadingBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoadingBinding.inflate(inflater, container, false)
        return requireNotNull(binding).root
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}