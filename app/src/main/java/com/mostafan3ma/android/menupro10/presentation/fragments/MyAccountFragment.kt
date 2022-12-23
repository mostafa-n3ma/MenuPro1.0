package com.mostafan3ma.android.menupro10.presentation.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.mostafan3ma.android.menupro10.R
import com.mostafan3ma.android.menupro10.databinding.FragmentMyAccountBinding
import com.mostafan3ma.android.menupro10.oporations.DataManagment.repository.ShopRepository
import com.mostafan3ma.android.menupro10.oporations.data_Entities.DomainModel
import com.mostafan3ma.android.menupro10.oporations.data_Entities.cache_Entities.CacheItem
import com.mostafan3ma.android.menupro10.oporations.utils.DataState
import com.mostafan3ma.android.menupro10.presentation.fragments.adapters.ProductListener1
import com.mostafan3ma.android.menupro10.presentation.fragments.adapters.ProductsAdapter
import com.mostafan3ma.android.menupro10.presentation.fragments.viewModels.DefaultLastMenuViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
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