package com.mostafan3ma.android.menupro10.presentation.fragments.startingFragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.mostafan3ma.android.menupro10.databinding.FragmentOtpBinding
import com.mostafan3ma.android.menupro10.oporations.DataManagment.repository.DefaultShopRepository
import com.mostafan3ma.android.menupro10.oporations.di.RealRepository
import com.mostafan3ma.android.menupro10.presentation.MainAuthViewModel
import com.mostafan3ma.android.menupro10.presentation.fragments.viewModels.OtpEvent
import com.mostafan3ma.android.menupro10.presentation.fragments.viewModels.OtpViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class OtpFragment
@Inject
constructor(
    @RealRepository private val repository: DefaultShopRepository) : Fragment() {

    private lateinit var binding: FragmentOtpBinding

    private val viewModel: OtpViewModel by viewModels()
    private val authViewModel:MainAuthViewModel by activityViewModels()
    companion object {
        const val TAG = "OtpFragment"
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOtpBinding.inflate(inflater)
        binding.viewModel = viewModel

        var args = OtpFragmentArgs.fromBundle(requireArguments())

        lifecycleScope.launch {
            Log.d(TAG, "onCreateView: requestPhoneNumberVerificationCode")
            repository.requestPhoneNumberVerificationCode(args.shop.phoneNumber,this@OtpFragment.requireActivity())
        }
        viewModel.getShop(args.shop)

        subscribeObservers()

        binding.otpEt.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.setViewModelEvent(OtpEvent.DoneClickedEvent)
                handled = true
            }
            handled
        })
        if (FirebaseAuth.getInstance().currentUser!=null) {
            Toast.makeText(requireContext(), "logged", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    private fun subscribeObservers() {
        authViewModel.authenticationState.observe(viewLifecycleOwner, Observer {authentication->
            when(authentication){
                MainAuthViewModel.AuthenticationState.AUTHENTICATED->{
                    viewModel.setViewModelEvent(OtpEvent.SignedInEvent)
                }
                MainAuthViewModel.AuthenticationState.UNAUTHENTICATED->{
                    //Do Nothing let the DoneClickedEvent handle it >>>
                }
            }

        })

        viewModel.codelessThenSixDigits.observe(viewLifecycleOwner, Observer { less ->
            if (less) {
                Toast.makeText(requireContext(), "code must be 6 Digits", Toast.LENGTH_SHORT).show()
            }
        })


        viewModel.resendCode.observe(viewLifecycleOwner, Observer {resend->
            if (resend){
                lifecycleScope.launch {
                    repository.resendCodeToVerifyPhoneNumber(viewModel.shop.phoneNumber,this@OtpFragment.requireActivity())
                }
            }
        })

        viewModel.navigateToCategoriesFragment.observe(
            viewLifecycleOwner,
            Observer { dataUploaded ->
                if (dataUploaded) {
                    findNavController().navigate(OtpFragmentDirections.actionOtpFragmentToAddCategoriesFragment())
                }
            })
    }
}
