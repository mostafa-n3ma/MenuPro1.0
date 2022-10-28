package com.mostafan3ma.android.menupro10.presentation.startingFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mostafan3ma.android.menupro10.R
import com.mostafan3ma.android.menupro10.databinding.FragmentAddProductsBinding

class AddProductsFragment : Fragment() {

    private lateinit var binding: FragmentAddProductsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= FragmentAddProductsBinding.inflate(inflater)






        return binding.root
    }


}