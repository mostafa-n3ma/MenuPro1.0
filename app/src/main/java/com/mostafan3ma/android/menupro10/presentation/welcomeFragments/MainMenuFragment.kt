package com.mostafan3ma.android.menupro10.presentation.welcomeFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mostafan3ma.android.menupro10.R
import com.mostafan3ma.android.menupro10.databinding.FragmentMainmenuBinding
import com.mostafan3ma.android.menupro10.oporations.DataManagment.repository.DefaultShopRepository
import com.mostafan3ma.android.menupro10.oporations.di.RealRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainMenuFragment
@Inject
constructor(@RealRepository private val repository: DefaultShopRepository) : Fragment() {

    private lateinit var binding: FragmentMainmenuBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainmenuBinding.inflate(inflater)


        binding.btnGetStarted.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_createAccountFragment)
        }



        return binding.root
    }
}