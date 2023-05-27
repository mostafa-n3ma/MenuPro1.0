package com.mostafan3ma.android.menupro10.presentation.fragments.viewModels

import android.util.Log
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.mostafan3ma.android.menupro10.oporations.DataManagment.repository.DefaultShopRepository
import com.mostafan3ma.android.menupro10.oporations.data_Entities.Category
import com.mostafan3ma.android.menupro10.oporations.data_Entities.DomainModel
import com.mostafan3ma.android.menupro10.oporations.data_Entities.Item
import com.mostafan3ma.android.menupro10.oporations.data_Entities.cache_Entities.CacheShop
import com.mostafan3ma.android.menupro10.oporations.di.RealRepository
import com.mostafan3ma.android.menupro10.oporations.utils.*
import com.mostafan3ma.android.menupro10.presentation.fragments.adapters.StyleChooserItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DefaultLastMenuViewModel
@Inject
constructor(
    @RealRepository private val repository: DefaultShopRepository,
    val colorPickerController: ColorPickerController,
    val superImageController: SuperImageController
) : ViewModel() {

    companion object {
        const val TAG = "LastMenuViewModel"
        const val STYLE_CHOOSER_CODE = 1
        const val ITEM_STYLE_CHOOSER_CODE = 2
    }


    var onSecondPreviewItemChoice = false
    var secondPreviewItem: ProductListItem? = null


    private val _resetStyleRequired = MutableLiveData<Boolean>()
    val resetStyleRequired: LiveData<Boolean> get() = _resetStyleRequired


    val drawerItemSizeAutoValue = MutableLiveData<String>()
    val drawerRawCountValue = MutableLiveData<String>()


    val previewItemSize=MutableLiveData<String>()
    val previewItemBackground = MutableLiveData<Int>()
    val previewItemNameSize = MutableLiveData<String>()
    val previewItemNameColor = MutableLiveData<Int>()
    val previewItemDescriptionSize = MutableLiveData<String>()
    val previewItemDescriptionColor = MutableLiveData<Int>()
    val previewItemSizeTextSize = MutableLiveData<String>()
    val previewItemSizeTextColor = MutableLiveData<Int>()
    val previewItemConcurrencyType = MutableLiveData<String>()
    val previewItemConcurrencySize = MutableLiveData<String>()
    val previewItemConcurrencyColor = MutableLiveData<Int>()


    fun emptyItemPreviewValues() {
        previewItemSize.value=""
        previewItemBackground.value = 0
        previewItemNameSize.value = ""
        previewItemNameColor.value = 0
        previewItemDescriptionSize.value = ""
        previewItemDescriptionColor.value = 0
        previewItemSizeTextSize.value = ""
        previewItemSizeTextColor.value = 0
        previewItemConcurrencyType.value = ""
        previewItemConcurrencySize.value = ""
        previewItemConcurrencyColor.value = 0
    }

    fun calculateAndApplyItemPreviewValues() {
        val productListItem= when(onSecondPreviewItemChoice){
            true->{
                if (secondPreviewItem!=null){
                    secondPreviewItem
                } else {
                    _style.value!!.product_list_item
                }
            }
            false->{
                _style.value!!.product_list_item
            }
        }


        if (previewItemSize.value!=""){
            productListItem!!.item_size=previewItemSize.value!!
        }

        if (previewItemBackground.value != 0) {
            productListItem!!.background_color = previewItemBackground.value!!
        }
        if (previewItemNameSize.value != "") {
            productListItem!!.name_text_size = previewItemNameSize.value!!
        }
        if (previewItemNameColor.value != 0) {
            productListItem!!.name_text_color = previewItemNameColor.value!!
        }
        if (previewItemDescriptionSize.value != "") {
            productListItem!!.description_text_size = previewItemDescriptionSize.value!!
        }
        if (previewItemDescriptionColor.value != 0) {
            productListItem!!.description_text_color = previewItemDescriptionColor.value!!
        }

        if (previewItemConcurrencySize.value != "") {
            productListItem!!.concurrency_text_size = previewItemConcurrencySize.value!!
        }
        if (previewItemConcurrencyColor.value != 0) {
            productListItem!!.concurrency_text_Color = previewItemConcurrencyColor.value!!
        }
        if (previewItemConcurrencyType.value != "") {
            productListItem!!.concurrency_type_text = previewItemConcurrencyType.value!!
        }

        if (previewItemSizeTextSize.value != "") {
            productListItem!!.size_text_size = previewItemSizeTextSize.value!!
        }
        if (previewItemSizeTextColor.value != 0) {
            productListItem!!.size_text_color = previewItemSizeTextColor.value!!
        }

        _style.value!!.product_list_item = productListItem!!
        _style.postValue(_style.value)
        applyCustomAttr()
        emptyItemPreviewValues()
    }


    var backgroundChooserResult: Any? = null


    private val _launchColorPicker = MutableLiveData<String>()
    val launchColorPicker: LiveData<String> get() = _launchColorPicker


    private val _launchSuperImageController = MutableLiveData<Boolean>()
    val launchSuperImageController: LiveData<Boolean> get() = _launchSuperImageController

    fun launchSuperImageController() {
        setEvent(DefaultViewModelEvent.LaunchSuperImageControllerRegistrarEvent)
    }


    private val _styleChooserList = MutableLiveData<MutableList<StyleChooserItem>>()
    val styleChooserList: LiveData<MutableList<StyleChooserItem>> get() = _styleChooserList
    fun populateStyleChooserList(stylesList: MutableList<StyleChooserItem>) {
        Log.d(TAG, "populateStyleChooserList: styleChooser $stylesList")
        _styleChooserList.value = stylesList
    }

    fun adjustStyleChooserList(styleCode: Int, chooserCode: Int) {
        when (chooserCode) {
            STYLE_CHOOSER_CODE -> {
                for (styleItem in _styleChooserList.value!!) {
                    styleItem.isChecked = false
                    Log.d(TAG, "adjustStyleChooserList: styleChooser::styleItem.isChecked=false")
                }
                when (styleCode) {
                    ChosenStyle.DEFAULT.styleCode -> {
                        _styleChooserList.value!![styleCode].isChecked = true
                        _styleChooserList.postValue(_styleChooserList.value)
                        Log.d(
                            TAG,
                            "testingChooserAdapter: list adjusted after clicking($styleCode)::>> ${styleChooserList.value}"
                        )
                    }
                    ChosenStyle.BLUR_PRO.styleCode -> {
                        _styleChooserList.value!![styleCode].isChecked = true
                        _styleChooserList.postValue(_styleChooserList.value)
                        Log.d(
                            TAG,
                            "testingChooserAdapter: list adjusted after clicking($styleCode)::>> ${styleChooserList.value}"
                        )
                    }
                    ChosenStyle.BAKERY_BLACK.styleCode -> {
                        _styleChooserList.value!![styleCode].isChecked = true
                        _styleChooserList.postValue(_styleChooserList.value)
                        Log.d(
                            TAG,
                            "testingChooserAdapter: list adjusted after clicking($styleCode)::>> ${styleChooserList.value}"
                        )
                    }
                    ChosenStyle.STANDARD_MATERIAL_DETAILS_ITEMS.styleCode -> {
                        _styleChooserList.value!![styleCode].isChecked = true
                        _styleChooserList.postValue(_styleChooserList.value)
                        Log.d(
                            TAG,
                            "testingChooserAdapter: list adjusted after clicking($styleCode)::>> ${styleChooserList.value}"
                        )
                    }
                    ChosenStyle.COLORIZES_CATEGORIES_DETAILS_ITEMS.styleCode -> {
                        _styleChooserList.value!![styleCode].isChecked = true
                        _styleChooserList.postValue(_styleChooserList.value)
                        Log.d(
                            TAG,
                            "testingChooserAdapter: list adjusted after clicking($styleCode)::>> ${styleChooserList.value}"
                        )
                    }
                    ChosenStyle.CATEGORIES_LIST_SWEETS_DETAILS_ITEMS.styleCode -> {
                        _styleChooserList.value!![styleCode].isChecked = true
                        _styleChooserList.postValue(_styleChooserList.value)
                        Log.d(
                            TAG,
                            "testingChooserAdapter: list adjusted after clicking($styleCode)::>> ${styleChooserList.value}"
                        )
                    }

                }
            }
            ITEM_STYLE_CHOOSER_CODE -> {
                for (styleItem in _styleChooserList.value!!) {
                    styleItem.isChecked = false
                }
                when (styleCode) {
                    ProductListItemStyle.DEFAULT_ITEM.itemStyleCode -> {
                        _styleChooserList.value!![styleCode].isChecked = true
                        _styleChooserList.postValue(_styleChooserList.value)
                        Log.d(
                            TAG,
                            "testingChooserAdapter: list adjusted after clicking($styleCode)::>> ${styleChooserList.value}"
                        )
                    }
                    ProductListItemStyle.MATERIAL_ITEM.itemStyleCode -> {
                        _styleChooserList.value!![styleCode].isChecked = true
                        _styleChooserList.postValue(_styleChooserList.value)
                        Log.d(
                            TAG,
                            "testingChooserAdapter: list adjusted after clicking($styleCode)::>> ${styleChooserList.value}"
                        )
                    }
                    ProductListItemStyle.BAKERY_ITEM.itemStyleCode -> {
                        _styleChooserList.value!![styleCode].isChecked = true
                        _styleChooserList.postValue(_styleChooserList.value)
                        Log.d(
                            TAG,
                            "testingChooserAdapter: list adjusted after clicking($styleCode)::>> ${styleChooserList.value}"
                        )
                    }
                    ProductListItemStyle.BLUR_ITEM.itemStyleCode -> {
                        _styleChooserList.value!![styleCode].isChecked = true
                        _styleChooserList.postValue(_styleChooserList.value)
                        Log.d(
                            TAG,
                            "testingChooserAdapter: list adjusted after clicking($styleCode)::>> ${styleChooserList.value}"
                        )
                    }
                }
            }
        }

    }

    private val _hideKeyBoardRequired = MutableLiveData<Boolean>()
    val hideKeyBoardRequired: LiveData<Boolean> get() = _hideKeyBoardRequired

    var tempTextAlignValue: String = Style.ALIGN_LEFT
    fun alignTxt(alignDir: String) {
        tempTextAlignValue = alignDir
    }

    private val _bottomDoneClicked = MutableLiveData<Boolean>()
    val bottomDoneClicked: LiveData<Boolean> get() = _bottomDoneClicked


    private val _visibleBottomSheetLayout = MutableLiveData<AttrVisibleViews>()
    val visibleBottomSheetLayout: LiveData<AttrVisibleViews> get() = _visibleBottomSheetLayout

    private val _style = MutableLiveData<Style>()
    val style: LiveData<Style> get() = _style


    fun passPrefStyle(prefStyle: Style) {
        _style.value = prefStyle
    }

    fun addAttrChanges(givenStyle: Style) {
        Log.d(TAG, "styleTest/addAttrChanges: style from welcome text editor layout $givenStyle")
//        _style.value = givenStyle
        _style.postValue(givenStyle)
    }

    fun applyCustomAttr() {
        _style.value!!.attributes = Style.CUSTOM_ATTRIBUTES
        _style.postValue(_style.value)
        Log.d(TAG, "styleTest/applyCustomAttr:style.attributes=${_style.value!!.attributes} ")
    }


    private val _clicksEnabled = MutableLiveData<Boolean>()
    val clicksEnabled: LiveData<Boolean> get() = _clicksEnabled


    private val _refreshFragment = MutableLiveData<Boolean>()
    val refreshFragment: LiveData<Boolean> get() = _refreshFragment


    lateinit var simpleDrawerController: SimpleDrawerController

    fun setSimpleDrawerController(drawerLayout: DrawerLayout, navController: NavController) {
        simpleDrawerController = SimpleDrawerController(drawerLayout, navController)
    }

    private val _domainModel = MutableLiveData<DomainModel>()
    val domainModel: LiveData<DomainModel> get() = _domainModel

    private val _chipsList = MutableLiveData<List<String>>()
    val chipList: LiveData<List<String>> get() = _chipsList

    private fun initChipList(domainModel: DomainModel) {
        val chipsStringList = mutableListOf<String>()
        domainModel.categories_list!!.map { category: Category ->
            chipsStringList.add(category.name)
        }
        if (chipsStringList.isNotEmpty()) {
            _chipsList.value = chipsStringList.toList()
        }
    }

    private val _filteredProductsList = MutableLiveData<List<Item>>()
    val filteredProductsList: LiveData<List<Item>> get() = _filteredProductsList

    private var randomItemData: Item? = Item()
    fun getRandomItemData(): Item {
        return when (randomItemData) {
            null -> {
                Item(
                    name = "item title",
                    description = "item description",
                    price = "99$",
                    size = "Large"
                )
            }
            else -> {
                randomItemData!!
            }
        }
    }

    private fun filterProductsList(chosenCategory: String) {
        val allProductsList: List<Item>? = _domainModel.value!!.items
        var filteredList = mutableListOf<Item>()

        if (chosenCategory == "all") {
            filteredList = allProductsList as MutableList<Item>
        } else {
            allProductsList!!.map { product ->
                if (product.category!!.name == chosenCategory) {
                    filteredList.add(product)
                }
            }
        }
        _filteredProductsList.value = filteredList
    }


    init {


        drawerItemSizeAutoValue.value = ""
        drawerRawCountValue.value = ""



        _resetStyleRequired.value = false

        previewItemSize.value=""
        previewItemBackground.value = 0
        previewItemNameSize.value = ""
        previewItemNameColor.value = 0
        previewItemDescriptionSize.value = ""
        previewItemDescriptionColor.value = 0
        previewItemSizeTextSize.value = ""
        previewItemSizeTextColor.value = 0
        previewItemConcurrencyType.value = ""
        previewItemConcurrencySize.value = ""
        previewItemConcurrencyColor.value = 0





        _launchColorPicker.value = ""
        _launchSuperImageController.value = false
        _hideKeyBoardRequired.value = false
        _bottomDoneClicked.value = false
        _visibleBottomSheetLayout.value = AttrVisibleViews.NOTHING
        _clicksEnabled.value = true
        _refreshFragment.value = false
        randomItemData = null



        viewModelScope.launch {
            repository.getCacheDomainShop().onEach { dataState: DataState<DomainModel> ->
                when (dataState) {
                    is DataState.Success -> {
                        _domainModel.value = dataState.data!!
                        Log.d(TAG, "DomainModel:::${dataState.data.toString()}: ")
                        initChipList(_domainModel.value!!)
                        val productsListSize: Int = domainModel.value!!.items!!.size
                        if (productsListSize != 0) {
                            val randomNum = (0 until productsListSize - 1).random()
                            Log.d(TAG, "randomNum=$randomNum: productsListSize=$productsListSize")
                            randomItemData = domainModel.value!!.items!![randomNum]
                        }
                        _filteredProductsList.value = _domainModel.value!!.items!!
                    }
                    is DataState.Error -> {
                        Log.d(TAG, "error getting DomainModel: ${dataState.exception.message}")
                    }
                    is DataState.Loading -> {
                        Log.d(TAG, "getting Domain Model: Loading>>>")
                    }
                }
            }.launchIn(viewModelScope)

        }

    }


    fun openDrawer() {
        if (_clicksEnabled.value!!) {
            setEvent(DefaultViewModelEvent.OpenDrawerEvent)
        } else {
            // if clicksEnabled=false that's means the AttrBottomSheet
            // is opened and the menuBtn clicks should not be enabled so
            // the drawer wont open until the bottom sheet is closed
        }
        Log.d(TAG, "openDrawer: viewModel.openDrawer()")
    }


    fun setEvent(defaultViewModelEvent: DefaultViewModelEvent<Nothing>) {
        when (defaultViewModelEvent) {
            is DefaultViewModelEvent.OpenDrawerEvent -> {
                Log.d(TAG, "setEvent: OpenDrawer")
                simpleDrawerController.openDrawer()
            }
            is DefaultViewModelEvent.CloseDrawerEvent -> {
                Log.d(TAG, "setEvent: CloseDrawer")
                simpleDrawerController.closeDrawer()
            }
            is DefaultViewModelEvent.OpenAttributesDrawerEvent -> {
                Log.d(TAG, "setEvent: open attributes drawer")
                simpleDrawerController.openAttributesDrawer()
            }
            is DefaultViewModelEvent.CloseAttributesDrawerEvent -> {
                Log.d(TAG, "setEvent: close attributes drawer")
                simpleDrawerController.closeAttrDrawer()
            }
            is DefaultViewModelEvent.ChangeFilterEvent -> {
                if (defaultViewModelEvent.filter != "") {
                    Log.d(TAG, "setEvent: Change Filter :${defaultViewModelEvent.filter}")
                    filterProductsList(defaultViewModelEvent.filter)
                }
            }
            is DefaultViewModelEvent.RefreshFragmentEvent -> {
                Log.d(TAG, "setEvent: RefreshFragmentEvent")
                _refreshFragment.value = true
            }

            is DefaultViewModelEvent.EnableClicks -> {
                _clicksEnabled.value = true
            }
            is DefaultViewModelEvent.DisableClicks -> {
                _clicksEnabled.value = false
            }
            is DefaultViewModelEvent.OpenAttrBottomSheetEventWithView -> {
                when (defaultViewModelEvent.toBeVisible) {
                    AttrVisibleViews.NOTHING -> {
                        _visibleBottomSheetLayout.value = defaultViewModelEvent.toBeVisible
                        setEvent(DefaultViewModelEvent.OpenAttributesDrawerEvent)
                        setEvent(DefaultViewModelEvent.EnableClicks)
                        setEvent(DefaultViewModelEvent.HideKeyBoardRequired)
                        emptyItemPreviewValues()

                    }
                    else -> {
                        _visibleBottomSheetLayout.value = defaultViewModelEvent.toBeVisible
                        setEvent(DefaultViewModelEvent.CloseAttributesDrawerEvent)
                        setEvent(DefaultViewModelEvent.DisableClicks)
                    }
                }
            }


            is DefaultViewModelEvent.BottomDoneClickedEvent -> {
                _bottomDoneClicked.value = true
                _bottomDoneClicked.value = false
            }

            is DefaultViewModelEvent.HideKeyBoardRequired -> {
                _hideKeyBoardRequired.value = true
                _hideKeyBoardRequired.value = false
            }
            is DefaultViewModelEvent.LaunchSuperImageControllerRegistrarEvent -> {
                _launchSuperImageController.value = true
                _launchSuperImageController.value = false
            }
            is DefaultViewModelEvent.LaunchColorPicker -> {
                when (defaultViewModelEvent.requestCode) {
                    "" -> {

                    }
                    else -> {
                        _launchColorPicker.value = defaultViewModelEvent.requestCode
                    }
                }
            }
            is DefaultViewModelEvent.ResetStyleAttributes -> {
                _resetStyleRequired.value = true
                _resetStyleRequired.value = false
            }
        }
    }

    fun requestStyleReset() {
        setEvent(DefaultViewModelEvent.ResetStyleAttributes)
    }


    fun bottomDoneClicked() {
        setEvent(DefaultViewModelEvent.BottomDoneClickedEvent)
    }


    fun closeAttrBottomSheet() {
        setEvent(DefaultViewModelEvent.OpenAttrBottomSheetEventWithView(AttrVisibleViews.NOTHING))
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: viewModel is cleared")
    }


    var tempStyleChoice: Int = 99
    fun getTheNewChosenStyle(chosenStyleCode: Int, chooserCode: Int) {
        when (chooserCode) {
            STYLE_CHOOSER_CODE -> {
                tempStyleChoice = when (chosenStyleCode) {
                    0 -> ChosenStyle.DEFAULT.styleCode
                    1 -> ChosenStyle.BLUR_PRO.styleCode
                    2 -> ChosenStyle.BAKERY_BLACK.styleCode
                    3 -> ChosenStyle.STANDARD_MATERIAL_DETAILS_ITEMS.styleCode
                    4 -> ChosenStyle.COLORIZES_CATEGORIES_DETAILS_ITEMS.styleCode
                    5 -> ChosenStyle.CATEGORIES_LIST_SWEETS_DETAILS_ITEMS.styleCode
                    else -> ChosenStyle.DEFAULT.styleCode
                }

            }
            ITEM_STYLE_CHOOSER_CODE -> {
            }
        }

    }


    fun openItemEditorBottom() {
        setEvent(DefaultViewModelEvent.OpenAttrBottomSheetEventWithView(AttrVisibleViews.ITEM_EDITOR_LAYOUT))
    }
}


