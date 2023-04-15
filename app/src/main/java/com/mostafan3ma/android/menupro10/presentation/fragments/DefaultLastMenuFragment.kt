package com.mostafan3ma.android.menupro10.presentation.fragments

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.mostafan3ma.android.menupro10.R
import com.mostafan3ma.android.menupro10.databinding.FragmentLastDefaultMenuBinding
import com.mostafan3ma.android.menupro10.databinding.FragmentMyAccountBinding
import com.mostafan3ma.android.menupro10.oporations.utils.*
import com.mostafan3ma.android.menupro10.presentation.fragments.adapters.*
import com.mostafan3ma.android.menupro10.presentation.fragments.viewModels.AttrVisibleViews
import com.mostafan3ma.android.menupro10.presentation.fragments.viewModels.DefaultLastMenuViewModel
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
            val preffStyle = getPrefStyle(requireContext())
            Log.d(TAG, "styleTest/onCreate:prefStyle $preffStyle")
            viewModel.passPrefStyle(getPrefStyle(requireContext()))
        }

        productsAdapter = ProductsAdapter(ProductListener1 { position: Int ->
            when (clicksEnabled) {
                true -> {
                    Toast.makeText(
                        this.requireContext(),
                        "item $position  clicked>>>",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    Log.d(TAG, "onCreate: ProductsAdapter init")
                }
                false -> {
                    // don't do clicks on Adapter items
                }
            }

        })


    }


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return when (viewModel.style.value!!.styleCode) {
            ChosenStyle.DEFAULT -> {
                Log.d(TAG, "onCreateView: inflating DefaultStyle")
                inflateDefaultMenuScreen(inflater, getPrefStyle(requireContext()))
            }
            ChosenStyle.BLUR_PRO -> {
                Log.d(TAG, "onCreateView: inflating BLUR_PRO")
                inflateDefaultMenuScreen(inflater, getPrefStyle(requireContext()))
//                inflateMyAccount(inflater)
            }
            ChosenStyle.BAKERY_BLACK -> {
                Log.d(TAG, "onCreateView: inflating BAKERY_BLACK")
                inflateDefaultMenuScreen(inflater, getPrefStyle(requireContext()))
            }
            ChosenStyle.STANDARD_MATERIAL_DETAILS_ITEMS -> {
                Log.d(TAG, "onCreateView: inflating STANDARD_MATERIAL_DETAILS_ITEMS")
                inflateDefaultMenuScreen(inflater, getPrefStyle(requireContext()))

            }
            ChosenStyle.COLORIZES_CATEGORIES_DETAILS_ITEMS -> {
                Log.d(TAG, "onCreateView: inflating COLORIZES_CATEGORIES_DETAILS_ITEMS")
                inflateDefaultMenuScreen(inflater, getPrefStyle(requireContext()))

            }
            ChosenStyle.CATEGORIES_LIST_SWEETS_DETAILS_ITEMS -> {
                Log.d(TAG, "onCreateView: inflating CATEGORIES_LIST_SWEETS_DETAILS_ITEMS")
                inflateDefaultMenuScreen(inflater, getPrefStyle(requireContext()))
            }
            else -> {
                Log.d(TAG, "onCreateView: inflating else")
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
        binding.shopLogo.setOnClickListener {
            superImageController.launchRegistrar()
        }

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
            Log.d(TAG, "observeStyleAttributes: style observed >>>>")

            when (style.styleCode) {
                ChosenStyle.DEFAULT -> {
                    binding.styleImg.setImageResource(com.mostafan3ma.android.menupro10.R.drawable.default_style)
                    binding.styleName.text = "Default Style"
                }
                ChosenStyle.BLUR_PRO -> {
                    binding.styleImg.setImageResource(com.mostafan3ma.android.menupro10.R.drawable.blure_pro)
                    binding.styleName.text = "Blur pro"
                }
                ChosenStyle.BAKERY_BLACK -> {
                    binding.styleImg.setImageResource(com.mostafan3ma.android.menupro10.R.drawable.bakery_black)
                    binding.styleName.text = "Bakery Black"
                }
                ChosenStyle.STANDARD_MATERIAL_DETAILS_ITEMS -> {
                    binding.styleImg.setImageResource(com.mostafan3ma.android.menupro10.R.drawable.standerd3_material)
                    binding.styleName.text = "Standard Style"
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
                    setTextColor(style.chipsUnCheckedColor)
                }
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
                    setTextColor(style.chipsCheckedColor)
                }
            }
        }


    }


    private var welcomeTxtColorRegistry: Int? = null
    private var snapHelper: LinearSnapHelper? = null

    @RequiresApi(Build.VERSION_CODES.P)
    private fun subscribeObservers() {
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
            styleChooserAdapter.submitList(stylesList)
            Log.d(TAG, "testingChooserAdapter: listObserved: $stylesList")
            styleChooserAdapter.notifyDataSetChanged()
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


                    else -> {
                        Log.d(
                            TAG,
                            "selected Color observer:error (els case)!!?    /selectedColor=$selectedColor"
                        )
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
                    attrBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
                AttrVisibleViews.WELCOME_EDIT_TXT_LAYOUT -> {
                    binding.bottomWelcomeTxtMainLayout.visibility = View.VISIBLE
                    attrBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    initWelcomeTextMainLayout()
                }
                AttrVisibleViews.STYLE_CHOOSER -> {
                    styleChooserAdapter =
                        StyleChooserAdapter(StyleChooserListener { choseStyleCode: Int ->
                            viewModel.adjustStyleChooserList(choseStyleCode)
                            Log.d(
                                TAG,
                                "onCreate: testingChooserAdapter: clicking the item $choseStyleCode"
                            )
                            viewModel.getTheNewChosenStyle(choseStyleCode)
                        })
                    binding.bottomStyleChooserRec.adapter = styleChooserAdapter
                    binding.bottomStyleChooserRec.scrollToPosition(viewModel.style.value!!.styleCode.styleCode)

                    //snapping the rec item to the medial of the screen
                    when (snapHelper) {
                        /*
                        the snapHelper should be initiated and attached only on time
                        so if it's value iss not null don't initiate it again
                        otherwise you have { FATAL EXCEPTION:An instance of OnFlingListener already set.}
                         */
                        null -> {
                            snapHelper = LinearSnapHelper()
                            snapHelper!!.attachToRecyclerView(binding.bottomStyleChooserRec)
                        }
                        else -> {}
                    }

                    binding.bottomStyleChooserMainLayout.visibility = View.VISIBLE
                    attrBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    viewModel.populateStyleChooserList(initialStylesChooserList())
                }
                AttrVisibleViews.BACKGROUND_CHOOSER -> {
                    binding.bottomBackgroundChooserMainLayout.visibility = View.VISIBLE
                    attrBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    binding.bottomSetMainColorBtn.setOnClickListener {
                        viewModel.setEvent(DefaultViewModelEvent.LaunchColorPicker("change_background"))
                    }
                }
                AttrVisibleViews.ITEM_STYLE_CHOOSER -> {

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
                    "click_chips_unchecked_btn" -> {
                        viewModel.setEvent(DefaultViewModelEvent.LaunchColorPicker("change_Unchecked_chips_color"))
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
                chip.setTextColor(style.chipsCheckedColor)
                chip.setOnCheckedChangeListener { compoundButton, checked ->
                    when (clicksEnabled) {
                        true -> {
                            if (checked) {
                                viewModel.setEvent(DefaultViewModelEvent.ChangeFilterEvent(filter = compoundButton.tag.toString()))
                                chip.setTextColor(style.chipsUnCheckedColor)
                            } else {
                                chip.setTextColor(style.chipsCheckedColor)
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


    private fun initialStylesChooserList(): MutableList<StyleChooserItem> {
        Log.d(TAG, "initialStylesChooserList:styleChooser ")
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
            ChosenStyle.DEFAULT -> stylesList[ChosenStyle.DEFAULT.styleCode].isChecked = true
            ChosenStyle.BLUR_PRO -> stylesList[ChosenStyle.BLUR_PRO.styleCode].isChecked = true
            ChosenStyle.BAKERY_BLACK -> stylesList[ChosenStyle.BAKERY_BLACK.styleCode].isChecked =
                true
            ChosenStyle.STANDARD_MATERIAL_DETAILS_ITEMS -> stylesList[ChosenStyle.STANDARD_MATERIAL_DETAILS_ITEMS.styleCode].isChecked =
                true
            ChosenStyle.COLORIZES_CATEGORIES_DETAILS_ITEMS -> stylesList[ChosenStyle.COLORIZES_CATEGORIES_DETAILS_ITEMS.styleCode].isChecked =
                true
            ChosenStyle.CATEGORIES_LIST_SWEETS_DETAILS_ITEMS -> stylesList[ChosenStyle.CATEGORIES_LIST_SWEETS_DETAILS_ITEMS.styleCode].isChecked =
                true
        }
        Log.d(
            TAG,
            "initialStylesChooserList:styleChooser :::>> init list:${stylesList.toString()} "
        )
        return stylesList
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
            AttrVisibleViews.ITEM_STYLE_CHOOSER -> {}
            else -> {

            }
        }

        viewModel.setEvent(DefaultViewModelEvent.OpenAttrBottomSheetEventWithView(AttrVisibleViews.NOTHING))

    }

    private fun hideAttrBottomSheetViews() {
        binding.bottomWelcomeTxtMainLayout!!.visibility = View.GONE
        binding.bottomStyleChooserMainLayout.visibility = View.GONE
        binding.bottomBackgroundChooserMainLayout.visibility = View.GONE


        // add thee other attrBottomViews layouts
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
                binding.chipsUncheckedBtn,
                "click_chips_unchecked_btn",
                null
            ),
            ClickableView(
                binding.itemCardStyleImg,
                "click_item_card_style_img",
                null
            ),
            ClickableView(
                binding.itemCardBackgroundColorAuto,
                "click_item_card_background_auto",
                null
            ),
            ClickableView(
                binding.itemNameSizeEdit,
                "click_item_name_size_edit",
                null
            ),
            ClickableView(
                binding.itemNameTxtColorAuto,
                "click_item_name_txt_color_auto",
                null
            ),
            ClickableView(
                binding.itemDescriptionSizeEdit,
                "click_item_description_size_edit",
                null
            ),
            ClickableView(
                binding.itemDescriptionTxtColorAuto,
                "click_item_description_txt_color_auto",
                null
            ),
            ClickableView(
                binding.concurrencyTxtEdit,
                "concurrency_txt_edit",
                null
            ),
            ClickableView(
                binding.concurrencyTxtColorAuto,
                "click_concurrency_txt_color_auto",
                null
            ),
            ClickableView(
                binding.saveAndApplyStyleBtn,
                "click_save_and_apply_style_btn",
                null
            )
        )
    }


}


//
//styleCode:
//attributes: String =
//mainBackground:
//welcomeText:
//welcomeTextAlign:
//welcomeTextSize:
//welcomeTextColor:
//chipsCheckedColor:
//chipsUnCheckedColor:


//product_list_item:////////

//style:
//concurrency:
//concurrencyColor:
//background:
//textColor
//textSizeName:
//textColorName:
//textSizeDescription:
//textColorDescription
//


















