package com.mostafan3ma.android.menupro10.presentation.fragments.viewModels

import android.util.Log
import android.view.LayoutInflater
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.android.material.chip.Chip
import com.mostafan3ma.android.menupro10.R
import com.mostafan3ma.android.menupro10.oporations.DataManagment.repository.DefaultShopRepository
import com.mostafan3ma.android.menupro10.oporations.data_Entities.Category
import com.mostafan3ma.android.menupro10.oporations.data_Entities.DomainModel
import com.mostafan3ma.android.menupro10.oporations.data_Entities.Item
import com.mostafan3ma.android.menupro10.oporations.data_Entities.cache_Entities.CacheCategory
import com.mostafan3ma.android.menupro10.oporations.data_Entities.cache_Entities.CacheItem
import com.mostafan3ma.android.menupro10.oporations.data_Entities.cache_Entities.CacheShop
import com.mostafan3ma.android.menupro10.oporations.di.RealRepository
import com.mostafan3ma.android.menupro10.oporations.utils.DataState
import com.mostafan3ma.android.menupro10.oporations.utils.SimpleDrawerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DefaultLastMenuViewModel
@Inject
constructor(@RealRepository private val repository: DefaultShopRepository) : ViewModel() {

    companion object{
        const val TAG="LastMenuViewModel"
    }

    lateinit var simpleDrawerController: SimpleDrawerController

    fun setSimpleDrawerController(drawerLayout: DrawerLayout, navController: NavController){
        simpleDrawerController= SimpleDrawerController(drawerLayout, navController)
    }

    private val _domainModel=MutableLiveData<DomainModel>()
    val domainModel:LiveData<DomainModel> get() = _domainModel

    private val _chipsList=MutableLiveData<List<String>>()
    val chipList:LiveData<List<String>>get() = _chipsList

    private fun initChipList(domainModel: DomainModel){
        val chipsStringList= mutableListOf<String>()
        domainModel.categories_list!!.map {category: Category ->
            chipsStringList.add(category.name)
        }
        if (chipsStringList.isNotEmpty()){
            _chipsList.value=chipsStringList.toList()
        }
    }

    private val _filteredProductsList=MutableLiveData<List<Item>>()
    val filteredProductsList:LiveData<List<Item>> get() = _filteredProductsList


    fun filterProductsList(chosenCategory:String){
        val allProductsList: List<Item>? =_domainModel.value!!.items
        var filteredList= mutableListOf<Item>()
        allProductsList!!.map {product->
            if (product.category!!.name ==chosenCategory){
                filteredList.add(product)
            }
        }
        _filteredProductsList.value=filteredList
    }


    init {

        viewModelScope.launch {
            repository.getCacheDomainShop().onEach {dataState: DataState<DomainModel>  ->
                when(dataState){
                    is DataState.Success->{
                        _domainModel.value=dataState.data!!
                        Log.d(TAG, "DomainModel:::${dataState.data.toString()}: ")
                        initChipList(_domainModel.value!!)
                    }
                    is DataState.Error->{
                        Log.d(TAG, "error getting DomainModel: ${dataState.exception.message}")
                    }
                    is DataState.Loading->{
                        Log.d(TAG, "getting Domain Model: Loading>>>")
                    }
                }
            }.launchIn(viewModelScope)

        }
    }


    fun setEvent(defaultViewModelEvent: DefaultViewModelEvent<Nothing>){
        when(defaultViewModelEvent){
            is DefaultViewModelEvent.OpenDrawerEvent->{
                Log.d(TAG, "setEvent: OpenDrawer")
                simpleDrawerController.openDrawer()
            }
            is DefaultViewModelEvent.CloseDrawerEvent->{
                Log.d(TAG, "setEvent: CloseDrawer")
                simpleDrawerController.closeDrawer()
            }
            is DefaultViewModelEvent.ChangeFilterEvent->{
                if (defaultViewModelEvent.filter!=""){
                    Log.d(TAG, "setEvent: Change Filter :${defaultViewModelEvent.filter}")
                    filterProductsList(defaultViewModelEvent.filter)
                }
            }
        }
    }
}

sealed class DefaultViewModelEvent<out R>{
    object OpenDrawerEvent:DefaultViewModelEvent<Nothing>()
    object CloseDrawerEvent:DefaultViewModelEvent<Nothing>()
    data class ChangeFilterEvent(val filter:String=""):DefaultViewModelEvent<Nothing>()

}

