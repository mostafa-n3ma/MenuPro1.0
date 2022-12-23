package com.mostafan3ma.android.menupro10.presentation.fragments.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mostafan3ma.android.menupro10.oporations.DataManagment.repository.DefaultShopRepository
import com.mostafan3ma.android.menupro10.oporations.data_Entities.cache_Entities.CacheShop
import com.mostafan3ma.android.menupro10.oporations.di.RealRepository
import com.mostafan3ma.android.menupro10.oporations.utils.EntitiesMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpViewModel
@Inject
constructor(
    @RealRepository private val repository: DefaultShopRepository,
    private val mapper: EntitiesMapper
) : ViewModel() {

    companion object {
        const val TAG = "OtpViewModel"
    }


    val code = MutableLiveData<String>()
    private val _codelessThenSixDigits = MutableLiveData<Boolean>()
    val codelessThenSixDigits: LiveData<Boolean> get() = _codelessThenSixDigits
    private val _navigateToCategoriesFragment=MutableLiveData<Boolean>()
    val navigateToCategoriesFragment:LiveData<Boolean>get() = _navigateToCategoriesFragment
    private val _resendCode=MutableLiveData<Boolean>()
    val resendCode:LiveData<Boolean> get() = _resendCode




      init {
        code.value = ""
        _codelessThenSixDigits.value = false
    }


     lateinit var shop: CacheShop

    fun getShop(shop: CacheShop) {
        this.shop = shop
    }


    fun setViewModelEvent(event: OtpEvent) {
        when (event) {

            is OtpEvent.ResendCodeEvent -> {
               _resendCode.value=true
               _resendCode.value=false
            }
            is OtpEvent.DoneClickedEvent -> {
                Log.d(TAG, "setViewModelEvent: DoneClicked")
                validateCodeAndSignIn()
            }

            is OtpEvent.NavigateToCategoriesFragment->{
                _navigateToCategoriesFragment.value=true
                _navigateToCategoriesFragment.value=false

            }
            is OtpEvent.SignedInEvent->{
                saveData()
            }
            else -> {

            }
        }


    }

    private fun signIn() {
        viewModelScope.launch {
             repository.signIn(code.value!!)
//            val uid =
//            if (uid != null) {
//                shop.id = uid
//                repository.insertShop(shop)
//                val domainShop = mapper.buildDomainFromCache(shop)
//                repository.uploadCacheData(
//                    collectionName = "shops",
//                    shopName = shop.name,
//                    mapper.mapDomainToNetworkEntity(domainShop)
//                ).let {success->
//                    if (success){
//                        setViewModelEvent(OtpEvent.NavigateToCategoriesFragment)
//                    }
//                }
//            }
        }
    }

    private fun saveData(){
        viewModelScope.launch {
           val uid: String = FirebaseAuth.getInstance().currentUser!!.uid
            shop.id = uid
            repository.insertShop(shop)
            val domainShop = mapper.buildDomainFromCache(shop)
            repository.uploadCacheData(
                collectionName = "shops",
                shopName = shop.name,
                mapper.mapDomainToNetworkEntity(domainShop)
            ).let {success->
                if (success){
                    setViewModelEvent(OtpEvent.NavigateToCategoriesFragment)
                }
            }
        }
    }


    fun triggerResendEvent() {
        setViewModelEvent(OtpEvent.ResendCodeEvent)
        Log.d(TAG, "triggerResendEvent: ")
    }




    private fun validateCodeAndSignIn() {
        when (code.value?.length) {
            6 -> {
                Log.d(TAG, "validateCodeAndSendOtpRequest: code is 6 Digits")
                signIn()
            }
            else -> {
                Log.d(TAG, "validateCodeAndSendOtpRequest: code is less then 6 digits")
                _codelessThenSixDigits.value = true
                _codelessThenSixDigits.value = false
            }
        }
    }


}

sealed class OtpEvent() {
    object ResendCodeEvent : OtpEvent()
    object DoneClickedEvent : OtpEvent()
    object NavigateToCategoriesFragment:OtpEvent()
    object SignedInEvent:OtpEvent()
}


