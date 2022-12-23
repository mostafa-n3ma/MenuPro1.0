package com.mostafan3ma.android.menupro10.presentation.fragments.viewModels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.mostafan3ma.android.menupro10.oporations.DataManagment.repository.DefaultShopRepository
import com.mostafan3ma.android.menupro10.oporations.data_Entities.Category
import com.mostafan3ma.android.menupro10.oporations.data_Entities.Item
import com.mostafan3ma.android.menupro10.oporations.data_Entities.cache_Entities.mapToCategoriesList
import com.mostafan3ma.android.menupro10.oporations.data_Entities.getCacheItem
import com.mostafan3ma.android.menupro10.oporations.di.RealRepository
import com.mostafan3ma.android.menupro10.oporations.utils.DataState
import com.mostafan3ma.android.menupro10.oporations.utils.SuperImageController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddProductsViewModel
@Inject
constructor(
    @RealRepository private val repository: DefaultShopRepository,
    val superImageController: SuperImageController
) : ViewModel() {
    companion object {
        const val TAG = "AddProductsViewModel"
    }

    val returnedImgUri = MutableLiveData<Uri?>()

    private val _productsList = MutableLiveData<List<Item>>()
    val productsList: LiveData<List<Item>> get() = _productsList

    private val _clickedProduct = MutableLiveData<Item>()
    val clickedProduct: LiveData<Item> get() = _clickedProduct

    val chosenProductCategory = MutableLiveData<String>()

    val categoryFieldHint=MutableLiveData<String>()

    var position: Int = 999

    private val _bottomSheetLaunched = MutableLiveData<Boolean>()
    val bottomSheetLaunched: LiveData<Boolean> get() = _bottomSheetLaunched

    private val _requestNameFocus = MutableLiveData<Boolean>()
    val requestNameFocus: LiveData<Boolean> get() = _requestNameFocus

    private val _hideKeyBoard = MutableLiveData<Boolean>()
    val hideKeyBoard: LiveData<Boolean> get() = _hideKeyBoard

    private val _clickableEnabled = MutableLiveData<Boolean>()
    val clickableEnabled: LiveData<Boolean> get() = _clickableEnabled

    private val _addEditBtnTxt = MutableLiveData<String>()
    val addEditBtnTxt: LiveData<String> get() = _addEditBtnTxt

    private val _savingImagesRequest = MutableLiveData<Boolean>()
    val savingImagesRequest: LiveData<Boolean> get() = _savingImagesRequest


    private val _navigateToDefaultMenuFragment = MutableLiveData<Boolean>()
    val navigateToDefaultMenuFragment: LiveData<Boolean> get() = _navigateToDefaultMenuFragment


    private val _launchImgPicker = MutableLiveData<Boolean>()
    val launchImgPicker: LiveData<Boolean> get() = _launchImgPicker


    val productName = MutableLiveData<String>()
    val productDescription = MutableLiveData<String>()
    val productPrice = MutableLiveData<String>()
    val productSize = MutableLiveData<String>()


    private val _categories_list = MutableLiveData<List<Category>>()
    val categories_list: LiveData<List<Category>> get() = _categories_list

    init {
        returnedImgUri.value = null
        _productsList.value = mutableListOf(
            Item(),
            Item(),
            Item(),
            Item(),
            Item(),
            Item()
        )
        _clickedProduct.value = Item()
        _bottomSheetLaunched.value = false
        _requestNameFocus.value = false
        _hideKeyBoard.value = false
        _clickableEnabled.value = true
        _addEditBtnTxt.value = "Add Product"
        _savingImagesRequest.value = false
        _navigateToDefaultMenuFragment.value = false
        _launchImgPicker.value = false

        productName.value = ""
        productDescription.value = ""
        productPrice.value = ""
        productSize.value = ""
        chosenProductCategory.value = ""
        categoryFieldHint.value="Category"
        viewModelScope.launch {
            repository.getCategories().onEach { dataState ->
                when (dataState) {
                    is DataState.Loading -> {
                        Log.d(TAG, "categoriesList: Loading")
                    }
                    is DataState.Success -> {
                        Log.d(TAG, "categoriesList: success >> size=${dataState.data.size}")
                        _categories_list.value = dataState.data.mapToCategoriesList()
                        dataState.data.map {
                            Log.d(TAG, "categoriesLoop: ${it.name}//${it.description}")
                        }
                    }
                    is DataState.Error -> {
                        Log.d(TAG, "categoriesList: Error:{${dataState.exception.message}}")
                    }
                }

            }.launchIn(viewModelScope)

            repository.getItems().onEach {dataState ->
            when(dataState){
                is DataState.Success->{
                    Log.d(
                        TAG,
                        "getting Items List (Success) :::: ListSize= ${dataState.data.size}: "
                    )
                    dataState.data.map {cacheItem ->
                        Log.d(TAG, "item: ${cacheItem.name}")
                    }
                }
                is DataState.Error->{
                    Log.d(TAG, "gitting ItemsList (Error): ${dataState.exception.message}")
                }
                is DataState.Loading->{
                    Log.d(TAG, "getting Items List (Loading):>>>>>>> ")
                }
            }

            }.launchIn(viewModelScope)
        }


    }


    fun setEvent(event: AddProductsViewModelEvent) {
        when (event) {
            ///////////////////////////
            ///////////////////////////
            is AddProductsViewModelEvent.ItemClicked -> {
                Log.d(TAG, "setEvent: ItemClicked")
                if (clickedProduct.value!!.category!=null){
                    categoryFieldHint.value=clickedProduct.value!!.category!!.name
                }

                setEvent(AddProductsViewModelEvent.LaunchProductBottomSheet)

            }
            ///////////////////////////
            ///////////////////////////
            is AddProductsViewModelEvent.RequestNameFocus -> {
                Log.d(TAG, "setEvent: RequestNameFocusEvent")
                _requestNameFocus.value = true
                _requestNameFocus.value = false
            }
            ///////////////////////////
            ///////////////////////////
            is AddProductsViewModelEvent.LaunchProductBottomSheet -> {
                _bottomSheetLaunched.value = true
                Log.d(TAG, "setEvent: LaunchProductBottomSheet")
                setEvent(AddProductsViewModelEvent.DisableClicks)
                setEvent(AddProductsViewModelEvent.ValidateAddButton)
                setEvent(AddProductsViewModelEvent.RequestNameFocus)
            }
            ///////////////////////////
            ///////////////////////////
            is AddProductsViewModelEvent.CancelProductBottomSheet -> {
                viewModelScope.launch {
                    delay(50)
                    _bottomSheetLaunched.value = false
                    Log.d(TAG, "setEvent: CancelProductBottomSheet")
                }
                chosenProductCategory.value=""
                categoryFieldHint.value="Category"
                arrangeProductsList()
                setEvent(AddProductsViewModelEvent.EnableClicks)
                setEvent(AddProductsViewModelEvent.HideKeyBordEvent)
            }
            ///////////////////////////
            ///////////////////////////
            is AddProductsViewModelEvent.DisableClicks -> {
                Log.d(TAG, "setEvent: DisableClicks")
                _clickableEnabled.value = false
            }
            ///////////////////////////
            ///////////////////////////
            is AddProductsViewModelEvent.EnableClicks -> {
                Log.d(TAG, "setEvent: EnableClicks")
                _clickableEnabled.value = true
            }
            ///////////////////////////
            ///////////////////////////
            is AddProductsViewModelEvent.ValidateAddButton -> {
                when (_clickedProduct.value) {
                    Item() -> {
                        _addEditBtnTxt.value = "Add Product"
                        Log.d(TAG, "setEvent: ValidateAddButton:AddProduct")
                    }
                    else -> {
                        _addEditBtnTxt.value = "Edit Product"
                        Log.d(TAG, "setEvent: ValidateAddButton:Edit Product")
                    }
                }
            }
            ///////////////////////////
            ///////////////////////////
            is AddProductsViewModelEvent.AddEditProduct -> {
                Log.d(TAG, "setEvent: AddEditProduct")
                _clickedProduct.value!!.imageName=_clickedProduct.value!!.name
                _clickedProduct.value!!.imageUri = returnedImgUri.value.toString() ?: ""
                val categoryList = _categories_list.value
                var theCategory: Category? = categoryList!!.find { category ->
                    category.name == chosenProductCategory.value
                }
                Log.d(TAG, "chosenProductCategory:${chosenProductCategory.value.toString()} ")
                Log.d(TAG, "theCategory= ${theCategory.toString()}")
                when(theCategory){
                    null->{
                        _clickedProduct.value!!.category = Category()
                    }
                    else->{
                        _clickedProduct.value!!.category = theCategory
                    }
                }

                val cacheProduct = _clickedProduct.value!!.getCacheItem(_clickedProduct.value!!)
                Log.d(TAG, "cacheItem: ${cacheProduct.toString()} ")
                val list: MutableList<Item> = _productsList.value!!.toMutableList()
                list[position] = _clickedProduct.value!!
                _productsList.value = list.toList()
                setEvent(AddProductsViewModelEvent.CancelProductBottomSheet)
            }
            ///////////////////////////
            ///////////////////////////
            is AddProductsViewModelEvent.HideKeyBordEvent -> {
                Log.d(TAG, "setEvent: HideKeyBordEvent")
                _hideKeyBoard.value = true
                _hideKeyBoard.value = false
            }
            ///////////////////////////
            ///////////////////////////
            is AddProductsViewModelEvent.NextBtnClicked -> {
                Log.d(TAG, "setEvent: NextBtnClicked")
                val cleanedList: List<Item> = getCleanedProductsList()
                Log.d(TAG, "setEvent: cleanedProductsList:size=${cleanedList.size}")
                if (cleanedList.isEmpty()) {
                    Log.d(TAG, "setEvent: cleanedProductsList : is Empty")
                    setEvent(AddProductsViewModelEvent.NavigateToDefaultLastMenuFragment)
                } else {
                    Log.d(TAG, "setEvent: cleanedProductsList: Not Empty and size is : ${cleanedList.size}")
                    cleanedList.map { product ->
                        viewModelScope.launch {
                            setEvent(AddProductsViewModelEvent.SaveImagesEvent)
                            repository.insertItem(product.getCacheItem(product))
                            Log.d(
                                TAG,
                                "setEvent: nextBtnClicked>>adding product(${product.name}to database"
                            )
                        }

                    }
                    setEvent(AddProductsViewModelEvent.NavigateToDefaultLastMenuFragment)
                }
            }
            ///////////////////////////
            ///////////////////////////
            is AddProductsViewModelEvent.SkipBtnClicked -> {
                Log.d(TAG, "setEvent: SkipBtnClicked")
                setEvent(AddProductsViewModelEvent.NavigateToDefaultLastMenuFragment)
            }
            ///////////////////////////
            ///////////////////////////
            is AddProductsViewModelEvent.NavigateToDefaultLastMenuFragment -> {
                _navigateToDefaultMenuFragment.value = true
                Log.d(TAG, "setEvent: NavigateToDefaultMenuFragment")
                _navigateToDefaultMenuFragment.value = false
            }
            ///////////////////////////
            ///////////////////////////
            is AddProductsViewModelEvent.SaveImagesEvent -> {
                _savingImagesRequest.value = true
                Log.d(TAG, "setEvent: SaveImagesEvent")
                _savingImagesRequest.value = false
            }
            is AddProductsViewModelEvent.LaunchImagePicker -> {
                _launchImgPicker.value = true
                Log.d(TAG, "setEvent: LaunchImagePicker")
                _launchImgPicker.value = false
            }
            else -> {

            }
        }
    }

    private fun arrangeProductsList() {
        var emptyProductsCount=0
        _productsList.value!!.map {product->
            when(product){
                Item()->{
                    emptyProductsCount+=1
                }else->{

                }
            }
        }
        when(emptyProductsCount){
            0->{
                val mutableProductList=_productsList.value!!.toMutableList()
                mutableProductList.add(Item())
                _productsList.value=mutableProductList.toList()
            }
        }





    }


    fun getCleanedProductsList(): List<Item> {
        var cleanedList = mutableListOf<Item>()
        _productsList.value!!.map { product ->
            when (product) {
                Item() -> {

                }
                else -> {
                    if (product.category==null){
                        product.category= Category()
                    }
                    cleanedList.add(product)
                }
            }
        }
        return cleanedList
    }

    fun getClickedProduct(position: Int) {
        _clickedProduct.value = _productsList.value!![position]
        this.position = position
    }

    fun cancelBottomSheet() {
        setEvent(AddProductsViewModelEvent.CancelProductBottomSheet)
    }

    fun addEditCategory() {
        setEvent(AddProductsViewModelEvent.AddEditProduct)
    }

    fun launchImgPicker() {
        setEvent(AddProductsViewModelEvent.LaunchImagePicker)
    }

    fun skipBtnClicked() {
        setEvent(AddProductsViewModelEvent.SkipBtnClicked)
    }

    fun nextBtnClicked() {
        setEvent(AddProductsViewModelEvent.NextBtnClicked)
    }


}


sealed class AddProductsViewModelEvent() {
    object ItemClicked : AddProductsViewModelEvent()
    object RequestNameFocus : AddProductsViewModelEvent()
    object LaunchProductBottomSheet : AddProductsViewModelEvent()
    object CancelProductBottomSheet : AddProductsViewModelEvent()
    object LaunchImagePicker : AddProductsViewModelEvent()
    object DisableClicks : AddProductsViewModelEvent()
    object EnableClicks : AddProductsViewModelEvent()
    object ValidateAddButton : AddProductsViewModelEvent()
    object AddEditProduct : AddProductsViewModelEvent()
    object HideKeyBordEvent : AddProductsViewModelEvent()
    object NextBtnClicked : AddProductsViewModelEvent()
    object SkipBtnClicked : AddProductsViewModelEvent()
    object NavigateToDefaultLastMenuFragment : AddProductsViewModelEvent()
    object SaveImagesEvent : AddProductsViewModelEvent()
}