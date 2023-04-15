package com.mostafan3ma.android.menupro10.presentation.fragments.startingFragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.mostafan3ma.android.menupro10.databinding.FragmentWelcomBinding
import com.mostafan3ma.android.menupro10.presentation.MainAuthViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class WelcomeFragment
    : Fragment() {
companion object{
    const val TAG="WelcomeFragment"
}


    private lateinit var binding: FragmentWelcomBinding
    private val authViewModel :MainAuthViewModel by activityViewModels()




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWelcomBinding.inflate(inflater)



        authViewModel.authenticationState.observe(viewLifecycleOwner,androidx.lifecycle.Observer{Authentication->
            when(Authentication){
                MainAuthViewModel.AuthenticationState.AUTHENTICATED->{
                    Log.d(TAG, "WelcomeFragment/onCreateView:(AuthenticationState=AUTHENTICATED) ")
                    //navigate to DefaultMenuFragment
                    findNavController().navigate(WelcomeFragmentDirections.actionMainMenuFragmentToAddCategoriesFragment())
                }
                MainAuthViewModel.AuthenticationState.UNAUTHENTICATED->{
                    Log.d(TAG, "WelcomeFragment/onCreateView:(AuthenticationState=UNAUTHENTICATED) ")
                    //go with the create Account processes GetStarted button
                    binding.btnGetStarted.setOnClickListener {
//                        findNavController().navigate(WelcomeFragmentDirections.actionMainMenuFragmentToCreateAccountFragment())
                        findNavController().navigate(WelcomeFragmentDirections.actionMainMenuFragmentToAddCategoriesFragment())
                    }
                }
            }
        })






        return binding.root
    }


}