sealed class DefaultViewModelEvent<out R> {
    object OpenDrawerEvent : DefaultViewModelEvent<Nothing>()
    object CloseDrawerEvent : DefaultViewModelEvent<Nothing>()
    object OpenAttributesDrawerEvent : DefaultViewModelEvent<Nothing>()
    object CloseAttributesDrawerEvent : DefaultViewModelEvent<Nothing>()
    data class ChangeFilterEvent(val filter: String = "") : DefaultViewModelEvent<Nothing>()
    object RefreshFragmentEvent : DefaultViewModelEvent<Nothing>()
    data class OpenAttrBottomSheetEventWithView(val toBeVisible: AttrVisibleViews = AttrVisibleViews.NOTHING) :
        DefaultViewModelEvent<Nothing>()

    data class LaunchColorPicker(val requestCode: String = "") : DefaultViewModelEvent<Nothing>()
    object EnableClicks : DefaultViewModelEvent<Nothing>()
    object DisableClicks : DefaultViewModelEvent<Nothing>()
    object BottomDoneClickedEvent : DefaultViewModelEvent<Nothing>()
    object HideKeyBoardRequired : DefaultViewModelEvent<Nothing>()
    object LaunchSuperImageControllerRegistrarEvent : DefaultViewModelEvent<Nothing>()
    object ResetStyleAttributes : DefaultViewModelEvent<Nothing>()

}

enum class AttrVisibleViews() {
    NOTHING,
    STYLE_CHOOSER,
    BACKGROUND_CHOOSER,
    WELCOME_EDIT_TXT_LAYOUT,
    ITEM_EDITOR_LAYOUT
}