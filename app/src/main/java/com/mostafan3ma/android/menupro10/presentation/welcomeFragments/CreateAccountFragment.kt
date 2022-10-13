package com.mostafan3ma.android.menupro10.presentation.welcomeFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mostafan3ma.android.menupro10.R
import com.mostafan3ma.android.menupro10.databinding.FragmentCreateAcountBinding

class CreateAccountFragment : Fragment() {

private lateinit var binding: FragmentCreateAcountBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentCreateAcountBinding.inflate(inflater)



        return binding.root
    }

}