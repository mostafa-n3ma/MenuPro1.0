package com.mostafan3ma.android.menupro10.presentation.fragments.startingFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mostafan3ma.android.menupro10.databinding.FragmentPutNumberBinding

class PutNumberFragment : Fragment() {


    private lateinit var binding: FragmentPutNumberBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentPutNumberBinding.inflate(inflater)


        return binding.root
    }

}