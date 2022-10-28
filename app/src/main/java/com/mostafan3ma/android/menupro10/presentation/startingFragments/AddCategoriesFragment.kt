package com.mostafan3ma.android.menupro10.presentation.startingFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mostafan3ma.android.menupro10.R
import com.mostafan3ma.android.menupro10.databinding.FragmentAddCatagoriesBinding

class AddCategoriesFragment : Fragment() {

    private lateinit var binding:FragmentAddCatagoriesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentAddCatagoriesBinding.inflate(inflater)



        binding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.action_addCategoriesFragment_to_addProductsFragment)
        }








        return binding.root
    }

}