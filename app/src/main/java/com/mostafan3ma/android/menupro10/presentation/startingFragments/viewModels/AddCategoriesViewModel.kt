package com.mostafan3ma.android.menupro10.presentation.startingFragments.viewModels

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mostafan3ma.android.menupro10.oporations.DataManagment.repository.DefaultShopRepository
import com.mostafan3ma.android.menupro10.oporations.data_Entities.Category
import com.mostafan3ma.android.menupro10.oporations.data_Entities.mapToCacheCategory
import com.mostafan3ma.android.menupro10.oporations.di.RealRepository
import com.mostafan3ma.android.menupro10.oporations.utils.SuperImageController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddCategoriesViewModel
@Inject
constructor(
    @RealRepository private val repository: DefaultShopRepository,
    val superImageController: SuperImageController
) : ViewModel() {

    val returnedImgUri = MutableLiveData<Uri?>()

    private val _categoriesList = MutableLiveData<List<Category>>()
    val categoriesList: LiveData<List<Category>> get() = _categoriesList

    private val _bottomSheetLaunched = MutableLiveData<Boolean>()
    val bottomSheetLaunched: LiveData<Boolean> get() = _bottomSheetLaunched

    private val _clickedCategory = MutableLiveData<Category>()
    val clickedCategory: LiveData<Category> get() = _clickedCategory

    var position: Int = 0

    private val _hideKeyBord = MutableLiveData<Boolean>()
    val hideKeyBord: LiveData<Boolean> get() = _hideKeyBord

    private val _clickableEnabled = MutableLiveData<Boolean>()
    val clickableEnabled: LiveData<Boolean> get() = _clickableEnabled

    private val _addEditBtnTxt = MutableLiveData<String>()
    val addEditBtnTxt: LiveData<String> get() = _addEditBtnTxt

    private val _savingImagesRequest=MutableLiveData<Boolean>()
    val savingImagesRequest:LiveData<Boolean>get() = _savingImagesRequest



    private val _navigateToAddItemFragment = MutableLiveData<Boolean>()
    val navigateToAddItemFragment: LiveData<Boolean> get() = _navigateToAddItemFragment


    init {
        viewModelScope.launch {
            repository.clearCategories()
        }
        _categoriesList.value =
            mutableListOf(
                Category(),
                Category(),
                Category(),
                Category(),
                Category(),
                Category()
            )
        _bottomSheetLaunched.value = false
        _clickedCategory.value = Category()
        _hideKeyBord.value = false
        _clickableEnabled.value = true
        _addEditBtnTxt.value = "AddCategory"
        returnedImgUri.value = null
        _navigateToAddItemFragment.value = false
        _savingImagesRequest.value=false
    }


    @RequiresApi(Build.VERSION_CODES.P)
    fun setEvent(event: AddCategoryViewModelEvents) {
        when (event) {

            is AddCategoryViewModelEvents.ItemClicked -> {
                Log.d(TAG, "setEvent: Item clicked")
                setEvent(AddCategoryViewModelEvents.LaunchCategoryBottomSheet)
            }

            is AddCategoryViewModelEvents.LaunchCategoryBottomSheet -> {
                Log.d(TAG, "setEvent: LaunchCategoryBottomSheet")
                _bottomSheetLaunched.value = true
                Log.d(TAG, " _bottomSheetLaunched = ${_bottomSheetLaunched.value}")
                setEvent(AddCategoryViewModelEvents.DisableClicks)
                setEvent(AddCategoryViewModelEvents.ValidateAddButton)
            }
            is AddCategoryViewModelEvents.ValidateAddButton -> {
                when (_clickedCategory.value) {
                    Category() -> {
                        _addEditBtnTxt.value = "Add Category"
                    }
                    else -> {
                        _addEditBtnTxt.value = "Edit Category"
                    }

                }
            }
            is AddCategoryViewModelEvents.CancelCategoryBottomSheet -> {
                setEvent(AddCategoryViewModelEvents.HideKeyBordEvent)
                Log.d(TAG, "setEvent: CancelCategoryBottomSheet")
                viewModelScope.launch {
                    delay(50)
                    _bottomSheetLaunched.value = false
                }
                Log.d(TAG, " _bottomSheetLaunched = ${_bottomSheetLaunched.value}")
                setEvent(AddCategoryViewModelEvents.EnableClicks)
                returnedImgUri.value = ("").toUri()
                arrangeCategoriesList()
            }
            is AddCategoryViewModelEvents.DisableClicks -> {
                _clickableEnabled.value = false
                Log.d(TAG, "setEvent: DisableClicks")
                Log.d(TAG, "_clickableEnabled :${_clickableEnabled.value} ")
            }
            is AddCategoryViewModelEvents.EnableClicks -> {
                _clickableEnabled.value = true
                Log.d(TAG, "setEvent: EnableClicks")
                Log.d(TAG, "_clickableEnabled :${_clickableEnabled.value} ")
            }
            is AddCategoryViewModelEvents.AddEditCategory -> {
                _clickedCategory.value!!.imageName = _clickedCategory.value!!.name
                _clickedCategory.value!!.imageUri = returnedImgUri.value.toString()
                val list: MutableList<Category> = _categoriesList.value!!.toMutableList()
                list[position] = _clickedCategory.value!!
                _categoriesList.value = list
                setEvent(AddCategoryViewModelEvents.CancelCategoryBottomSheet)
            }
            is AddCategoryViewModelEvents.HideKeyBordEvent -> {
                Log.d(TAG, "setEvent: hideKeyBord")
                _hideKeyBord.value = true
                Log.d(TAG, "_hideKeyBord: ${_hideKeyBord.value} ")
                _hideKeyBord.value = false
                Log.d(TAG, "_hideKeyBord: ${_hideKeyBord.value} ")
            }

            is AddCategoryViewModelEvents.NextBtnClicked -> {
                if (_categoriesList.value!!.isEmpty()){
                    setEvent(AddCategoryViewModelEvents.NavigateToAddItemsFragment)
                }else{
                    for (category: Category in _categoriesList.value!!) {
                        viewModelScope.launch {
                            setEvent(AddCategoryViewModelEvents.SaveImagesEvent)
                            repository.insertCategory(category.mapToCacheCategory(category))
                        }
                    }
                    setEvent(AddCategoryViewModelEvents.NavigateToAddItemsFragment)
                }
            }
            is AddCategoryViewModelEvents.SkipBtnClicked -> {
                setEvent(AddCategoryViewModelEvents.NavigateToAddItemsFragment)
            }
            is AddCategoryViewModelEvents.NavigateToAddItemsFragment -> {
                _navigateToAddItemFragment.value = true
                _navigateToAddItemFragment.value = false
            }
            is AddCategoryViewModelEvents.SaveImagesEvent->{
                _savingImagesRequest.value=true
                _savingImagesRequest.value=false
            }



            else -> {

            }


        }
    }


    @RequiresApi(Build.VERSION_CODES.P)
    fun cancelBottomSheet() {
        Log.d(TAG, "cancelBottomSheet: triggered")
        setEvent(AddCategoryViewModelEvents.CancelCategoryBottomSheet)
    }


    fun getClickedCategory(position: Int) {
        _clickedCategory.value = _categoriesList.value!![position]
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun addEditCategory() {
        setEvent(AddCategoryViewModelEvents.AddEditCategory)
    }


    private fun arrangeCategoriesList() {
        var emptyCategory = 0
        for (category: Category in _categoriesList.value!!) {
            when (category) {
                Category() -> {
                    emptyCategory += 1
                }
                else -> {
                }
            }
        }

        when (emptyCategory) {
            0 -> {
                val mutableCategoryList = _categoriesList.value!!.toMutableList()
                mutableCategoryList.add(Category())
                _categoriesList.value = mutableCategoryList.toList()
            }
        }


    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun clickNext() {
        setEvent(AddCategoryViewModelEvents.NextBtnClicked)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun clickSkip() {
        setEvent(AddCategoryViewModelEvents.SkipBtnClicked)
    }




    companion object {
        const val TAG = "AddCategoriesViewModel"
    }
}


sealed class AddCategoryViewModelEvents() {
    object ItemClicked : AddCategoryViewModelEvents()
    object LaunchCategoryBottomSheet : AddCategoryViewModelEvents()
    object CancelCategoryBottomSheet : AddCategoryViewModelEvents()
    object DisableClicks : AddCategoryViewModelEvents()
    object EnableClicks : AddCategoryViewModelEvents()
    object ValidateAddButton : AddCategoryViewModelEvents()
    object AddEditCategory : AddCategoryViewModelEvents()
    object HideKeyBordEvent : AddCategoryViewModelEvents()
    object NextBtnClicked : AddCategoryViewModelEvents()
    object SkipBtnClicked : AddCategoryViewModelEvents()
    object NavigateToAddItemsFragment : AddCategoryViewModelEvents()
    object SaveImagesEvent:AddCategoryViewModelEvents()
}