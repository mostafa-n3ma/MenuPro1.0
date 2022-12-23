package com.mostafan3ma.android.menupro10.presentation.fragments.startingFragments

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.mostafan3ma.android.menupro10.R
import com.mostafan3ma.android.menupro10.databinding.FragmentCreateAcountBinding
import com.mostafan3ma.android.menupro10.oporations.data_Entities.cache_Entities.CacheShop
import com.mostafan3ma.android.menupro10.oporations.utils.SuperImageController
import com.mostafan3ma.android.menupro10.presentation.fragments.viewModels.CreateAccountViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CreateAccountFragment
@Inject
constructor(private val superImageController: SuperImageController) : Fragment() {

    private val viewModel: CreateAccountViewModel by viewModels()

    companion object{
        const val TAG="CreateAccountFragment"
    }

    private lateinit var binding: FragmentCreateAcountBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateAcountBinding.inflate(inflater)
        binding.viewModel=viewModel
        superImageController.register(this)
        setDropDawnTypeMenu(binding)
        subscribeObservers()
        initPhoneEt()



        return binding.root
    }

    private fun initPhoneEt() {
        binding.phoneEdit.setOnFocusChangeListener { view, b ->
            if (b) {
                binding.phoneEdit.hint=""
                binding.tvCountryLabel.visibility=View.VISIBLE
                binding.countryFrameLayout.setBackgroundResource(R.drawable.country_picker_rounded_corner_yellow)
            } else {
                binding.phoneEdit.hint="Phone Number"
                binding.tvCountryLabel.visibility=View.INVISIBLE
                binding.countryFrameLayout.setBackgroundResource(R.drawable.country_picker_rounded_corner_black)
            }
        }
        viewModel.phoneNumber.observe(viewLifecycleOwner, Observer { phonNumber->
            when(phonNumber){
                 "" ->{
                     binding.imgClearPhone.visibility=View.GONE
                }
                else->{
                    binding.imgClearPhone.visibility=View.VISIBLE
                }
            }
        })
        binding.imgClearPhone.setOnClickListener {
            binding.phoneEdit.text?.clear()
        }



    }

    private fun subscribeObservers() {
        viewModel.addLogoClicked.observe(viewLifecycleOwner, Observer {clicked->
        if (clicked){
            Log.d(TAG, "subscribeObservers:AddLogoClicked ")
         superImageController.launchRegistrar()
            viewModel.addLogoCompleted()
        }
        })
        viewModel.signInClicked.observe(viewLifecycleOwner, Observer { clicked->
            if (clicked){
                Log.d(TAG, "subscribeObservers:signInClicked ")
                findNavController().navigate(R.id.action_createAccountFragment_to_putNumberFragment)
                viewModel.signInCompleted()
            }
        })
        viewModel.nextClicked.observe(viewLifecycleOwner, Observer { clicked->
            if (clicked){
                Log.d(TAG, "subscribeObservers: nextClicked")
                validateFields()
                viewModel.nextCompleted()
            }
        })
        viewModel.logoImg.observe(viewLifecycleOwner, Observer { imgBitmap->
            if (imgBitmap != null) {
                binding.imgAddLogo.setImageBitmap(imgBitmap)
            } else {
                binding.imgAddLogo.setImageResource(R.drawable.add_new_img)
            }
        })
        viewModel.type.observe(viewLifecycleOwner, Observer {type->
            when(type){
                 "Other" ->{
                  binding.newTypeField.visibility=View.VISIBLE
                }
                else->{
                    binding.newTypeField.visibility=View.GONE
                }
            }
        })
    }

    private fun validateFields() {
        var shopName:String
        var shopType:String
        var phoneNumber:String
        var logoImgName:String


        if (binding.shopNameEdit.text!!.trim().isEmpty()){
            binding.shopNameField.error="please Enter Shop Name"
            binding.shopNameField.requestFocus()
            return
        }else{
            shopName=binding.shopNameEdit.text.toString()
            binding.shopNameField.error=""
        }
        if (binding.typeAutoTxt.text!!.isEmpty()){
            binding.typeField.error="please specify a type"
            binding.typeField.requestFocus()
            return
        }else if (binding.typeAutoTxt.text.toString() =="Other"){
            if (binding.newTypeEdit.text!!.trim().isEmpty()){
                binding.typeField.error=""
                binding.newTypeField.error="enter your shop Type"
                binding.newTypeField.requestFocus()
                return
            }else{
                shopType=binding.newTypeEdit.text.toString()
                binding.newTypeField.error=""
            }
        }else{
            shopType=binding.typeAutoTxt.text.toString()
            binding.typeField.error=""
        }

        if (binding.phoneEdit.text!!.trim().isEmpty()){
            binding.phoneEdit.error="enter your phone Number"
            binding.phoneEdit.requestFocus()
            return
        }else if (binding.phoneEdit.text!!.length != 10){
            binding.phoneEdit.error=" phone Number must be 10 digits"
            binding.phoneEdit.requestFocus()
            return
        }else{
            val code=binding.countryCodePicker.selectedCountryCodeWithPlus
            phoneNumber=code+binding.phoneEdit.text.toString()
        }
        var imgBitMap:Bitmap
        if (viewModel.logoImg.value !=null){
            lifecycleScope.launch {
                val path= superImageController
                    .saveImageToInternalStorage(requireContext()
                        ,viewModel.logoImg.value!!
                        ,"logoImg")
            }

        }else{
            Toast.makeText(requireContext(),"please Add your logo image",Toast.LENGTH_SHORT).show()
            return
        }
        val shop=CacheShop(name = shopName, type = shopType, phoneNumber = phoneNumber, logoImageName = "logoImg")
        findNavController().navigate(CreateAccountFragmentDirections.actionCreateAccountFragmentToOtpFragment(shop))

    }

    private fun setDropDawnTypeMenu(binding: FragmentCreateAcountBinding) {
        val types: Array<String> =resources.getStringArray(R.array.types)
        val typesAdapter=ArrayAdapter(requireContext(),R.layout.drop_dwon_item,types)
        binding.typeAutoTxt.setAdapter(typesAdapter)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onResume() {
        super.onResume()
        if (superImageController.returnedUri != null) {
            viewModel.getChosenImg(superImageController.getBitmapFromRegister(requireContext()))
        }

    }


}
