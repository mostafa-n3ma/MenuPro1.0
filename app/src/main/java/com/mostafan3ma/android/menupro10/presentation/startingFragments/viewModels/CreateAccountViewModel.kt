package com.mostafan3ma.android.menupro10.presentation.startingFragments.viewModels

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class CreateAccountViewModel
@Inject
constructor(
) : ViewModel() {
    companion object {
        const val TAG = "CreateAccountViewModel"
    }

    private val _logoImg: MutableLiveData<Bitmap> = MutableLiveData()
    val logoImg: LiveData<Bitmap> get() = _logoImg
    fun getChosenImg(bitmap: Bitmap) {
        _logoImg.value = bitmap
    }


    val shopName = MutableLiveData<String>()
    val type = MutableLiveData<String>()
    val otherType=MutableLiveData<String>()
    val phoneNumber = MutableLiveData<String>()





    private val _addLogoClicked: MutableLiveData<Boolean> = MutableLiveData()
    val addLogoClicked: LiveData<Boolean> get() = _addLogoClicked
    fun addLogoClicked(){
        _addLogoClicked.value=true
    }
    fun addLogoCompleted(){
        _addLogoClicked.value=false
    }



    private val _signInClicked: MutableLiveData<Boolean> = MutableLiveData()
    val signInClicked: LiveData<Boolean> get() = _signInClicked
    fun signInClicked(){
        _signInClicked.value=true
    }
    fun signInCompleted(){
        _signInClicked.value=false
    }

    private val _nextClicked: MutableLiveData<Boolean> = MutableLiveData()
    val nextClicked: LiveData<Boolean> get() = _nextClicked
    fun nextClicked(){
        _nextClicked.value=true
    }
    fun nextCompleted(){
        _nextClicked.value=false
    }



    init {
        _addLogoClicked.value = false
        _signInClicked.value = false
        _nextClicked.value = false
    }


}

