package com.mostafan3ma.android.menupro10.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mostafan3ma.android.menupro10.databinding.FragmentMyAccountBinding
import com.mostafan3ma.android.menupro10.oporations.DataManagment.repository.ShopRepository
import com.mostafan3ma.android.menupro10.presentation.fragments.adapters.ProductsAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyAccountFragment : Fragment() {
    lateinit var binding:FragmentMyAccountBinding
    lateinit var productsAdapter: ProductsAdapter

    @Inject
    lateinit var repository: ShopRepository


    companion object{
        const val TAG="MyAccountFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentMyAccountBinding.inflate(layoutInflater)



        return binding.root
    }


}