package com.mostafan3ma.android.menupro10.presentation.fragments

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearSnapHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.mostafan3ma.android.menupro10.R
import com.mostafan3ma.android.menupro10.databinding.*
import com.mostafan3ma.android.menupro10.oporations.data_Entities.Item
import com.mostafan3ma.android.menupro10.oporations.utils.*
import com.mostafan3ma.android.menupro10.oporations.utils.ProductListItemStyle.*
import com.mostafan3ma.android.menupro10.presentation.fragments.adapters.*
import com.mostafan3ma.android.menupro10.presentation.fragments.viewModels.AttrVisibleViews
import com.mostafan3ma.android.menupro10.presentation.fragments.viewModels.DefaultLastMenuViewModel
import com.mostafan3ma.android.menupro10.presentation.fragments.viewModels.DefaultLastMenuViewModel.Companion.ITEM_STYLE_CHOOSER_CODE
import com.mostafan3ma.android.menupro10.presentation.fragments.viewModels.DefaultLastMenuViewModel.Companion.STYLE_CHOOSER_CODE
import com.mostafan3ma.android.menupro10.presentation.fragments.viewModels.DefaultViewModelEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class DefaultLastMenuFragment
@Inject
constructor(
) : Fragment() {
    private lateinit var binding: FragmentLastDefaultMenuBinding
    private lateinit var binding2: FragmentMyAccountBinding
    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var styleChooserAdapter: StyleChooserAdapter
    private lateinit var itemStyleChooserAdapter: StyleChooserAdapter
    private lateinit var attrBottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    val viewModel: DefaultLastMenuViewModel by viewModels()

    companion object {
        const val TAG = "DefaultLastMenuFragment"
    }

    private var clicksEnabled: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: called")

        if (savedInstanceState == null) {
            val prefStyle = getPrefStyle(requireContext())
            Log.d(TAG, "styleTest/onCreate:prefStyle $prefStyle")
            viewModel.passPrefStyle(getPrefStyle(requireContext()))
        }

        productsAdapter = ProductsAdapter(ProductListener { item: Item ->
            when (clicksEnabled) {
                true -> {
                    Toast.makeText(
                        this.requireContext(),
                        "item ${item.name}  clicked>>>",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    Log.d(TAG, "onCreate: ProductsAdapter init")
                }
                false -> {
                    // don't do clicks on Adapter items
                }
            }

        }, viewModel.style.value!!.product_list_item)


    }


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return when (viewModel.style.value!!.styleCode) {
            ChosenStyle.DEFAULT -> {
                Log.d(TAG, "onCreateView: inflated Screen DefaultStyle")
                inflateDefaultMenuScreen(inflater, getPrefStyle(requireContext()))
            }
            ChosenStyle.BLUR_PRO -> {
                Log.d(TAG, "onCreateView: inflated Screen BLUR_PRO")
                inflateDefaultMenuScreen(inflater, getPrefStyle(requireContext()))
//                inflateMyAccount(inflater)
            }
            ChosenStyle.BAKERY_BLACK -> {
                Log.d(TAG, "onCreateView: inflated Screen BAKERY_BLACK")
                inflateDefaultMenuScreen(inflater, getPrefStyle(requireContext()))
            }
            ChosenStyle.STANDARD_MATERIAL_DETAILS_ITEMS -> {
                Log.d(TAG, "onCreateView: inflated Screen STANDARD_MATERIAL_DETAILS_ITEMS")
                inflateDefaultMenuScreen(inflater, getPrefStyle(requireContext()))

            }
            ChosenStyle.COLORIZES_CATEGORIES_DETAILS_ITEMS -> {
                Log.d(TAG, "onCreateView: inflated Screen COLORIZES_CATEGORIES_DETAILS_ITEMS")
                inflateDefaultMenuScreen(inflater, getPrefStyle(requireContext()))

            }
            ChosenStyle.CATEGORIES_LIST_SWEETS_DETAILS_ITEMS -> {
                Log.d(TAG, "onCreateView: inflated Screen CATEGORIES_LIST_SWEETS_DETAILS_ITEMS")
                inflateDefaultMenuScreen(inflater, getPrefStyle(requireContext()))
            }
            else -> {
                Log.d(TAG, "onCreateView: inflated Screen else(default) ")
                inflateDefaultMenuScreen(inflater, getPrefStyle(requireContext()))
            }

        }

    }

    private fun inflateMyAccount(inflater: LayoutInflater): View {
        Log.d(TAG, "inflateMyAccount: init")
        binding2 = FragmentMyAccountBinding.inflate(inflater)
        binding2.menuBbttnn.setOnClickListener {
            putPrefStyle(requireContext(), Style(styleCode = ChosenStyle.DEFAULT))
            viewModel.setEvent(DefaultViewModelEvent.RefreshFragmentEvent)
        }
        return binding2.root
    }

    private fun refreshCurrentFragment() {
        val id = findNavController().currentDestination?.id
        findNavController().popBackStack(id!!, true)
        findNavController().navigate(id)
    }

    @Inject
    lateinit var superImageController: SuperImageController

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onResume() {
        super.onResume()
        if (superImageController.returnedUri != null) {
            binding.bottomBackgroundImage.background =
                superImageController.getDrawableFromUri(
                    requireContext(),
                    superImageController.returnedUri!!
                )

            lifecycleScope.launch {
                val returnedBitmap: Bitmap =
                    superImageController.getBitmapFromRegister(requireContext())
                superImageController.saveImageToInternalStorage(
                    requireContext(), returnedBitmap, Style.TEMP_BACKGROUND_IMAGE_NAME
                )
                viewModel.backgroundChooserResult = Style.TEMP_BACKGROUND_IMAGE_NAME
            }

            superImageController.returnedUri = null
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun inflateDefaultMenuScreen(inflater: LayoutInflater, prefStyle: Style): View {
        Log.d(TAG, "inflateDefaultMenuScreen: init")

        observeStyleAttributes()

        binding = FragmentLastDefaultMenuBinding.inflate(inflater)
        binding.adapter = productsAdapter
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        attrBottomSheetBehavior = setUpAttrBottomSheet()

        superImageController.register(this)


        viewModel.setSimpleDrawerController(binding.drawerLayout, findNavController())

        subscribeObservers()


        addDrawerClickableViews()

        //later for adjusting the span count
        //binding.recyclerView.layoutManager=GridLayoutManager(requireContext(),3)


        return binding.root

    }

    private fun setUpAttrBottomSheet(): BottomSheetBehavior<LinearLayout> {
        val attrBottomSheet = BottomSheetBehavior.from(binding.attrBottomSheet!!).apply {
            isDraggable = false
        }
        return attrBottomSheet
    }

    private fun observeStyleAttributes() {
        viewModel.style.observe(viewLifecycleOwner, Observer { style ->
            Log.d(TAG, "observeStyleAttributes: style observed >>>> $style")

            when (style.styleCode) {
                ChosenStyle.DEFAULT -> {
                    binding.styleImg.setImageResource(com.mostafan3ma.android.menupro10.R.drawable.default_style)
                    binding.styleName.text = "Default Style"
                    binding.drawerItemCardImg.setImageResource(R.drawable.default_item_card)
                    binding.itemCardStyleName.text = "Default item"
                }
                ChosenStyle.BLUR_PRO -> {
                    binding.styleImg.setImageResource(com.mostafan3ma.android.menupro10.R.drawable.blure_pro)
                    binding.styleName.text = "Blur pro"
                    binding.drawerItemCardImg.setImageResource(R.drawable.blure_item_card)
                    binding.itemCardStyleName.text = "Blur item"
                }
                ChosenStyle.BAKERY_BLACK -> {
                    binding.styleImg.setImageResource(com.mostafan3ma.android.menupro10.R.drawable.bakery_black)
                    binding.styleName.text = "Bakery Black"
                    binding.drawerItemCardImg.setImageResource(R.drawable.bakery_item_card)
                    binding.itemCardStyleName.text = "Bakery item"
                }
                ChosenStyle.STANDARD_MATERIAL_DETAILS_ITEMS -> {
                    binding.styleImg.setImageResource(com.mostafan3ma.android.menupro10.R.drawable.standerd3_material)
                    binding.styleName.text = "Standard Style"
                    binding.drawerItemCardImg.setImageResource(R.drawable.material_item_card)
                    binding.itemCardStyleName.text = "Material item"
                }
                ChosenStyle.COLORIZES_CATEGORIES_DETAILS_ITEMS -> {
                    binding.styleImg.setImageResource(com.mostafan3ma.android.menupro10.R.drawable.colories_categories)
                    binding.styleName.text = "colorizes categories"
                }
                ChosenStyle.CATEGORIES_LIST_SWEETS_DETAILS_ITEMS -> {
                    binding.styleImg.setImageResource(com.mostafan3ma.android.menupro10.R.drawable.categories_sweets)
                    binding.styleName.text = "Sweet categories"
                }
                else -> {
                    binding.styleImg.setImageResource(com.mostafan3ma.android.menupro10.R.drawable.default_style)
                    binding.styleName.text = "Default Style"
                }
            }


            if (style.attributes != Style.DEFAULT_VALUE) {
                Log.d(TAG, "observeStyleAttributes: style welcome changed and observed >>> ")
                applyAttributes(style)
            }

        })


    }

    private fun applyAttributes(style: Style?) {

        when (style!!.backgroundChoice) {
            Style.BACKGROUND_IMAGE_CHOICE -> {
                if (style.mainBackgroundImage == Style.DEFAULT_VALUE) {
                    return
                }
                val backgroundBitmap: Bitmap? = superImageController.getImageFromInternalStorage(
                    requireContext(), style.mainBackgroundImage,
                    R.drawable.domy_background
                )
                val backgroundDrawable =
                    BitmapDrawable(requireContext().resources, backgroundBitmap)
                binding.mainBackground.background = backgroundDrawable
                binding.backgroundImg.background = backgroundDrawable
                binding.bottomBackgroundImage.background = backgroundDrawable


            }
            Style.BACKGROUND_COLOR_CHOICE -> {
                binding.mainBackground.setBackgroundColor(style.mainBackgroundColor!!)
                binding.backgroundImg.setBackgroundColor(style.mainBackgroundColor!!)
                binding.bottomBackgroundImage.setBackgroundColor(style.mainBackgroundColor!!)
            }
        }

        when (style?.welcomeText) {
            Style.DEFAULT_VALUE -> {}
            else -> {
                //main view
                binding.chooseMealTxt.text = style?.welcomeText
                //drawer view
                binding.drawerWelcomeTxt.text = style?.welcomeText

                //bottom View
                binding.bottomWelcomeTxtLayout.hint = style?.welcomeText
            }
        }


        when (style?.welcomeTextSize) {
            Style.DEFAULT_VALUE -> {
                //stay on the default Size
            }
            "Small" -> {
                //18
                binding.chooseMealTxt.textSize = 18F
            }
            "Medium" -> {
                //20
                binding.chooseMealTxt.textSize = 20F
            }
            "Large" -> {
                //30sp
                binding.chooseMealTxt.textSize = 30F
            }
            "X-Large" -> {
                binding.chooseMealTxt.textSize = 35F
            }
            else -> {
                //also stay on the default textSize
            }
        }


        when (style?.welcomeTextColor) {
            Style.DEFAULT_TEXT_COLOR -> {
                //don't do anyThing
            }
            else -> {
                binding.chooseMealTxt.setTextColor(style!!.welcomeTextColor)
            }

        }


        when (style?.welcomeTextAlign) {
            Style.ALIGN_LEFT -> {
                //stay on default and don't do anything
                Log.d(TAG, "applyAttributes: welcomeTxtAlignment=left")
                val constraintLayout: MotionLayout = binding.mainBackground
                val constraintSet = ConstraintSet()
                constraintSet.clone(constraintLayout)
                constraintSet.connect(
                    binding.chooseMealTxt.id,
                    ConstraintSet.START,
                    binding.mainBackground.id,
                    ConstraintSet.START
                )
                constraintSet.connect(
                    binding.chooseMealTxt.id,
                    ConstraintSet.END,
                    binding.shopLogo.id,
                    ConstraintSet.START
                )
                constraintSet.applyTo(constraintLayout)

                // apply the changes to the bottomSheet align buttons
                binding.bottomWelcomeTxtLeftAlignBtn.setImageResource(com.mostafan3ma.android.menupro10.R.drawable.left_align_yellow)
                binding.bottomWelcomeTxtRightAlignBtn.setImageResource(com.mostafan3ma.android.menupro10.R.drawable.right_align_black)
            }
            Style.ALIGN_RIGHT -> {
                Log.d(TAG, "applyAttributes: welcomeTxtAlignment=right")
                val constraintLayout: MotionLayout = binding.mainBackground
                val constraintSet = ConstraintSet()
                constraintSet.clone(constraintLayout)
                constraintSet.connect(
                    binding.chooseMealTxt.id,
                    ConstraintSet.START,
                    binding.shopLogo.id,
                    ConstraintSet.END
                )
                constraintSet.connect(
                    binding.chooseMealTxt.id,
                    ConstraintSet.END,
                    binding.mainBackground.id,
                    ConstraintSet.END
                )
                constraintSet.applyTo(constraintLayout)

                // apply the changes to the bottomSheet align btn's
                binding.bottomWelcomeTxtRightAlignBtn.setImageResource(com.mostafan3ma.android.menupro10.R.drawable.right_align_yellow)
                binding.bottomWelcomeTxtLeftAlignBtn.setImageResource(com.mostafan3ma.android.menupro10.R.drawable.left_align_black)
            }
        }

        when (style?.chipsCheckedColor) {
            Color.parseColor(
                Style.DEFAULT_CHIPS_CHECKED_COLOR
            ) -> {

            }
            else -> {
                // the main chips will already applied their color when they are first created
                // the drawer chips baton's need to  apply there colors
                binding.chipsCheckedBtn.apply {
                    setBackgroundColor(style.chipsCheckedColor)
                    setTextColor(style.chipsCheckedTextColor)
                }
            }
        }
        when(style.chipsCheckedTextColor){
            Color.parseColor(Style.DEFAULT_BLACK_COLOR)->{

            }
            else->{
                binding.drawerCheckedChipsTxt.setTextColor(style.chipsCheckedTextColor)
                binding.drawerCheckedChipsTxtColorBtn.setBackgroundColor(style.chipsCheckedTextColor)
            }
        }


        when (style?.chipsUnCheckedColor) {
            Color.parseColor(
                Style.DEFAULT_CHIPS_CHECKED_COLOR
            ) -> {

            }
            else -> {
                // the main chips will already applied their color when they are first created
                // the drawer chips baton's need to  apply there colors
                binding.chipsUncheckedBtn.apply {
                    setBackgroundColor(style.chipsUnCheckedColor)
                    setTextColor(style.chipsUnCheckedTextColor)
                }
            }
        }
        when(style.chipsUnCheckedTextColor){
            Color.parseColor(Style.DEFAULT_BLACK_COLOR)->{

            }
            else->{
                binding.drawerUncheckedChipsTxt.setTextColor(style.chipsUnCheckedTextColor)
                binding.drawerUncheckedChipsTxtColorBtn.setBackgroundColor(style.chipsUnCheckedTextColor)
            }
        }

        when (style.product_list_item.style) {
            DEFAULT_ITEM -> {
                binding.drawerItemCardImg.setImageResource(R.drawable.default_item_card)
                binding.itemCardStyleName.text = "Default item"
            }
            MATERIAL_ITEM -> {
                binding.drawerItemCardImg.setImageResource(R.drawable.material_item_card)
                binding.itemCardStyleName.text = "Material item"
            }
            BAKERY_ITEM -> {
                binding.drawerItemCardImg.setImageResource(R.drawable.bakery_item_card)
                binding.itemCardStyleName.text = "Bakery item"
            }
            BLUR_ITEM -> {
                binding.drawerItemCardImg.setImageResource(R.drawable.blure_item_card)
                binding.itemCardStyleName.text = "Blur item"
            }
            else -> {
                binding.drawerItemCardImg.setImageResource(R.drawable.default_item_card)
                binding.itemCardStyleName.text = "Default item"
            }
        }

    }


    private var welcomeTxtColorRegistry: Int? = null
    private var mainSnapHelper: LinearSnapHelper? = null
    private var itemSnapHelper: LinearSnapHelper? = null


    @RequiresApi(Build.VERSION_CODES.P)
    private fun subscribeObservers() {
        viewModel.domainModel.observe(viewLifecycleOwner, Observer { domainModel->
            when(domainModel.logoImageName){
                ""->{}
                else->{
                    try {
                        val bitmap: Bitmap? =superImageController.getImageFromInternalStorage(requireContext(),domainModel.logoImageName,R.drawable.logo)
                        binding.shopLogo.setImageBitmap(bitmap)
                    }catch (e:Exception){
                        Log.d(
                            TAG,
                            "subscribeObservers: error fetching the image because ${e.message}"
                        )
                    }
                }
            }


        })

        viewModel.launchColorPicker.observe(viewLifecycleOwner, Observer { requestCode ->
            if (requestCode != "") {
                viewModel.colorPickerController.showColorPicker(
                    requireContext(),
                    R.color.black,
                    requestCode
                )
            }

        })




        viewModel.launchSuperImageController.observe(viewLifecycleOwner, Observer { launche ->
            if (launche) {
                superImageController.launchRegistrar()
            }
        })


        viewModel.styleChooserList.observe(viewLifecycleOwner, Observer { stylesList ->
            Log.d(
                TAG,
                "initStyleChooserMainLayout: observing stylesList>> ${stylesList.toString()}"
            )


            try {
                styleChooserAdapter.submitList(stylesList)
                Log.d(TAG, "testingChooserAdapter: listObserved: $stylesList")
                styleChooserAdapter.notifyDataSetChanged()
            }catch (e:Exception){

            }

            try {
                itemStyleChooserAdapter.submitList(stylesList)
                itemStyleChooserAdapter.notifyDataSetChanged()
            }catch (e:Exception){

            }


        })

        viewModel.hideKeyBoardRequired.observe(
            viewLifecycleOwner,
            Observer { hideKeyBoardRequired ->
                if (hideKeyBoardRequired) {
                    hideKeyboard()
                }

            })

        viewModel.colorPickerController.selectedColor.observe(
            viewLifecycleOwner,
            Observer { selectedColor: Int ->
                when (viewModel.colorPickerController.requestCode) {
                    "change_background" -> {
                        if (selectedColor != 0) {
                            viewModel.backgroundChooserResult = selectedColor
                            binding.bottomBackgroundImage.setBackgroundColor(selectedColor)
                            viewModel.colorPickerController.resetSelectedColor()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "please  select a color",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    "change_welcome_text_color" -> {
                        welcomeTxtColorRegistry = selectedColor
                        binding.bottomWelcomeTxtColorCard.setCardBackgroundColor(selectedColor)
                    }

                    "change_checked_chips_color" -> {
                        val tempStyle = viewModel.style.value
                        tempStyle!!.chipsCheckedColor = selectedColor
                        binding.chipsCheckedBtn.apply {
                            setBackgroundColor(selectedColor)
                            setTextColor(tempStyle.chipsUnCheckedColor)
                        }
                        viewModel.addAttrChanges(tempStyle)
                    }
                    "change_Unchecked_chips_color" -> {
                        val tempStyle = viewModel.style.value
                        tempStyle!!.chipsUnCheckedColor = selectedColor
                        binding.chipsUncheckedBtn.apply {
                            setBackgroundColor(selectedColor)
                            setTextColor(tempStyle.chipsCheckedColor)
                        }
                        viewModel.addAttrChanges(tempStyle)
                    }

                    "chose_item_Background_Color" -> {
                        viewModel.previewItemBackground.postValue(selectedColor)
                    }
                    "chose_item_Name_color" -> {
                        viewModel.previewItemNameColor.postValue(selectedColor)
                    }
                    "chose_item_Description_color" -> {
                        viewModel.previewItemDescriptionColor.postValue(selectedColor)
                    }
                    "chose_item_Size_Text_color" -> {
                        viewModel.previewItemSizeTextColor.postValue(selectedColor)
                    }
                    "chose_item_Concurrency_color" -> {
                        viewModel.previewItemConcurrencyColor.postValue(selectedColor)
                    }

                    "drawerCheckedChipsTxtColorBtn"->{
                        val tempStyle=viewModel.style.value
                        tempStyle!!.chipsCheckedTextColor=selectedColor
                        viewModel.addAttrChanges(tempStyle)
                    }

                    "drawerUncheckedChipsTxtColorBtn"->{
                        val tempStyle=viewModel.style.value
                        tempStyle!!.chipsUnCheckedTextColor=selectedColor
                        viewModel.addAttrChanges(tempStyle)
                    }


                    else -> {
                        Log.d(
                            TAG,
                            "selected Color observer:error (els case)!!?    /selectedColor=$selectedColor"
                        )
                    }

                }

            })



        viewModel.resetStyleRequired.observe(viewLifecycleOwner, Observer {required->
            if (required){
                when (viewModel.visibleBottomSheetLayout.value) {
                    AttrVisibleViews.NOTHING -> {
                        Log.d(TAG, "subscribeObservers:reset btn Nothing//reset main style ")
                        val tempStyle=Style()
                        tempStyle.styleCode=viewModel.style.value!!.styleCode
                        viewModel.addAttrChanges(tempStyle)
                    }
                    AttrVisibleViews.STYLE_CHOOSER -> {
                        Log.d(TAG, "subscribeObservers:reset btn styleChooser//return to default style")
                        val tempStyle=Style()
                        viewModel.addAttrChanges(tempStyle)
                    }
                    AttrVisibleViews.BACKGROUND_CHOOSER -> {
                        Log.d(TAG, "subscribeObservers:reset btn BACKGROUND_CHOOSER//reset  background ")
                        val tempStyle=viewModel.style.value
                        tempStyle!!.apply {
                            backgroundChoice=Style.BACKGROUND_IMAGE_CHOICE
                            mainBackgroundImage=Style.DEFAULT_VALUE
                        }
                        viewModel.addAttrChanges(tempStyle)
                    }
                    AttrVisibleViews.WELCOME_EDIT_TXT_LAYOUT -> {
                        Log.d(TAG, "subscribeObservers:reset btn WELCOME_EDIT_TXT_LAYOUT//reset  welcome text attributes ")
                        val tempStyle=viewModel.style.value
                        tempStyle!!.apply {
                            welcomeText=Style.DEFAULT_VALUE
                            welcomeTextAlign=Style.ALIGN_LEFT
                            welcomeTextSize=Style.DEFAULT_TEXT_SIZE
                            welcomeTextColor=Style.DEFAULT_TEXT_COLOR
                        }
                        viewModel.addAttrChanges(tempStyle)
                    }
                    AttrVisibleViews.ITEM_EDITOR_LAYOUT -> {
                        Log.d(TAG, "subscribeObservers:reset btn ITEM_EDITOR_LAYOUT//reset  list item attributes ")
                        val tempItemList=ProductListItem()
                        tempItemList.style=viewModel.style.value!!.product_list_item.style
                        val tempStyle=viewModel.style.value
                        tempStyle!!.product_list_item=tempItemList
                        viewModel.addAttrChanges(tempStyle)
                    }
                    else -> {

                    }
                }
            }
        })


        viewModel.bottomDoneClicked.observe(viewLifecycleOwner, Observer { doneClicked ->
            if (doneClicked) {
                checkForNewDataChanges()
            }
        })

        viewModel.clicksEnabled.observe(viewLifecycleOwner, Observer { clicksEnabled ->
            this.clicksEnabled = clicksEnabled
        })

        viewModel.visibleBottomSheetLayout.observe(viewLifecycleOwner, Observer { visibleLayout ->
            when (visibleLayout) {
                AttrVisibleViews.NOTHING -> {
                    hideAttrBottomSheetViews()
                    Log.d(
                        TAG,
                        "subscribeObservers: Nothing to be visible so close bottomSheet and hide all  the bottom layouts"
                    )
                    deInitItemStyleChooserCard()
                    attrBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
                AttrVisibleViews.WELCOME_EDIT_TXT_LAYOUT -> {
                    binding.bottomWelcomeTxtMainLayout.visibility = View.VISIBLE
                    attrBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    initWelcomeTextMainLayout()
                }
                AttrVisibleViews.STYLE_CHOOSER -> {
                    styleChooserAdapter =
                        StyleChooserAdapter(StyleChooserListener { chosenStyleCode: Int ->
                            viewModel.adjustStyleChooserList(chosenStyleCode, STYLE_CHOOSER_CODE)
                            Log.d(
                                TAG,
                                "onCreate: testingChooserAdapter: clicking the item $chosenStyleCode"
                            )
                            viewModel.getTheNewChosenStyle(chosenStyleCode, STYLE_CHOOSER_CODE)
                        }, StyleChooserAdapter.MAIN_STYLE_VIEW_HOLDER)
                    binding.bottomStyleChooserRec.adapter = styleChooserAdapter
                    binding.bottomStyleChooserRec.scrollToPosition(viewModel.style.value!!.styleCode.styleCode)

                    //snapping the rec item to the medial of the screen
                    when (mainSnapHelper) {
                        /*
                        the snapHelper should be initiated and attached only one time
                        so if it's value is not null don't initiate it again
                        otherwise you have { FATAL EXCEPTION:An instance of OnFlingListener already set.}
                         */
                        null -> {
                            mainSnapHelper = LinearSnapHelper()
                            mainSnapHelper!!.attachToRecyclerView(binding.bottomStyleChooserRec)
                        }
                        else -> {}
                    }

                    binding.bottomStyleChooserMainLayout.visibility = View.VISIBLE
                    attrBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    viewModel.populateStyleChooserList(initialStylesChooserList(STYLE_CHOOSER_CODE))
                }
                AttrVisibleViews.BACKGROUND_CHOOSER -> {
                    binding.bottomBackgroundChooserMainLayout.visibility = View.VISIBLE
                    attrBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    binding.bottomSetMainColorBtn.setOnClickListener {
                        viewModel.setEvent(DefaultViewModelEvent.LaunchColorPicker("change_background"))
                    }
                }
                AttrVisibleViews.ITEM_EDITOR_LAYOUT -> {
                    binding.bottomListItemEditorMainLayout.visibility = View.VISIBLE
                    setUpDropDawnMenus()
                    var previewItem: Item? = null
                    viewModel.itemForPreview.observe(
                        viewLifecycleOwner,
                        Observer { itemForPreview ->
                            previewItem = when (itemForPreview) {
                                null -> {
                                    Item(
                                        name = "item title",
                                        description = "item description",
                                        price = "99$",
                                        size = "Large"
                                    )
                                }
                                else -> {
                                    itemForPreview
                                }
                            }
                        })
                    viewModel.style.observe(viewLifecycleOwner, Observer { style ->
                        addStyleAttrValues(style)
                        when (style.product_list_item.style) {
                            DEFAULT_ITEM -> {
                                var itemBinding = ItemDefaultBinding.inflate(layoutInflater)
                                itemBinding.item = previewItem
                                itemBinding.itemCard.setOnClickListener {
                                initItemStyleChooserCardAnimation()
                                }
                                addCustomAttributes(itemBinding, style)
                                subscribeItemAttrValues(itemBinding)
                                binding.itemPreviewLayout.addView(itemBinding.root)
                            }
                            MATERIAL_ITEM -> {
                                var itemBinding = ItemMaterialBinding.inflate(layoutInflater)
                                itemBinding.item = previewItem
                                itemBinding.itemCard.setOnClickListener {
                                    initItemStyleChooserCardAnimation()
                                }
                                addCustomAttributes(itemBinding, style)
                                subscribeItemAttrValues(itemBinding)
                                binding.itemPreviewLayout.addView(itemBinding.root)
                            }
                            BAKERY_ITEM -> {
                                var itemBinding = ItemBakeryBinding.inflate(layoutInflater)
                                itemBinding.item = previewItem
                                itemBinding.itemCard.setOnClickListener {
                                    initItemStyleChooserCardAnimation()

                                }
                                addCustomAttributes(itemBinding, style)
                                subscribeItemAttrValues(itemBinding)
                                binding.itemPreviewLayout.addView(itemBinding.root)
                            }
                            BLUR_ITEM -> {
                                var itemBinding = ItemBlureProBinding.inflate(layoutInflater)
                                itemBinding.item = previewItem
                                itemBinding.itemCard.setOnClickListener {
                                    initItemStyleChooserCardAnimation()

                                }
                                addCustomAttributes(itemBinding, style)
                                subscribeItemAttrValues(itemBinding)
                                binding.itemPreviewLayout.addView(itemBinding.root)

                            }
                            else -> {}
                        }
                    })


                    attrBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

                }
                else -> {
                    hideAttrBottomSheetViews()
                }
            }

        })



        viewModel.simpleDrawerController.drawerEvent.observe(
            viewLifecycleOwner,
            Observer { action ->
                when (action) {
                    "my account action" -> {
                        Toast.makeText(
                            this.requireContext(),
                            "my account action",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    "setting action" -> {
                        Toast.makeText(
                            this.requireContext(),
                            "setting action",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    "photo library option" -> {
                        Toast.makeText(
                            this.requireContext(),
                            "photo library option",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    "logout option" -> {
                        Toast.makeText(
                            this.requireContext(),
                            "logout option",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    "attributes action" -> {
                        Toast.makeText(
                            this.requireContext(),
                            "opening attr drawer",
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.setEvent(DefaultViewModelEvent.CloseDrawerEvent)
                        viewModel.setEvent(DefaultViewModelEvent.OpenAttributesDrawerEvent)
                    }
                    "click_style_img" -> {
                        Toast.makeText(
                            this.requireContext(),
                            "click_style_img",
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.setEvent(
                            DefaultViewModelEvent.OpenAttrBottomSheetEventWithView(
                                AttrVisibleViews.STYLE_CHOOSER
                            )
                        )
                    }
                    "click_background_img" -> {
                        Toast.makeText(
                            this.requireContext(),
                            "click_background_img",
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.setEvent(
                            DefaultViewModelEvent.OpenAttrBottomSheetEventWithView(
                                AttrVisibleViews.BACKGROUND_CHOOSER
                            )
                        )


                    }
                    "click_welcome_txt_edit_btn" -> {
                        Toast.makeText(
                            this.requireContext(),
                            "click_welcome_txt_edit_btn",
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.setEvent(
                            DefaultViewModelEvent.OpenAttrBottomSheetEventWithView(
                                AttrVisibleViews.WELCOME_EDIT_TXT_LAYOUT
                            )
                        )
                    }
                    "click_chips_checked_btn" -> {
                        viewModel.setEvent(DefaultViewModelEvent.LaunchColorPicker("change_checked_chips_color"))
                    }
                    "drawerCheckedChipsTxtColorBtn"->{
                        viewModel.setEvent(DefaultViewModelEvent.LaunchColorPicker("drawerCheckedChipsTxtColorBtn"))
                    }
                    "click_chips_unchecked_btn" -> {
                        viewModel.setEvent(DefaultViewModelEvent.LaunchColorPicker("change_Unchecked_chips_color"))
                    }
                    "drawerUncheckedChipsTxtColorBtn"->{
                        viewModel.setEvent(DefaultViewModelEvent.LaunchColorPicker("drawerUncheckedChipsTxtColorBtn"))

                    }
                    "click_item_card_style_img" -> {
                        Toast.makeText(
                            this.requireContext(),
                            "click_item_card_style_img",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    "click_item_card_background_auto" -> {
                        Toast.makeText(
                            this.requireContext(),
                            "click_item_card_background_auto",
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.setEvent(
                            DefaultViewModelEvent.OpenAttrBottomSheetEventWithView(
                                AttrVisibleViews.ITEM_EDITOR_LAYOUT
                            )
                        )

                    }
                    "click_item_name_size_edit" -> {
                        Toast.makeText(
                            this.requireContext(),
                            "click_item_name_size_edit",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    "click_item_name_txt_color_auto" -> {
                        Toast.makeText(
                            this.requireContext(),
                            "click_item_name_txt_color_auto",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    "click_item_description_size_edit" -> {
                        Toast.makeText(
                            this.requireContext(),
                            "click_item_description_size_edit",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    "click_item_description_txt_color_auto" -> {
                        Toast.makeText(
                            this.requireContext(),
                            "click_item_description_txt_color_auto",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    "concurrency_txt_edit" -> {
                        Toast.makeText(
                            this.requireContext(),
                            "concurrency_txt_edit",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    "click_concurrency_txt_color_auto" -> {
                        Toast.makeText(
                            this.requireContext(),
                            "click_concurrency_txt_color_auto",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    "click_save_and_apply_style_btn" -> {
                        val tempStyle = viewModel.style.value
                        if (tempStyle!!.backgroundChoice == Style.BACKGROUND_IMAGE_CHOICE) {
                            if (tempStyle!!.mainBackgroundImage == Style.TEMP_BACKGROUND_IMAGE_NAME) {
                                // fetching tempBackgroundImage from internal storage
                                val tempBackgroundImage: Bitmap? =
                                    superImageController.getImageFromInternalStorage(
                                        requireContext(),
                                        Style.TEMP_BACKGROUND_IMAGE_NAME,
                                        R.drawable.domy_background
                                    )
                                lifecycleScope.launch {
                                    // applying tempBackgroundImage to DefaultBackgroundImage
                                    superImageController.saveImageToInternalStorage(
                                        requireContext(),
                                        tempBackgroundImage!!,
                                        Style.DEFAULT_BACKGROUND_IMAGE_NAME
                                    )

                                    tempStyle.mainBackgroundImage =
                                        Style.DEFAULT_BACKGROUND_IMAGE_NAME

                                }

                            }

                        }


                        viewModel.addAttrChanges(tempStyle)
                        putPrefStyle(requireContext(), viewModel.style.value!!)
                        viewModel.setEvent(DefaultViewModelEvent.RefreshFragmentEvent)
                    }
                }
            })

        viewModel.chipList.observe(viewLifecycleOwner, Observer { chipsNamesList ->
            val chipGroup = binding.categoriesChips
            val inflater = LayoutInflater.from(chipGroup.context)
            val childrenChips = chipsNamesList.map { chipName ->
                val chip = inflater.inflate(
                    com.mostafan3ma.android.menupro10.R.layout.chip_category_item,
                    chipGroup,
                    false
                ) as Chip
                chip.text = chipName
                chip.tag = chipName
                val states = arrayOf(
                    intArrayOf(android.R.attr.state_selected),
                    intArrayOf(-android.R.attr.state_checked)
                )
                val style = viewModel.style.value
                val colors = intArrayOf(style!!.chipsCheckedColor, style.chipsUnCheckedColor)
                val colorsStateList = ColorStateList(states, colors)
                chip.chipBackgroundColor = colorsStateList
                chip.setBackgroundColor(style.chipsUnCheckedColor)
                chip.setTextColor(style.chipsCheckedTextColor)
                chip.setOnCheckedChangeListener { compoundButton, checked ->
                    when (clicksEnabled) {
                        true -> {
                            if (checked) {
                                viewModel.setEvent(DefaultViewModelEvent.ChangeFilterEvent(filter = compoundButton.tag.toString()))
                            } else {
                                chip.setTextColor(style.chipsUnCheckedTextColor)
                            }
                        }
                        false -> {
//                            the bottomSheet is opened don't filter when chips is checked
//
                        }
                    }
                }

                chip
            }
            chipGroup.removeAllViews()
            for (chip in childrenChips) {
                chipGroup.addView(chip)
            }
            chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
                if (checkedIds.isEmpty()) {
                    viewModel.setEvent(DefaultViewModelEvent.ChangeFilterEvent(filter = "all"))
                }
            }

        })

        viewModel.filteredProductsList.observe(
            viewLifecycleOwner,
            Observer { filteredProductsList ->
                productsAdapter.submitList(filteredProductsList)
                productsAdapter.notifyDataSetChanged()
            })



        viewModel.refreshFragment.observe(
            viewLifecycleOwner,
            Observer { refreshRequested: Boolean ->
                when (refreshRequested) {
                    true -> refreshCurrentFragment()
                    else -> {}
                }
            })


    }

    private fun initItemStyleChooserCardAnimation() {


        TransitionManager.beginDelayedTransition(binding.bottomItemStyleChooserCard,AutoTransition())
        binding.bottomItemStyleChooserCard.visibility=View.VISIBLE


        itemStyleChooserAdapter= StyleChooserAdapter(StyleChooserListener { chosenItemStyle->
            viewModel.adjustStyleChooserList(chosenItemStyle,
                ITEM_STYLE_CHOOSER_CODE)
            Log.d(TAG, "subscribeObservers: tesitng item chooser adapter")
            viewModel.getTheNewChosenStyle(chosenItemStyle,
                ITEM_STYLE_CHOOSER_CODE)
            binding.itemPreviewLayout.removeAllViews()
            deInitItemStyleChooserCard()
        }, StyleChooserAdapter.ITEM_STYLE_VIEW_HOLDER)
        binding.bottomItemStyleChooserRec.adapter=itemStyleChooserAdapter
        binding.bottomItemStyleChooserRec.scrollToPosition(viewModel.style.value!!.product_list_item.style.itemStyleCode)



        TransitionManager.beginDelayedTransition(binding.bottomItemPreviewCard,AutoTransition())
        binding.bottomItemPreviewCard.visibility=View.GONE

        viewModel.populateStyleChooserList(initialStylesChooserList(ITEM_STYLE_CHOOSER_CODE))


    }

    private fun deInitItemStyleChooserCard() {
        TransitionManager.beginDelayedTransition(binding.bottomItemPreviewCard,AutoTransition())
        binding.bottomItemPreviewCard.visibility=View.VISIBLE

        TransitionManager.beginDelayedTransition(binding.bottomItemStyleChooserCard,AutoTransition())
        binding.bottomItemStyleChooserCard.visibility=View.GONE
    }

    private fun subscribeItemAttrValues(itemBinding: Any) {

        when(itemBinding){
           is ItemDefaultBinding->{
               viewModel.previewItemBackground.observe(viewLifecycleOwner, Observer {
                   if (it != 0) {
                       itemBinding.itemCard.setBackgroundColor(it)
                       binding.bottomItemEditorBackgroundBtn.setBackgroundColor(it)
                   }
               })

               viewModel.previewItemNameSize.observe(viewLifecycleOwner, Observer {
                   when (it) {
                       "Small" -> {
                           itemBinding.itemName.textSize = 14F
                       }
                       "Medium" -> {
                           itemBinding.itemName.textSize = 20F
                       }
                       "Large" -> {
                           itemBinding.itemName.textSize = 24F
                       }
                       "X-Large" -> {
                           itemBinding.itemName.textSize = 30F
                       }
                       else -> {

                       }
                   }
               })
               viewModel.previewItemNameColor.observe(viewLifecycleOwner, Observer {
                   if (it != 0) {
                       itemBinding.itemName.setTextColor(it)
                       binding.bottomItemEditorNameColorBtn.setBackgroundColor(it)
                   }
               })

               viewModel.previewItemDescriptionSize.observe(viewLifecycleOwner, Observer {
                   when (it) {
                       "Small" -> {
                           itemBinding.itemDescription.textSize = 14F
                       }
                       "Medium" -> {
                           itemBinding.itemDescription.textSize = 20F
                       }
                       "Large" -> {
                           itemBinding.itemDescription.textSize = 24F
                       }
                       "X-Large" -> {
                           itemBinding.itemDescription.textSize = 30F
                       }
                       else -> {

                       }
                   }
               })
               viewModel.previewItemDescriptionColor.observe(viewLifecycleOwner, Observer {
                   if (it != 0) {
                       itemBinding.itemDescription.setTextColor(it)
                       binding.bottomItemEditorDescriptionColorBtn.setBackgroundColor(it)
                   }
               })

               viewModel.previewItemSizeTextSize.observe(viewLifecycleOwner, Observer {
                   when (it) {
                       "Small" -> {
                           itemBinding.itemSize.textSize = 14F
                       }
                       "Medium" -> {
                           itemBinding.itemSize.textSize = 20F
                       }
                       "Large" -> {
                           itemBinding.itemSize.textSize = 24F
                       }
                       "X-Large" -> {
                           itemBinding.itemSize.textSize = 30F
                       }
                       else -> {

                       }
                   }
               })
               viewModel.previewItemSizeTextColor.observe(viewLifecycleOwner, Observer {
                   if (it != 0) {
                       itemBinding.itemSize.setTextColor(it)
                       binding.bottomItemEditorSizeTxtColorBtn.setBackgroundColor(it)
                   }
               })

               viewModel.previewItemConcurrencySize.observe(viewLifecycleOwner, Observer {
                   when (it) {
                       "Small" -> {
                           itemBinding.itemPrice.textSize = 14F
                       }
                       "Medium" -> {
                           itemBinding.itemPrice.textSize = 20F
                       }
                       "Large" -> {
                           itemBinding.itemPrice.textSize = 24F
                       }
                       "X-Large" -> {
                           itemBinding.itemPrice.textSize = 30F
                       }
                       else -> {

                       }
                   }
               })
               viewModel.previewItemConcurrencyColor.observe(viewLifecycleOwner, Observer {
                   if (it != 0) {
                       itemBinding.itemPrice.setTextColor(it)
                       binding.bottomItemEditorConcurrencyColorBtn.setBackgroundColor(it)
                   }
               })


               // setting the listeners for the color buttons
               binding.bottomItemEditorBackgroundBtn.setOnClickListener {
                   viewModel.setEvent(DefaultViewModelEvent.LaunchColorPicker("chose_item_Background_Color"))
               }
               binding.bottomItemEditorNameColorBtn.setOnClickListener {
                   viewModel.setEvent(DefaultViewModelEvent.LaunchColorPicker("chose_item_Name_color"))
               }
               binding.bottomItemEditorDescriptionColorBtn.setOnClickListener {
                   viewModel.setEvent(DefaultViewModelEvent.LaunchColorPicker("chose_item_Description_color"))
               }
               binding.bottomItemEditorSizeTxtColorBtn.setOnClickListener {
                   viewModel.setEvent(DefaultViewModelEvent.LaunchColorPicker("chose_item_Size_Text_color"))
               }
               binding.bottomItemEditorConcurrencyColorBtn.setOnClickListener {
                   viewModel.setEvent(DefaultViewModelEvent.LaunchColorPicker("chose_item_Concurrency_color"))
               }

           }
            is ItemMaterialBinding->{
                viewModel.previewItemBackground.observe(viewLifecycleOwner, Observer {
                    if (it != 0) {
                        itemBinding.itemCard.setBackgroundColor(it)
                        binding.bottomItemEditorBackgroundBtn.setBackgroundColor(it)
                    }
                })

                viewModel.previewItemNameSize.observe(viewLifecycleOwner, Observer {
                    when (it) {
                        "Small" -> {
                            itemBinding.itemName.textSize = 14F
                        }
                        "Medium" -> {
                            itemBinding.itemName.textSize = 20F
                        }
                        "Large" -> {
                            itemBinding.itemName.textSize = 24F
                        }
                        "X-Large" -> {
                            itemBinding.itemName.textSize = 30F
                        }
                        else -> {

                        }
                    }
                })
                viewModel.previewItemNameColor.observe(viewLifecycleOwner, Observer {
                    if (it != 0) {
                        itemBinding.itemName.setTextColor(it)
                        binding.bottomItemEditorNameColorBtn.setBackgroundColor(it)
                    }
                })


                viewModel.previewItemSizeTextSize.observe(viewLifecycleOwner, Observer {
                    when (it) {
                        "Small" -> {
                            itemBinding.itemSize.textSize = 14F
                        }
                        "Medium" -> {
                            itemBinding.itemSize.textSize = 20F
                        }
                        "Large" -> {
                            itemBinding.itemSize.textSize = 24F
                        }
                        "X-Large" -> {
                            itemBinding.itemSize.textSize = 30F
                        }
                        else -> {

                        }
                    }
                })
                viewModel.previewItemSizeTextColor.observe(viewLifecycleOwner, Observer {
                    if (it != 0) {
                        itemBinding.itemSize.setTextColor(it)
                        binding.bottomItemEditorSizeTxtColorBtn.setBackgroundColor(it)
                    }
                })

                viewModel.previewItemConcurrencySize.observe(viewLifecycleOwner, Observer {
                    when (it) {
                        "Small" -> {
                            itemBinding.itemPrice.textSize = 14F
                        }
                        "Medium" -> {
                            itemBinding.itemPrice.textSize = 20F
                        }
                        "Large" -> {
                            itemBinding.itemPrice.textSize = 24F
                        }
                        "X-Large" -> {
                            itemBinding.itemPrice.textSize = 30F
                        }
                        else -> {

                        }
                    }
                })
                viewModel.previewItemConcurrencyColor.observe(viewLifecycleOwner, Observer {
                    if (it != 0) {
                        itemBinding.itemPrice.setTextColor(it)
                        binding.bottomItemEditorConcurrencyColorBtn.setBackgroundColor(it)
                    }
                })


                // setting the listeners for the color buttons
                binding.bottomItemEditorBackgroundBtn.setOnClickListener {
                    viewModel.setEvent(DefaultViewModelEvent.LaunchColorPicker("chose_item_Background_Color"))
                }
                binding.bottomItemEditorNameColorBtn.setOnClickListener {
                    viewModel.setEvent(DefaultViewModelEvent.LaunchColorPicker("chose_item_Name_color"))
                }
                binding.bottomItemEditorDescriptionColorBtn.setOnClickListener {
                    viewModel.setEvent(DefaultViewModelEvent.LaunchColorPicker("chose_item_Description_color"))
                }
                binding.bottomItemEditorSizeTxtColorBtn.setOnClickListener {
                    viewModel.setEvent(DefaultViewModelEvent.LaunchColorPicker("chose_item_Size_Text_color"))
                }
                binding.bottomItemEditorConcurrencyColorBtn.setOnClickListener {
                    viewModel.setEvent(DefaultViewModelEvent.LaunchColorPicker("chose_item_Concurrency_color"))
                }

            }
            is ItemBakeryBinding->{
                viewModel.previewItemBackground.observe(viewLifecycleOwner, Observer {
                    if (it != 0) {
                        itemBinding.itemCard.setBackgroundColor(it)
                        binding.bottomItemEditorBackgroundBtn.setBackgroundColor(it)
                    }
                })

                viewModel.previewItemNameSize.observe(viewLifecycleOwner, Observer {
                    when (it) {
                        "Small" -> {
                            itemBinding.itemName.textSize = 14F
                        }
                        "Medium" -> {
                            itemBinding.itemName.textSize = 20F
                        }
                        "Large" -> {
                            itemBinding.itemName.textSize = 24F
                        }
                        "X-Large" -> {
                            itemBinding.itemName.textSize = 30F
                        }
                        else -> {

                        }
                    }
                })
                viewModel.previewItemNameColor.observe(viewLifecycleOwner, Observer {
                    if (it != 0) {
                        itemBinding.itemName.setTextColor(it)
                        binding.bottomItemEditorNameColorBtn.setBackgroundColor(it)
                    }
                })


                viewModel.previewItemSizeTextSize.observe(viewLifecycleOwner, Observer {
                    when (it) {
                        "Small" -> {
                            itemBinding.itemSize.textSize = 14F
                        }
                        "Medium" -> {
                            itemBinding.itemSize.textSize = 20F
                        }
                        "Large" -> {
                            itemBinding.itemSize.textSize = 24F
                        }
                        "X-Large" -> {
                            itemBinding.itemSize.textSize = 30F
                        }
                        else -> {

                        }
                    }
                })
                viewModel.previewItemSizeTextColor.observe(viewLifecycleOwner, Observer {
                    if (it != 0) {
                        itemBinding.itemSize.setTextColor(it)
                        binding.bottomItemEditorSizeTxtColorBtn.setBackgroundColor(it)
                    }
                })

                viewModel.previewItemConcurrencySize.observe(viewLifecycleOwner, Observer {
                    when (it) {
                        "Small" -> {
                            itemBinding.itemPrice.textSize = 14F
                        }
                        "Medium" -> {
                            itemBinding.itemPrice.textSize = 20F
                        }
                        "Large" -> {
                            itemBinding.itemPrice.textSize = 24F
                        }
                        "X-Large" -> {
                            itemBinding.itemPrice.textSize = 30F
                        }
                        else -> {

                        }
                    }
                })
                viewModel.previewItemConcurrencyColor.observe(viewLifecycleOwner, Observer {
                    if (it != 0) {
                        itemBinding.itemPrice.setTextColor(it)
                        binding.bottomItemEditorConcurrencyColorBtn.setBackgroundColor(it)
                    }
                })


                // setting the listeners for the color buttons
                binding.bottomItemEditorBackgroundBtn.setOnClickListener {
                    viewModel.setEvent(DefaultViewModelEvent.LaunchColorPicker("chose_item_Background_Color"))
                }
                binding.bottomItemEditorNameColorBtn.setOnClickListener {
                    viewModel.setEvent(DefaultViewModelEvent.LaunchColorPicker("chose_item_Name_color"))
                }
                binding.bottomItemEditorDescriptionColorBtn.setOnClickListener {
                    viewModel.setEvent(DefaultViewModelEvent.LaunchColorPicker("chose_item_Description_color"))
                }
                binding.bottomItemEditorSizeTxtColorBtn.setOnClickListener {
                    viewModel.setEvent(DefaultViewModelEvent.LaunchColorPicker("chose_item_Size_Text_color"))
                }
                binding.bottomItemEditorConcurrencyColorBtn.setOnClickListener {
                    viewModel.setEvent(DefaultViewModelEvent.LaunchColorPicker("chose_item_Concurrency_color"))
                }

            }
            is ItemBlureProBinding->{
                viewModel.previewItemBackground.observe(viewLifecycleOwner, Observer {
                    if (it != 0) {
                        itemBinding.itemCard.setBackgroundColor(it)
                        binding.bottomItemEditorBackgroundBtn.setBackgroundColor(it)
                    }
                })

                viewModel.previewItemNameSize.observe(viewLifecycleOwner, Observer {
                    when (it) {
                        "Small" -> {
                            itemBinding.itemName.textSize = 14F
                        }
                        "Medium" -> {
                            itemBinding.itemName.textSize = 20F
                        }
                        "Large" -> {
                            itemBinding.itemName.textSize = 24F
                        }
                        "X-Large" -> {
                            itemBinding.itemName.textSize = 30F
                        }
                        else -> {

                        }
                    }
                })
                viewModel.previewItemNameColor.observe(viewLifecycleOwner, Observer {
                    if (it != 0) {
                        itemBinding.itemName.setTextColor(it)
                        binding.bottomItemEditorNameColorBtn.setBackgroundColor(it)
                    }
                })

                viewModel.previewItemDescriptionSize.observe(viewLifecycleOwner, Observer {
                    when (it) {
                        "Small" -> {
                            itemBinding.itemDescription.textSize = 14F
                        }
                        "Medium" -> {
                            itemBinding.itemDescription.textSize = 20F
                        }
                        "Large" -> {
                            itemBinding.itemDescription.textSize = 24F
                        }
                        "X-Large" -> {
                            itemBinding.itemDescription.textSize = 30F
                        }
                        else -> {

                        }
                    }
                })
                viewModel.previewItemDescriptionColor.observe(viewLifecycleOwner, Observer {
                    if (it != 0) {
                        itemBinding.itemDescription.setTextColor(it)
                        binding.bottomItemEditorDescriptionColorBtn.setBackgroundColor(it)
                    }
                })

                viewModel.previewItemSizeTextSize.observe(viewLifecycleOwner, Observer {
                    when (it) {
                        "Small" -> {
                            itemBinding.itemSize.textSize = 14F
                        }
                        "Medium" -> {
                            itemBinding.itemSize.textSize = 20F
                        }
                        "Large" -> {
                            itemBinding.itemSize.textSize = 24F
                        }
                        "X-Large" -> {
                            itemBinding.itemSize.textSize = 30F
                        }
                        else -> {

                        }
                    }
                })
                viewModel.previewItemSizeTextColor.observe(viewLifecycleOwner, Observer {
                    if (it != 0) {
                        itemBinding.itemSize.setTextColor(it)
                        binding.bottomItemEditorSizeTxtColorBtn.setBackgroundColor(it)
                    }
                })

                viewModel.previewItemConcurrencySize.observe(viewLifecycleOwner, Observer {
                    when (it) {
                        "Small" -> {
                            itemBinding.itemPrice.textSize = 14F
                        }
                        "Medium" -> {
                            itemBinding.itemPrice.textSize = 20F
                        }
                        "Large" -> {
                            itemBinding.itemPrice.textSize = 24F
                        }
                        "X-Large" -> {
                            itemBinding.itemPrice.textSize = 30F
                        }
                        else -> {

                        }
                    }
                })
                viewModel.previewItemConcurrencyColor.observe(viewLifecycleOwner, Observer {
                    if (it != 0) {
                        itemBinding.itemPrice.setTextColor(it)
                        binding.bottomItemEditorConcurrencyColorBtn.setBackgroundColor(it)
                    }
                })


                // setting the listeners for the color buttons
                binding.bottomItemEditorBackgroundBtn.setOnClickListener {
                    viewModel.setEvent(DefaultViewModelEvent.LaunchColorPicker("chose_item_Background_Color"))
                }
                binding.bottomItemEditorNameColorBtn.setOnClickListener {
                    viewModel.setEvent(DefaultViewModelEvent.LaunchColorPicker("chose_item_Name_color"))
                }
                binding.bottomItemEditorDescriptionColorBtn.setOnClickListener {
                    viewModel.setEvent(DefaultViewModelEvent.LaunchColorPicker("chose_item_Description_color"))
                }
                binding.bottomItemEditorSizeTxtColorBtn.setOnClickListener {
                    viewModel.setEvent(DefaultViewModelEvent.LaunchColorPicker("chose_item_Size_Text_color"))
                }
                binding.bottomItemEditorConcurrencyColorBtn.setOnClickListener {
                    viewModel.setEvent(DefaultViewModelEvent.LaunchColorPicker("chose_item_Concurrency_color"))
                }

            }



        }


    }

    private fun addStyleAttrValues(style: Style?) {
        binding.bottomItemEditorBackgroundBtn.setBackgroundColor(style!!.product_list_item.background_color)

        binding.bottomItemEditorNameInputField.hint = style.product_list_item.name_text_size
        binding.bottomItemEditorNameColorBtn.setBackgroundColor(style.product_list_item.name_text_color)

        binding.bottomItemEditorDescriptionInputField.hint =
            style.product_list_item.description_text_size
        binding.bottomItemEditorDescriptionColorBtn.setBackgroundColor(style.product_list_item.description_text_color)

        binding.bottomItemEditorSizeInputField.hint = style.product_list_item.size_text_size
        binding.bottomItemEditorSizeTxtColorBtn.setBackgroundColor(style.product_list_item.size_text_color)

        binding.bottomItemEditorConcurrencyTextInputField.hint =
            style.product_list_item.concurrency_type_text
        binding.bottomItemEditorConcurrencySizeInputField.hint =
            style.product_list_item.concurrency_text_size
        binding.bottomItemEditorConcurrencyColorBtn.setBackgroundColor(style.product_list_item.concurrency_text_Color)


    }

    private fun setUpDropDawnMenus() {
        val sizes = resources.getStringArray(R.array.sizes)
        val sizesAdapter = ArrayAdapter(requireContext(), R.layout.drop_dwon_item, sizes)
        binding.bottomItemEditorNameSizeAuto.setAdapter(sizesAdapter)
        binding.bottomItemEditorDescriptionSizeAuto.setAdapter(sizesAdapter)
        binding.bottomItemEditorConcurrencySizeAuto.setAdapter(sizesAdapter)
        binding.bottomItemEditorSizeAuto.setAdapter(sizesAdapter)
    }

    private fun addCustomAttributes(itemBinding: Any, style: Style) {
        when (itemBinding) {
            is ItemDefaultBinding -> {
                itemBinding.apply {
                    if (style.product_list_item.background_color!=ProductListItem.DEFAULT_COLOR_VALUE){
                        itemCard.setCardBackgroundColor(style.product_list_item.background_color)
                    }

                    if (style.product_list_item.name_text_size!=ProductListItem.DEFAULT_SIZE){
                        itemName.textSize = when (style.product_list_item.name_text_size) {
                            "Small" -> {
                                14F
                            }
                            "Medium" -> {
                                20F
                            }
                            "Large" -> {
                                24F
                            }
                            "X-Large" -> {
                                30F
                            }
                            else -> {
                                20F
                            }
                        }
                    }

                    if (style.product_list_item.name_text_color!=ProductListItem.DEFAULT_COLOR_VALUE){
                        itemName.setTextColor(style.product_list_item.name_text_color)
                    }

                    if (style.product_list_item.description_text_size!=ProductListItem.DEFAULT_SIZE){
                        itemDescription.textSize = when (style.product_list_item.description_text_size) {
                            "Small" -> {
                                14F
                            }
                            "Medium" -> {
                                20F
                            }
                            "Large" -> {
                                24F
                            }
                            "X-Large" -> {
                                30F
                            }
                            else -> {
                                20F
                            }
                        }
                    }
                    if (style.product_list_item.description_text_color!=ProductListItem.DEFAULT_COLOR_VALUE){
                        itemDescription.setTextColor(style.product_list_item.description_text_color)
                    }

                    if (style.product_list_item.concurrency_text_size!=ProductListItem.DEFAULT_SIZE){
                        itemPrice.textSize = when (style.product_list_item.concurrency_text_size) {
                            "Small" -> {
                                14F
                            }
                            "Medium" -> {
                                20F
                            }
                            "Large" -> {
                                24F
                            }
                            "X-Large" -> {
                                30F
                            }
                            else -> {
                                20F
                            }
                        }
                    }
                    if (style.product_list_item.concurrency_text_Color!=ProductListItem.DEFAULT_COLOR_VALUE){
                        itemPrice.setTextColor(style.product_list_item.concurrency_text_Color)
                    }

//                    item!!.price="${item!!.price} ${style.product_list_item.concurrency_type_text}"
                    if (style.product_list_item.size_text_size!=ProductListItem.DEFAULT_SIZE){
                        itemSize.textSize = when (style.product_list_item.size_text_size) {
                            "Small" -> {
                                14F
                            }
                            "Medium" -> {
                                20F
                            }
                            "Large" -> {
                                24F
                            }
                            "X-Large" -> {
                                30F
                            }
                            else -> {
                                20F
                            }
                        }
                    }
                    if (style.product_list_item.size_text_color!=ProductListItem.DEFAULT_COLOR_VALUE){
                        itemSize.setTextColor(style.product_list_item.size_text_color)
                    }
                }
            }
            is ItemMaterialBinding -> {
                itemBinding.apply {
                    if (style.product_list_item.background_color!=ProductListItem.DEFAULT_COLOR_VALUE){
                        itemCard.setCardBackgroundColor(style.product_list_item.background_color)
                    }

                    if (style.product_list_item.name_text_size!=ProductListItem.DEFAULT_SIZE){
                        itemName.textSize = when (style.product_list_item.name_text_size) {
                            "Small" -> {
                                14F
                            }
                            "Medium" -> {
                                20F
                            }
                            "Large" -> {
                                24F
                            }
                            "X-Large" -> {
                                30F
                            }
                            else -> {
                                20F
                            }
                        }
                    }

                    if (style.product_list_item.name_text_color!=ProductListItem.DEFAULT_COLOR_VALUE){
                        itemName.setTextColor(style.product_list_item.name_text_color)
                    }



                    if (style.product_list_item.concurrency_text_size!=ProductListItem.DEFAULT_SIZE){
                        itemPrice.textSize = when (style.product_list_item.concurrency_text_size) {
                            "Small" -> {
                                14F
                            }
                            "Medium" -> {
                                20F
                            }
                            "Large" -> {
                                24F
                            }
                            "X-Large" -> {
                                30F
                            }
                            else -> {
                                20F
                            }
                        }
                    }
                    if (style.product_list_item.concurrency_text_Color!=ProductListItem.DEFAULT_COLOR_VALUE){
                        itemPrice.setTextColor(style.product_list_item.concurrency_text_Color)
                    }

//                    item!!.price="${item!!.price} ${style.product_list_item.concurrency_type_text}"
                    if (style.product_list_item.size_text_size!=ProductListItem.DEFAULT_SIZE){
                        itemSize.textSize = when (style.product_list_item.size_text_size) {
                            "Small" -> {
                                14F
                            }
                            "Medium" -> {
                                20F
                            }
                            "Large" -> {
                                24F
                            }
                            "X-Large" -> {
                                30F
                            }
                            else -> {
                                20F
                            }
                        }
                    }
                    if (style.product_list_item.size_text_color!=ProductListItem.DEFAULT_COLOR_VALUE){
                        itemSize.setTextColor(style.product_list_item.size_text_color)
                    }
                }
            }
            is ItemBakeryBinding -> {
                itemBinding.apply {
                    if (style.product_list_item.background_color!=ProductListItem.DEFAULT_COLOR_VALUE){
                        itemCard.setCardBackgroundColor(style.product_list_item.background_color)
                    }

                    if (style.product_list_item.name_text_size!=ProductListItem.DEFAULT_SIZE){
                        itemName.textSize = when (style.product_list_item.name_text_size) {
                            "Small" -> {
                                14F
                            }
                            "Medium" -> {
                                20F
                            }
                            "Large" -> {
                                24F
                            }
                            "X-Large" -> {
                                30F
                            }
                            else -> {
                                20F
                            }
                        }
                    }

                    if (style.product_list_item.name_text_color!=ProductListItem.DEFAULT_COLOR_VALUE){
                        itemName.setTextColor(style.product_list_item.name_text_color)
                    }



                    if (style.product_list_item.concurrency_text_size!=ProductListItem.DEFAULT_SIZE){
                        itemPrice.textSize = when (style.product_list_item.concurrency_text_size) {
                            "Small" -> {
                                14F
                            }
                            "Medium" -> {
                                20F
                            }
                            "Large" -> {
                                24F
                            }
                            "X-Large" -> {
                                30F
                            }
                            else -> {
                                20F
                            }
                        }
                    }
                    if (style.product_list_item.concurrency_text_Color!=ProductListItem.DEFAULT_COLOR_VALUE){
                        itemPrice.setTextColor(style.product_list_item.concurrency_text_Color)
                    }

//                    item!!.price="${item!!.price} ${style.product_list_item.concurrency_type_text}"
                    if (style.product_list_item.size_text_size!=ProductListItem.DEFAULT_SIZE){
                        itemSize.textSize = when (style.product_list_item.size_text_size) {
                            "Small" -> {
                                14F
                            }
                            "Medium" -> {
                                20F
                            }
                            "Large" -> {
                                24F
                            }
                            "X-Large" -> {
                                30F
                            }
                            else -> {
                                20F
                            }
                        }
                    }
                    if (style.product_list_item.size_text_color!=ProductListItem.DEFAULT_COLOR_VALUE){
                        itemSize.setTextColor(style.product_list_item.size_text_color)
                    }
                }
            }
            is ItemBlureProBinding -> {
                itemBinding.apply {
                    if (style.product_list_item.background_color!=ProductListItem.DEFAULT_COLOR_VALUE){
                        itemCard.setCardBackgroundColor(style.product_list_item.background_color)
                    }

                    if (style.product_list_item.name_text_size!=ProductListItem.DEFAULT_SIZE){
                        itemName.textSize = when (style.product_list_item.name_text_size) {
                            "Small" -> {
                                14F
                            }
                            "Medium" -> {
                                20F
                            }
                            "Large" -> {
                                24F
                            }
                            "X-Large" -> {
                                30F
                            }
                            else -> {
                                20F
                            }
                        }
                    }

                    if (style.product_list_item.name_text_color!=ProductListItem.DEFAULT_COLOR_VALUE){
                        itemName.setTextColor(style.product_list_item.name_text_color)
                    }

                    if (style.product_list_item.description_text_size!=ProductListItem.DEFAULT_SIZE){
                        itemDescription.textSize = when (style.product_list_item.description_text_size) {
                            "Small" -> {
                                14F
                            }
                            "Medium" -> {
                                20F
                            }
                            "Large" -> {
                                24F
                            }
                            "X-Large" -> {
                                30F
                            }
                            else -> {
                                20F
                            }
                        }
                    }
                    if (style.product_list_item.description_text_color!=ProductListItem.DEFAULT_COLOR_VALUE){
                        itemDescription.setTextColor(style.product_list_item.description_text_color)
                    }

                    if (style.product_list_item.concurrency_text_size!=ProductListItem.DEFAULT_SIZE){
                        itemPrice.textSize = when (style.product_list_item.concurrency_text_size) {
                            "Small" -> {
                                14F
                            }
                            "Medium" -> {
                                20F
                            }
                            "Large" -> {
                                24F
                            }
                            "X-Large" -> {
                                30F
                            }
                            else -> {
                                20F
                            }
                        }
                    }
                    if (style.product_list_item.concurrency_text_Color!=ProductListItem.DEFAULT_COLOR_VALUE){
                        itemPrice.setTextColor(style.product_list_item.concurrency_text_Color)
                    }

//                    item!!.price="${item!!.price} ${style.product_list_item.concurrency_type_text}"
                    if (style.product_list_item.size_text_size!=ProductListItem.DEFAULT_SIZE){
                        itemSize.textSize = when (style.product_list_item.size_text_size) {
                            "Small" -> {
                                14F
                            }
                            "Medium" -> {
                                20F
                            }
                            "Large" -> {
                                24F
                            }
                            "X-Large" -> {
                                30F
                            }
                            else -> {
                                20F
                            }
                        }
                    }
                    if (style.product_list_item.size_text_color!=ProductListItem.DEFAULT_COLOR_VALUE){
                        itemSize.setTextColor(style.product_list_item.size_text_color)
                    }
                }
            }
            else -> {

            }
        }

    }


    private fun initialStylesChooserList(styleChooserRequestCode:Int): MutableList<StyleChooserItem> {
        return when (styleChooserRequestCode) {
            STYLE_CHOOSER_CODE -> {
                val stylesList = mutableListOf<StyleChooserItem>(
                    StyleChooserItem(
                        "Default Style",
                        ContextCompat.getDrawable(
                            requireContext(),
                            com.mostafan3ma.android.menupro10.R.drawable.default_style
                        ),
                        false,
                        ChosenStyle.DEFAULT.styleCode
                    ),
                    StyleChooserItem(
                        "blur pro",
                        ContextCompat.getDrawable(
                            requireContext(),
                            com.mostafan3ma.android.menupro10.R.drawable.blure_pro
                        ),
                        false,
                        ChosenStyle.BLUR_PRO.styleCode
                    ),

                    StyleChooserItem(
                        "Bakery shop",
                        ContextCompat.getDrawable(
                            requireContext(),
                            com.mostafan3ma.android.menupro10.R.drawable.bakery_black
                        ),
                        false,
                        ChosenStyle.BAKERY_BLACK.styleCode
                    ),
                    StyleChooserItem(
                        "standard menu",
                        ContextCompat.getDrawable(
                            requireContext(),
                            com.mostafan3ma.android.menupro10.R.drawable.standerd3_material
                        ),
                        false,
                        ChosenStyle.STANDARD_MATERIAL_DETAILS_ITEMS.styleCode
                    ),
                    StyleChooserItem(
                        "Color categories",
                        ContextCompat.getDrawable(
                            requireContext(),
                            com.mostafan3ma.android.menupro10.R.drawable.colories_categories
                        ),
                        false,
                        ChosenStyle.COLORIZES_CATEGORIES_DETAILS_ITEMS.styleCode
                    ),
                    StyleChooserItem(
                        "sweets categories",
                        ContextCompat.getDrawable(
                            requireContext(),
                            com.mostafan3ma.android.menupro10.R.drawable.categories_sweets
                        ),
                        false,
                        ChosenStyle.CATEGORIES_LIST_SWEETS_DETAILS_ITEMS.styleCode
                    )

                )
                when (viewModel.style.value!!.styleCode) {
                    ChosenStyle.DEFAULT -> stylesList[ChosenStyle.DEFAULT.styleCode].isChecked =
                        true
                    ChosenStyle.BLUR_PRO -> stylesList[ChosenStyle.BLUR_PRO.styleCode].isChecked =
                        true
                    ChosenStyle.BAKERY_BLACK -> stylesList[ChosenStyle.BAKERY_BLACK.styleCode].isChecked =
                        true
                    ChosenStyle.STANDARD_MATERIAL_DETAILS_ITEMS -> stylesList[ChosenStyle.STANDARD_MATERIAL_DETAILS_ITEMS.styleCode].isChecked =
                        true
                    ChosenStyle.COLORIZES_CATEGORIES_DETAILS_ITEMS -> stylesList[ChosenStyle.COLORIZES_CATEGORIES_DETAILS_ITEMS.styleCode].isChecked =
                        true
                    ChosenStyle.CATEGORIES_LIST_SWEETS_DETAILS_ITEMS -> stylesList[ChosenStyle.CATEGORIES_LIST_SWEETS_DETAILS_ITEMS.styleCode].isChecked =
                        true
                }

                return stylesList
            }
            ITEM_STYLE_CHOOSER_CODE -> {
                val itemStylesList = mutableListOf<StyleChooserItem>(
                    StyleChooserItem(
                        "Default Item", ContextCompat.getDrawable(
                            requireContext(), R.drawable.default_item_card
                        ), false, ProductListItemStyle.DEFAULT_ITEM.itemStyleCode
                    ), StyleChooserItem(
                        "Material Item", ContextCompat.getDrawable(
                            requireContext(), R.drawable.material_item_card
                        ), false, ProductListItemStyle.MATERIAL_ITEM.itemStyleCode
                    ), StyleChooserItem(
                        "Bakery Item", ContextCompat.getDrawable(
                            requireContext(), R.drawable.bakery_item_card
                        ), false, ProductListItemStyle.BAKERY_ITEM.itemStyleCode
                    ), StyleChooserItem(
                        "Blur Pro Item", ContextCompat.getDrawable(
                            requireContext(), R.drawable.blure_item_card
                        ), false, ProductListItemStyle.BLUR_ITEM.itemStyleCode
                    )
                )
                when (viewModel.chosenListItemStyle) {
                    ProductListItemStyle.DEFAULT_ITEM.itemStyleCode -> {
                        itemStylesList[ProductListItemStyle.DEFAULT_ITEM.itemStyleCode].isChecked =
                            true
                    }
                    ProductListItemStyle.MATERIAL_ITEM.itemStyleCode -> {
                        itemStylesList[ProductListItemStyle.MATERIAL_ITEM.itemStyleCode].isChecked =
                            true
                    }
                    ProductListItemStyle.BAKERY_ITEM.itemStyleCode -> {
                        itemStylesList[ProductListItemStyle.BAKERY_ITEM.itemStyleCode].isChecked =
                            true
                    }
                    ProductListItemStyle.BLUR_ITEM.itemStyleCode -> {
                        itemStylesList[ProductListItemStyle.BLUR_ITEM.itemStyleCode].isChecked =
                            true
                    }
                }

                return itemStylesList
            }
            else -> {
                 mutableListOf()
            }
        }


    }

    private fun initWelcomeTextMainLayout() {
        setUpDropDownSizeMenu()
        binding.bottomWelcomeTxtLeftAlignBtn.setOnClickListener {
            binding.bottomWelcomeTxtLeftAlignBtn.setImageResource(com.mostafan3ma.android.menupro10.R.drawable.left_align_yellow)
            binding.bottomWelcomeTxtRightAlignBtn.setImageResource(com.mostafan3ma.android.menupro10.R.drawable.right_align_black)
            viewModel.alignTxt(Style.ALIGN_LEFT)
        }
        binding.bottomWelcomeTxtRightAlignBtn.setOnClickListener {
            binding.bottomWelcomeTxtRightAlignBtn.setImageResource(com.mostafan3ma.android.menupro10.R.drawable.right_align_yellow)
            binding.bottomWelcomeTxtLeftAlignBtn.setImageResource(com.mostafan3ma.android.menupro10.R.drawable.left_align_black)
            viewModel.alignTxt(Style.ALIGN_RIGHT)
        }

        binding.bottomWelcomeTxtColorCard.setOnClickListener {
            viewModel.setEvent(DefaultViewModelEvent.LaunchColorPicker("change_welcome_text_color"))
        }
    }


    private fun setUpDropDownSizeMenu() {
        val list = resources.getStringArray(com.mostafan3ma.android.menupro10.R.array.sizes)
        val adapter = ArrayAdapter(
            this.requireContext(),
            com.mostafan3ma.android.menupro10.R.layout.drop_dwon_item,
            list
        )
        binding.bottomWelcomeTxtSizeAuto.setAdapter(adapter)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun checkForNewDataChanges() {
        val tempStyle = viewModel.style.value
        when (viewModel.visibleBottomSheetLayout.value) {
            AttrVisibleViews.NOTHING -> {
                //nothing to do
            }
            AttrVisibleViews.STYLE_CHOOSER -> {
                Toast.makeText(requireContext(), "style Choser", Toast.LENGTH_SHORT).show()

            }
            AttrVisibleViews.BACKGROUND_CHOOSER -> {
                when (viewModel.backgroundChooserResult) {
                    null -> {

                    }
                    is String -> {
                        tempStyle!!.backgroundChoice = Style.BACKGROUND_IMAGE_CHOICE
                        tempStyle.mainBackgroundImage = viewModel.backgroundChooserResult as String
                        viewModel.backgroundChooserResult = null
                    }
                    is Int -> {
                        tempStyle!!.backgroundChoice = Style.BACKGROUND_COLOR_CHOICE
                        tempStyle.mainBackgroundColor = viewModel.backgroundChooserResult as Int
                        viewModel.backgroundChooserResult = null
                    }
                }


                tempStyle!!.attributes = "custom"
                viewModel.addAttrChanges(tempStyle)
            }
            AttrVisibleViews.WELCOME_EDIT_TXT_LAYOUT -> {
                if (binding.bottomWelcomeTxtEdit.text!!.isNotEmpty()) {
                    tempStyle!!.welcomeText = binding.bottomWelcomeTxtEdit.text.toString()
                }

                if (binding.bottomWelcomeTxtSizeAuto.text.isNotEmpty()) {
                    tempStyle!!.welcomeTextSize = binding.bottomWelcomeTxtSizeAuto.text.toString()
                }

                if (welcomeTxtColorRegistry != null) {
                    tempStyle!!.welcomeTextColor = welcomeTxtColorRegistry!!
                }


                when (viewModel.tempTextAlignValue) {
                    Style.ALIGN_LEFT -> {
                        tempStyle?.welcomeTextAlign = viewModel.tempTextAlignValue
                    }
                    Style.ALIGN_RIGHT -> {
                        tempStyle?.welcomeTextAlign = viewModel.tempTextAlignValue
                    }
                }


                //check the aligns Buttons
                //create and call addAttrChanges()

                tempStyle!!.attributes = "custom"
                viewModel.addAttrChanges(tempStyle!!)
            }
            AttrVisibleViews.ITEM_EDITOR_LAYOUT -> {
                viewModel.calculateAndApplyItemPreviewValues()
            }
            else -> {

            }
        }

        viewModel.setEvent(DefaultViewModelEvent.OpenAttrBottomSheetEventWithView(AttrVisibleViews.NOTHING))

    }

    private fun hideAttrBottomSheetViews() {
        binding.bottomWelcomeTxtMainLayout!!.visibility = View.GONE
        binding.bottomStyleChooserMainLayout.visibility = View.GONE
        binding.bottomBackgroundChooserMainLayout.visibility = View.GONE
        binding.bottomListItemEditorMainLayout.visibility = View.GONE
    }


    private fun addDrawerClickableViews() {
        viewModel.simpleDrawerController.addClickableViews(
            ClickableView(
                binding.myAccountOption, "my account action",
                DefaultLastMenuFragmentDirections.actionDefaultLastMenuFragmentToMyAccountFragment()
            ),
            ClickableView(
                binding.settingsOption, "setting action", null
                //navigate to settings fragment
            ),
            ClickableView(
                binding.photoLibraryOption, "photo library option", null
                //navigate to photo library fragment
            ),
            ClickableView(
                binding.logoutOption, "logout option", null
                //logout and navigate out from LastDefaultMenuFragment to WelcomeFragment
            ),

            ClickableView(
                binding.attributesOption,
                "attributes action",
                null
            ),
            ClickableView(
                binding.styleImg,
                "click_style_img", null
            ),
            ClickableView(
                binding.backgroundImg,
                "click_background_img",
                null
            ),
            ClickableView(
                binding.welcomeTxtEditBtn,
                "click_welcome_txt_edit_btn",
                null
            ),
            ClickableView(
                binding.chipsCheckedBtn,
                "click_chips_checked_btn",
                null
            ),
            ClickableView(
                binding.drawerCheckedChipsTxtColorBtn,
                "drawerCheckedChipsTxtColorBtn",
                null
            ),

            ClickableView(
                binding.chipsUncheckedBtn,
                "click_chips_unchecked_btn",
                null
            ),
            ClickableView(
                binding.drawerUncheckedChipsTxtColorBtn,
                "drawerUncheckedChipsTxtColorBtn"
            ,null
            ),
            ClickableView(
                binding.saveAndApplyStyleBtn,
                "click_save_and_apply_style_btn",
                null
            )
        )
    }


}

















