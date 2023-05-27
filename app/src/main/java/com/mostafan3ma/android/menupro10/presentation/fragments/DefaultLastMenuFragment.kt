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
import androidx.constraintlayout.widget.Constraints
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


    }


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return when (viewModel.style.value!!.styleCode) {
            ChosenStyle.DEFAULT -> {
                Log.d(TAG, "onCreateView: inflated Screen DefaultStyle")
                inflateDefaultMenuScreen(inflater, viewModel.style.value!!)
            }
            ChosenStyle.BLUR_PRO -> {
                Log.d(TAG, "onCreateView: inflated Screen BLUR_PRO")
                inflateDefaultMenuScreen(inflater, viewModel.style.value!!)
//                inflateDefaultMenuStyleScreen(inflater,viewModel.style.value!!)
//                inflateMyAccount(inflater)
            }
            ChosenStyle.BAKERY_BLACK -> {
                Log.d(TAG, "onCreateView: inflated Screen BAKERY_BLACK")
                inflateDefaultMenuScreen(inflater, viewModel.style.value!!)
            }
            ChosenStyle.STANDARD_MATERIAL_DETAILS_ITEMS -> {
                Log.d(TAG, "onCreateView: inflated Screen STANDARD_MATERIAL_DETAILS_ITEMS")
                inflateDefaultMenuScreen(inflater, viewModel.style.value!!)

            }
            ChosenStyle.COLORIZES_CATEGORIES_DETAILS_ITEMS -> {
                Log.d(TAG, "onCreateView: inflated Screen COLORIZES_CATEGORIES_DETAILS_ITEMS")
                inflateDefaultMenuScreen(inflater, viewModel.style.value!!)

            }
            ChosenStyle.CATEGORIES_LIST_SWEETS_DETAILS_ITEMS -> {
                Log.d(TAG, "onCreateView: inflated Screen CATEGORIES_LIST_SWEETS_DETAILS_ITEMS")
                inflateDefaultMenuScreen(inflater, viewModel.style.value!!)
            }
            else -> {
                Log.d(TAG, "onCreateView: inflated Screen else(default) ")
                inflateDefaultMenuScreen(inflater, getPrefStyle(requireContext()))
            }

        }

    }

//    private fun inflateDefaultMenuStyleScreen(inflater: LayoutInflater, value: Style): View {
//
//    }

    private fun inflateMyAccount(inflater: LayoutInflater): View {
        Log.d(TAG, "inflateMyAccount: init")
        binding2 = FragmentMyAccountBinding.inflate(inflater)

        return binding2.root
    }

    private fun refreshCurrentFragment() {
        val id = findNavController().currentDestination?.id
        findNavController().popBackStack(id!!, true)
        findNavController().navigate(id)
    }


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onResume() {
        super.onResume()
        if (viewModel.superImageController.returnedUri != null) {
            binding.bottomBackgroundImage.background =
                viewModel.superImageController.getDrawableFromUri(
                    requireContext(),
                    viewModel.superImageController.returnedUri!!
                )

            lifecycleScope.launch {
                val returnedBitmap: Bitmap =
                    viewModel.superImageController.getBitmapFromRegister(requireContext())
                viewModel.superImageController.saveImageToInternalStorage(
                    requireContext(), returnedBitmap, Style.TEMP_BACKGROUND_IMAGE_NAME
                )
                viewModel.backgroundChooserResult = Style.TEMP_BACKGROUND_IMAGE_NAME
            }

            viewModel.superImageController.returnedUri = null
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun inflateDefaultMenuScreen(inflater: LayoutInflater, prefStyle: Style): View {
        Log.d(TAG, "inflateDefaultMenuScreen: init")

        observeStyleAttributes()
        binding = FragmentLastDefaultMenuBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        attrBottomSheetBehavior = setUpAttrBottomSheet()
        activateControllers()
        return binding.root

    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun activateControllers() {
        viewModel.superImageController.register(this)
        viewModel.setSimpleDrawerController(binding.drawerLayout, findNavController())
        addDrawerClickableViews()
        setUpItemRawCountDropDownMenu()
        subscribeObservers()

    }

    private fun setUpItemRawCountDropDownMenu() {
        val rawCountList = resources.getStringArray(R.array.raws)
        val adapter =
            ArrayAdapter(requireContext(), R.layout.drop_dwon_item, rawCountList)
        binding.drawerRawCount.setAdapter(adapter)
    }

    private fun setUpAttrBottomSheet(): BottomSheetBehavior<LinearLayout> {
        val attrBottomSheet = BottomSheetBehavior.from(binding.attrBottomSheet!!).apply {
            isDraggable = false
        }
        return attrBottomSheet
    }


    private fun observeStyleAttributes() {
        viewModel.style.observe(viewLifecycleOwner, Observer { style ->
            Log.d(TAG, "styleTest/observeStyleAttributes: style observed >>>> $style")
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

            }, style.product_list_item)
            binding.adapter = productsAdapter
            viewModel.filteredProductsList.observe(viewLifecycleOwner, Observer {
                productsAdapter.submitList(it)
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
                    val colors = intArrayOf(style!!.chipsCheckedColor, style.chipsUnCheckedColor)
                    val colorsStateList = ColorStateList(states, colors)
                    chip.chipBackgroundColor = colorsStateList
                    chip.setBackgroundColor(style.chipsUnCheckedColor)
                    chip.setTextColor(style.chipsUnCheckedTextColor)
                    chip.setOnCheckedChangeListener { compoundButton, checked ->
                        when (clicksEnabled) {
                            true -> {
                                if (checked) {
                                    viewModel.setEvent(
                                        DefaultViewModelEvent.ChangeFilterEvent(
                                            filter = compoundButton.tag.toString()
                                        )
                                    )
                                    chip.chipBackgroundColor = colorsStateList
                                    chip.setTextColor(style.chipsCheckedTextColor)
                                } else {
                                    chip.chipBackgroundColor = colorsStateList
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

            when (style.attributes) {
                Style.DEFAULT_VALUE -> {
                    Log.d(TAG, "styleTest/observeStyleAttributes: style.attributes= default")
                    when (style.styleCode) {
                        ChosenStyle.DEFAULT -> {
                            binding.styleImg.setImageResource(com.mostafan3ma.android.menupro10.R.drawable.default_style)
                            binding.styleName.text = "Default Style"
                            binding.drawerItemCardImg.setImageResource(R.drawable.default_item_card)
                            binding.itemCardStyleName.text = "Default item"
                            Log.d(
                                TAG,
                                "observeStyleAttributes: chosenStyle is Default//applying ::" +
                                        "\n  binding.styleImg.setImageResource(com.mostafan3ma.android.menupro10.R.drawable.default_style)\n" +
                                        "binding.styleName.text = \"Default Style\" \n" +
                                        "binding.drawerItemCardImg.setImageResource(R.drawable.default_item_card)\n" +
                                        "binding.itemCardStyleName.text = \"Default item\"\n"
                            )
                        }
                        ChosenStyle.BLUR_PRO -> {
                            binding.styleImg.setImageResource(com.mostafan3ma.android.menupro10.R.drawable.blure_pro)
                            binding.styleName.text = "Blur pro"
                            binding.drawerItemCardImg.setImageResource(R.drawable.blure_item_card)
                            binding.itemCardStyleName.text = "Blur item"
                            Log.d(
                                TAG,
                                "observeStyleAttributes: chosenStyle is Blur pro//applying ::" +
                                        "\n  binding.styleImg.setImageResource(com.mostafan3ma.android.menupro10.R.drawable.blure_pro)\n" +
                                        "binding.styleName.text = \"Blur pro\" \n" +
                                        "binding.drawerItemCardImg.setImageResource(R.drawable.blure_item_card)\n" +
                                        "binding.itemCardStyleName.text = \"Blur item\" \n"
                            )
                        }
                        ChosenStyle.BAKERY_BLACK -> {
                            binding.styleImg.setImageResource(com.mostafan3ma.android.menupro10.R.drawable.bakery_black)
                            binding.styleName.text = "Bakery Black"
                            binding.drawerItemCardImg.setImageResource(R.drawable.bakery_item_card)
                            binding.itemCardStyleName.text = "Bakery item"
                            Log.d(
                                TAG,
                                "observeStyleAttributes: chosenStyle is bakery black//applying ::" +
                                        "\n  binding.styleImg.setImageResource(com.mostafan3ma.android.menupro10.R.drawable.bakery_black)\n" +
                                        "binding.styleName.text = \"Bakery Black\" \n" +
                                        " binding.drawerItemCardImg.setImageResource(R.drawable.bakery_item_card)\n" +
                                        "binding.itemCardStyleName.text = \"Bakery item\" \n"
                            )
                        }
                        ChosenStyle.STANDARD_MATERIAL_DETAILS_ITEMS -> {
                            binding.styleImg.setImageResource(com.mostafan3ma.android.menupro10.R.drawable.standerd3_material)
                            binding.styleName.text = "Standard Style"
                            binding.drawerItemCardImg.setImageResource(R.drawable.material_item_card)
                            binding.itemCardStyleName.text = "Material item"
                            Log.d(
                                TAG,
                                "observeStyleAttributes: chosenStyle is STANDARD_MATERIAL_DETAILS_ITEMS //applying ::" +
                                        "\n  binding.styleImg.setImageResource(com.mostafan3ma.android.menupro10.R.drawable.standerd3_material)\n" +
                                        "binding.styleName.text = \"Standard Style\"\n" +
                                        " binding.drawerItemCardImg.setImageResource(R.drawable.material_item_card)\n" +
                                        "binding.itemCardStyleName.text = \"Material item\" \n"
                            )
                        }
                        ChosenStyle.COLORIZES_CATEGORIES_DETAILS_ITEMS -> {
                            binding.styleImg.setImageResource(com.mostafan3ma.android.menupro10.R.drawable.colories_categories)
                            binding.styleName.text = "colorizes categories"
                            Log.d(
                                TAG,
                                "observeStyleAttributes: chosenStyle is COLORIZES_CATEGORIES_DETAILS_ITEMS"
                            )
                        }
                        ChosenStyle.CATEGORIES_LIST_SWEETS_DETAILS_ITEMS -> {
                            binding.styleImg.setImageResource(com.mostafan3ma.android.menupro10.R.drawable.categories_sweets)
                            binding.styleName.text = "Sweet categories"
                            Log.d(
                                TAG,
                                "observeStyleAttributes: chosenStyle is CATEGORIES_LIST_SWEETS_DETAILS_ITEMS"
                            )
                        }
                        else -> {
                            binding.styleImg.setImageResource(com.mostafan3ma.android.menupro10.R.drawable.default_style)
                            binding.styleName.text = "Default Style"
                            Log.d(
                                TAG,
                                "observeStyleAttributes: chosenStyle is els case Applying Default Style"
                            )
                        }
                    }
                }
                Style.CUSTOM_ATTRIBUTES -> {
                    Log.d(TAG, "styleTest/observeStyleAttributes: style.attributes= Custom")
                    applyAttributes(style)
                }
            }
        })


    }

    private fun applyAttributes(style: Style) {
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
                Log.d(
                    TAG,
                    "observeStyleAttributes: chosenStyle is els case Applying Default Style"
                )
            }
        }

        when (style.recyclerRawCount) {
            1 -> {
                binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
            }
            2 -> {
                binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
            }
            3 -> {
                binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
            }
            4 -> {
                binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 4)
            }
            else -> {

            }
        }





        when (style.backgroundChoice) {
            Style.BACKGROUND_IMAGE_CHOICE -> {
                when(style.mainBackgroundImage){
                    Style.DEFAULT_VALUE->{

                    }
                    else->{
                        val backgroundBitmap: Bitmap? =
                            viewModel.superImageController.getImageFromInternalStorage(
                                requireContext(), style.mainBackgroundImage,
                                R.drawable.domy_background
                            )
                        val backgroundDrawable =
                            BitmapDrawable(requireContext().resources, backgroundBitmap)
                        binding.mainBackground.background = backgroundDrawable
                        binding.backgroundImg.background = backgroundDrawable
                        binding.bottomBackgroundImage.background = backgroundDrawable
                    }
                }


            }
            Style.BACKGROUND_COLOR_CHOICE -> {
                binding.mainBackground.setBackgroundColor(style.mainBackgroundColor!!)
                binding.backgroundImg.setBackgroundColor(style.mainBackgroundColor!!)
                binding.bottomBackgroundImage.setBackgroundColor(style.mainBackgroundColor!!)
            }
        }




        when (style.welcomeText) {
            Style.DEFAULT_VALUE -> {
                Log.d(TAG, "styleTest/applyAttributes: style.welcomeText=DefaultValue")
            }
            else -> {
                Log.d(TAG, "styleTest/applyAttributes: style.welcomeText=Not default")

                //main view
                binding.chooseMealTxt.text = style.welcomeText
                //drawer view
                binding.drawerWelcomeTxt.text = style.welcomeText

                //bottom View
                binding.bottomWelcomeTxtLayout.hint = style.welcomeText
            }
        }
        when (style.welcomeTextSize) {
            Style.DEFAULT_VALUE -> {
                Log.d(TAG, "styleTest/applyAttributes: style.welcomeTextSize= default")
                //stay on the default Size
            }
            "Small" -> {
                Log.d(TAG, "styleTest/applyAttributes: style.welcomeTextSize= small 18")
                //18
                binding.chooseMealTxt.textSize = 18F
            }
            "Medium" -> {
                Log.d(TAG, "styleTest/applyAttributes: style.welcomeTextSize= medium 20")
                //20
                binding.chooseMealTxt.textSize = 20F
            }
            "Large" -> {
                Log.d(TAG, "styleTest/applyAttributes: style.welcomeTextSize= Large 30")
                //30sp
                binding.chooseMealTxt.textSize = 30F
            }
            "X-Large" -> {
                Log.d(TAG, "styleTest/applyAttributes: style.welcomeTextSize= X-Large 35")
                binding.chooseMealTxt.textSize = 35F
            }
            else -> {
                //also stay on the default textSize
            }
        }
        when (style.welcomeTextColor) {
            Style.DEFAULT_TEXT_COLOR -> {
                Log.d(
                    TAG,
                    "styleTest/applyAttributes: style.DEFAULT_TEXT_COLOR= DEFAULT_TEXT_COLOR"
                )
                //don't do anyThing
            }
            else -> {
                Log.d(
                    TAG,
                    "styleTest/applyAttributes: style.DEFAULT_TEXT_COLOR= NOT DEFAULT"
                )
                binding.chooseMealTxt.setTextColor(style.welcomeTextColor)
            }

        }
        when (style.welcomeTextAlign) {
            Style.ALIGN_LEFT -> {
                //stay on default and don't do anything
                Log.d(TAG, "styleTest/applyAttributes: style.welcomeTxtAlignment=left")
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
                Log.d(TAG, "styleTest/applyAttributes: style.welcomeTxtAlignment=RIGHT")
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



        when (style.chipsCheckedColor) {
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
        when (style.chipsCheckedTextColor) {
            Color.parseColor(Style.DEFAULT_BLACK_COLOR) -> {

            }
            else -> {
                binding.drawerCheckedChipsTxt.setTextColor(style.chipsCheckedTextColor)
                binding.drawerCheckedChipsTxtColorBtn.setBackgroundColor(style.chipsCheckedTextColor)
            }
        }


        when (style.chipsUnCheckedColor) {
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
        when (style.chipsUnCheckedTextColor) {
            Color.parseColor(Style.DEFAULT_BLACK_COLOR) -> {

            }
            else -> {
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


    private fun subscribeObservers() {
        viewModel.domainModel.observe(viewLifecycleOwner, Observer { domainModel ->
            when (domainModel.logoImageName) {
                "" -> {}
                else -> {
                    try {
                        val bitmap: Bitmap? =
                            viewModel.superImageController.getImageFromInternalStorage(
                                requireContext(),
                                domainModel.logoImageName,
                                R.drawable.logo
                            )
                        binding.shopLogo.setImageBitmap(bitmap)
                    } catch (e: Exception) {
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
                viewModel.superImageController.launchRegistrar()
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
            } catch (e: Exception) {

            }

            try {
                itemStyleChooserAdapter.submitList(stylesList)
                itemStyleChooserAdapter.notifyDataSetChanged()
            } catch (e: Exception) {

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
                        viewModel.applyCustomAttr()
                    }
                    "change_Unchecked_chips_color" -> {
                        val tempStyle = viewModel.style.value
                        tempStyle!!.chipsUnCheckedColor = selectedColor
                        binding.chipsUncheckedBtn.apply {
                            setBackgroundColor(selectedColor)
                            setTextColor(tempStyle.chipsCheckedColor)
                        }
                        viewModel.addAttrChanges(tempStyle)
                        viewModel.applyCustomAttr()
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

                    "drawerCheckedChipsTxtColorBtn" -> {
                        val tempStyle = viewModel.style.value
                        tempStyle!!.chipsCheckedTextColor = selectedColor
                        viewModel.addAttrChanges(tempStyle)
                    }

                    "drawerUncheckedChipsTxtColorBtn" -> {
                        val tempStyle = viewModel.style.value
                        tempStyle!!.chipsUnCheckedTextColor = selectedColor
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



        viewModel.resetStyleRequired.observe(viewLifecycleOwner, Observer { required ->
            if (required) {
                when (viewModel.visibleBottomSheetLayout.value) {
                    AttrVisibleViews.NOTHING -> {
                        Log.d(TAG, "subscribeObservers:reset btn Nothing//reset main style ")
                        val tempStyle = Style()
                        tempStyle.styleCode = viewModel.style.value!!.styleCode
                        viewModel.addAttrChanges(tempStyle)
                    }
                    AttrVisibleViews.STYLE_CHOOSER -> {
                        Log.d(
                            TAG,
                            "subscribeObservers:reset btn styleChooser//return to default style"
                        )
                        val tempStyle = Style()
                        viewModel.addAttrChanges(tempStyle)
                    }
                    AttrVisibleViews.BACKGROUND_CHOOSER -> {
                        val tempStyle = viewModel.style.value
                        tempStyle!!.apply {
                            backgroundChoice = Style.BACKGROUND_IMAGE_CHOICE
                            mainBackgroundImage = Style.DEFAULT_VALUE
                        }
                        viewModel.addAttrChanges(tempStyle)
                    }
                    AttrVisibleViews.WELCOME_EDIT_TXT_LAYOUT -> {
                        val tempStyle = viewModel.style.value
                        tempStyle!!.apply {
                            welcomeText = Style.DEFAULT_VALUE
                            welcomeTextAlign = Style.ALIGN_LEFT
                            welcomeTextSize = Style.DEFAULT_TEXT_SIZE
                            welcomeTextColor = Style.DEFAULT_TEXT_COLOR
                        }
                        viewModel.addAttrChanges(tempStyle)
                    }
                    AttrVisibleViews.ITEM_EDITOR_LAYOUT -> {
                        val tempItemList = ProductListItem()
                        tempItemList.style = viewModel.style.value!!.product_list_item.style
                        val tempStyle = viewModel.style.value
                        tempStyle!!.product_list_item = tempItemList
                        viewModel.addAttrChanges(tempStyle)
                        viewModel.setEvent(DefaultViewModelEvent.OpenAttrBottomSheetEventWithView(AttrVisibleViews.NOTHING))
                    }
                    else -> {}
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
                    val previewItem = viewModel.style.value!!.product_list_item
                    initPreviewBindingItem(previewItem)
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
                    "drawerCheckedChipsTxtColorBtn" -> {
                        viewModel.setEvent(DefaultViewModelEvent.LaunchColorPicker("drawerCheckedChipsTxtColorBtn"))
                    }
                    "click_chips_unchecked_btn" -> {
                        viewModel.setEvent(DefaultViewModelEvent.LaunchColorPicker("change_Unchecked_chips_color"))
                    }
                    "drawerUncheckedChipsTxtColorBtn" -> {
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
                                    viewModel.superImageController.getImageFromInternalStorage(
                                        requireContext(),
                                        Style.TEMP_BACKGROUND_IMAGE_NAME,
                                        R.drawable.domy_background
                                    )
                                lifecycleScope.launch {
                                    // applying tempBackgroundImage to DefaultBackgroundImage
                                    viewModel.superImageController.saveImageToInternalStorage(
                                        requireContext(),
                                        tempBackgroundImage!!,
                                        Style.DEFAULT_BACKGROUND_IMAGE_NAME
                                    )

                                    tempStyle.mainBackgroundImage =
                                        Style.DEFAULT_BACKGROUND_IMAGE_NAME

                                }

                            }

                        }

                        checkItemSizeAndRawCount()
                        viewModel.addAttrChanges(tempStyle)
                        putPrefStyle(requireContext(), viewModel.style.value!!)
                        viewModel.setEvent(DefaultViewModelEvent.RefreshFragmentEvent)
                    }
                }
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

    private fun initPreviewBindingItem(previewItem: ProductListItem) {
        when (previewItem.style) {
            DEFAULT_ITEM -> {
                binding.itemPreviewLayout.removeAllViews()
                var itemBinding = ItemDefaultBinding.inflate(layoutInflater)
                itemBinding.item = viewModel.getRandomItemData()
                applyItemStyleTOBinding(itemBinding, previewItem)
                applyItemStyleToBottomSheetControllers(previewItem)
                subscribeControllersPreviewValues(itemBinding)
                itemBinding.itemCard.setOnClickListener {
                    initItemStyleChooserCardAnimation()
                }
                binding.itemPreviewLayout.addView(itemBinding.root)
            }
            MATERIAL_ITEM -> {
                binding.itemPreviewLayout.removeAllViews()
                var itemBinding = ItemMaterialBinding.inflate(layoutInflater)
                itemBinding.item = viewModel.getRandomItemData()
                applyItemStyleTOBinding(itemBinding, previewItem)
                applyItemStyleToBottomSheetControllers(previewItem)
                subscribeControllersPreviewValues(itemBinding)
                itemBinding.itemCard.setOnClickListener {
                    initItemStyleChooserCardAnimation()
                }
                binding.itemPreviewLayout.addView(itemBinding.root)
            }
            BAKERY_ITEM -> {
                binding.itemPreviewLayout.removeAllViews()
                var itemBinding = ItemBakeryBinding.inflate(layoutInflater)
                itemBinding.item = viewModel.getRandomItemData()
                applyItemStyleTOBinding(itemBinding, previewItem)
                applyItemStyleToBottomSheetControllers(previewItem)
                subscribeControllersPreviewValues(itemBinding)
                itemBinding.itemCard.setOnClickListener {
                    initItemStyleChooserCardAnimation()
                }
                binding.itemPreviewLayout.addView(itemBinding.root)
            }
            BLUR_ITEM -> {
                binding.itemPreviewLayout.removeAllViews()
                var itemBinding = ItemBlureProBinding.inflate(layoutInflater)
                itemBinding.item = viewModel.getRandomItemData()
                applyItemStyleTOBinding(itemBinding, previewItem)
                applyItemStyleToBottomSheetControllers(previewItem)
                subscribeControllersPreviewValues(itemBinding)
                itemBinding.itemCard.setOnClickListener {
                    initItemStyleChooserCardAnimation()
                }
                binding.itemPreviewLayout.addView(itemBinding.root)
            }
            else -> {}
        }

    }

    private fun checkItemSizeAndRawCount() {
        val tempStyle = viewModel.style.value
//        when (viewModel.drawerItemSizeAutoValue.value) {
//            "Small" -> {
//                tempStyle!!.product_list_item.item_width = "150dp"
//                tempStyle!!.product_list_item.item_height = "50dp"
//                viewModel.addAttrChanges(tempStyle)
//            }
//            "Medium" -> {
//                tempStyle!!.product_list_item.item_width = "250dp"
//                tempStyle!!.product_list_item.item_height = "150dp"
//                viewModel.addAttrChanges(tempStyle)
//            }
//            "Large" -> {
//                tempStyle!!.product_list_item.item_width = "350dp"
//                tempStyle!!.product_list_item.item_height = "250dp"
//                viewModel.addAttrChanges(tempStyle)
//            }
//            "X-Large" -> {
//                tempStyle!!.product_list_item.item_width = "450dp"
//                tempStyle!!.product_list_item.item_height = "350dp"
//                viewModel.addAttrChanges(tempStyle)
//            }
//            else -> {
//
//            }
//        }
        when (viewModel.drawerRawCountValue.value) {
            "1" -> {
                tempStyle!!.recyclerRawCount = 1
                viewModel.addAttrChanges(tempStyle)
            }
            "2" -> {
                tempStyle!!.recyclerRawCount = 2
                viewModel.addAttrChanges(tempStyle)
            }
            "3" -> {
                tempStyle!!.recyclerRawCount = 3
                viewModel.addAttrChanges(tempStyle)
            }
            "4" -> {
                tempStyle!!.recyclerRawCount = 4
                viewModel.addAttrChanges(tempStyle)
            }
        }
    }

    private fun initItemStyleChooserCardAnimation() {
        TransitionManager.beginDelayedTransition(
            binding.bottomItemStyleChooserCard,
            AutoTransition()
        )

        binding.bottomItemStyleChooserCard.visibility = View.VISIBLE

        itemStyleChooserAdapter = StyleChooserAdapter(StyleChooserListener { chosenItemStyle ->

            val preViewItemWithChosenStyle = ProductListItem()
            preViewItemWithChosenStyle.style = when (chosenItemStyle) {
                0 -> {
                    DEFAULT_ITEM
                }
                1 -> {
                    MATERIAL_ITEM
                }
                2 -> {
                    BAKERY_ITEM
                }
                3 -> {
                    BLUR_ITEM
                }
                4 -> {
                    DEFAULT_CATEGORY_ITEM
                }
                5 -> {
                    COLORIZES_CATEGORY_ITEM
                }
                else -> {
                    DEFAULT_ITEM
                }
            }
            viewModel.emptyItemPreviewValues()
            viewModel.secondPreviewItem = preViewItemWithChosenStyle
            viewModel.onSecondPreviewItemChoice = true
            initPreviewBindingItem(preViewItemWithChosenStyle)
            deInitItemStyleChooserCard()
        }, StyleChooserAdapter.ITEM_STYLE_VIEW_HOLDER)
        binding.bottomItemStyleChooserRec.adapter = itemStyleChooserAdapter




        TransitionManager.beginDelayedTransition(binding.bottomItemPreviewCard, AutoTransition())
        binding.bottomItemPreviewCard.visibility = View.GONE

        viewModel.populateStyleChooserList(initialStylesChooserList(ITEM_STYLE_CHOOSER_CODE))


    }

    private fun deInitItemStyleChooserCard() {
        TransitionManager.beginDelayedTransition(binding.bottomItemPreviewCard, AutoTransition())
        binding.bottomItemPreviewCard.visibility = View.VISIBLE

        TransitionManager.beginDelayedTransition(
            binding.bottomItemStyleChooserCard,
            AutoTransition()
        )
        binding.bottomItemStyleChooserCard.visibility = View.GONE
    }

    private fun subscribeControllersPreviewValues(bindingItem: Any) {
        when (bindingItem) {
            is ItemDefaultBinding -> {

                viewModel.previewItemSize.observe(viewLifecycleOwner, Observer { itemSize->
                    when (itemSize) {
                        ProductListItem.DEFAULT_ITEM_SIZE -> {
                            // stay on the default Size
                        }
                        "Small" -> {
                            /**
                             * Custom small size
                            (width,height)(250, 150)
                             */
                            bindingItem.itemCard.layoutParams = Constraints.LayoutParams(250, 150)
                        }
                        "Medium" -> {
                            /**
                             * mobile size
                            (width,height)(450, 271)
                             */
                            bindingItem.itemCard.layoutParams = Constraints.LayoutParams(450, 271)
                        }
                        "Large" -> {
                            /**
                             * tablet size (sw600dp)
                             * (width,height)(650, 391)
                             */
                            bindingItem.itemCard.layoutParams = Constraints.LayoutParams(650, 391)
                        }
                        "X-Large" -> {
                            /**
                             * Custom X-Large size
                             *
                             * (width,height) (850, 512)
                             */
                            bindingItem.itemCard.layoutParams = Constraints.LayoutParams(850, 512)

                        }

                    }

                })

                viewModel.previewItemBackground.observe(viewLifecycleOwner, Observer {
                    if (it != 0) {
                        bindingItem.itemCard.setBackgroundColor(it)
                        binding.bottomItemEditorBackgroundBtn.setBackgroundColor(it)
                    }
                })

                viewModel.previewItemNameSize.observe(viewLifecycleOwner, Observer {
                    when (it) {
                        "Small" -> {
                            bindingItem.itemName.textSize = 14F
                        }
                        "Medium" -> {
                            bindingItem.itemName.textSize = 20F
                        }
                        "Large" -> {
                            bindingItem.itemName.textSize = 24F
                        }
                        "X-Large" -> {
                            bindingItem.itemName.textSize = 30F
                        }
                        else -> {

                        }
                    }
                })
                viewModel.previewItemNameColor.observe(viewLifecycleOwner, Observer {
                    if (it != 0) {
                        bindingItem.itemName.setTextColor(it)
                        binding.bottomItemEditorNameColorBtn.setBackgroundColor(it)
                    }
                })

                viewModel.previewItemDescriptionSize.observe(viewLifecycleOwner, Observer {
                    when (it) {
                        "Small" -> {
                            bindingItem.itemDescription.textSize = 14F
                        }
                        "Medium" -> {
                            bindingItem.itemDescription.textSize = 20F
                        }
                        "Large" -> {
                            bindingItem.itemDescription.textSize = 24F
                        }
                        "X-Large" -> {
                            bindingItem.itemDescription.textSize = 30F
                        }
                        else -> {

                        }
                    }
                })
                viewModel.previewItemDescriptionColor.observe(viewLifecycleOwner, Observer {
                    if (it != 0) {
                        bindingItem.itemDescription.setTextColor(it)
                        binding.bottomItemEditorDescriptionColorBtn.setBackgroundColor(it)
                    }
                })

                viewModel.previewItemSizeTextSize.observe(viewLifecycleOwner, Observer {
                    when (it) {
                        "Small" -> {
                            bindingItem.itemSize.textSize = 14F
                        }
                        "Medium" -> {
                            bindingItem.itemSize.textSize = 20F
                        }
                        "Large" -> {
                            bindingItem.itemSize.textSize = 24F
                        }
                        "X-Large" -> {
                            bindingItem.itemSize.textSize = 30F
                        }
                        else -> {

                        }
                    }
                })
                viewModel.previewItemSizeTextColor.observe(viewLifecycleOwner, Observer {
                    if (it != 0) {
                        bindingItem.itemSize.setTextColor(it)
                        binding.bottomItemEditorSizeTxtColorBtn.setBackgroundColor(it)
                    }
                })

                viewModel.previewItemConcurrencySize.observe(viewLifecycleOwner, Observer {
                    when (it) {
                        "Small" -> {
                            bindingItem.itemPrice.textSize = 14F
                        }
                        "Medium" -> {
                            bindingItem.itemPrice.textSize = 20F
                        }
                        "Large" -> {
                            bindingItem.itemPrice.textSize = 24F
                        }
                        "X-Large" -> {
                            bindingItem.itemPrice.textSize = 30F
                        }
                        else -> {

                        }
                    }
                })
                viewModel.previewItemConcurrencyColor.observe(viewLifecycleOwner, Observer {
                    if (it != 0) {
                        bindingItem.itemPrice.setTextColor(it)
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
            is ItemMaterialBinding -> {

                viewModel.previewItemSize.observe(viewLifecycleOwner, Observer { itemSize->
                    when (itemSize) {
                        ProductListItem.DEFAULT_ITEM_SIZE -> {
                            // stay on the default Size
                        }
                        "Small" -> {
                            /**
                             * Custom small size
                            (width,height)(250, 238)
                             */
                            bindingItem.itemCard.layoutParams = Constraints.LayoutParams(250, 238)
                        }
                        "Medium" -> {
                            /**
                             * mobile size
                            (width,height)(450, 333)
                             */
                            bindingItem.itemCard.layoutParams = Constraints.LayoutParams(450, 333)
                        }
                        "Large" -> {
                            /**
                             * tablet size (sw600dp)
                             * (width,height)(650, 619)
                             */
                            bindingItem.itemCard.layoutParams = Constraints.LayoutParams(650, 619)
                        }
                        "X-Large" -> {
                            /**
                             * Custom X-Large size
                             *
                             * (width,height)(850, 809)
                             */
                            bindingItem.itemCard.layoutParams = Constraints.LayoutParams(850, 809)

                        }

                    }

                })

                viewModel.previewItemBackground.observe(viewLifecycleOwner, Observer {
                    if (it != 0) {
                        bindingItem.itemCard.setBackgroundColor(it)
                        binding.bottomItemEditorBackgroundBtn.setBackgroundColor(it)
                    }
                })

                viewModel.previewItemNameSize.observe(viewLifecycleOwner, Observer {
                    when (it) {
                        "Small" -> {
                            bindingItem.itemName.textSize = 14F
                        }
                        "Medium" -> {
                            bindingItem.itemName.textSize = 20F
                        }
                        "Large" -> {
                            bindingItem.itemName.textSize = 24F
                        }
                        "X-Large" -> {
                            bindingItem.itemName.textSize = 30F
                        }
                        else -> {

                        }
                    }
                })
                viewModel.previewItemNameColor.observe(viewLifecycleOwner, Observer {
                    if (it != 0) {
                        bindingItem.itemName.setTextColor(it)
                        binding.bottomItemEditorNameColorBtn.setBackgroundColor(it)
                    }
                })


                viewModel.previewItemSizeTextSize.observe(viewLifecycleOwner, Observer {
                    when (it) {
                        "Small" -> {
                            bindingItem.itemSize.textSize = 14F
                        }
                        "Medium" -> {
                            bindingItem.itemSize.textSize = 20F
                        }
                        "Large" -> {
                            bindingItem.itemSize.textSize = 24F
                        }
                        "X-Large" -> {
                            bindingItem.itemSize.textSize = 30F
                        }
                        else -> {

                        }
                    }
                })
                viewModel.previewItemSizeTextColor.observe(viewLifecycleOwner, Observer {
                    if (it != 0) {
                        bindingItem.itemSize.setTextColor(it)
                        binding.bottomItemEditorSizeTxtColorBtn.setBackgroundColor(it)
                    }
                })

                viewModel.previewItemConcurrencySize.observe(viewLifecycleOwner, Observer {
                    when (it) {
                        "Small" -> {
                            bindingItem.itemPrice.textSize = 14F
                        }
                        "Medium" -> {
                            bindingItem.itemPrice.textSize = 20F
                        }
                        "Large" -> {
                            bindingItem.itemPrice.textSize = 24F
                        }
                        "X-Large" -> {
                            bindingItem.itemPrice.textSize = 30F
                        }
                        else -> {

                        }
                    }
                })
                viewModel.previewItemConcurrencyColor.observe(viewLifecycleOwner, Observer {
                    if (it != 0) {
                        bindingItem.itemPrice.setTextColor(it)
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
            is ItemBakeryBinding -> {

                viewModel.previewItemSize.observe(viewLifecycleOwner, Observer { itemSize->
                    when (itemSize) {
                        ProductListItem.DEFAULT_ITEM_SIZE -> {
                            // stay on the default Size
                        }
                        "Small" -> {
                            /**
                             * Custom small size
                            (width,height)(250, 238)
                             */
                            bindingItem.itemCard.layoutParams = Constraints.LayoutParams(250, 312)
                        }
                        "Medium" -> {
                            /**
                             * mobile size
                            (width,height)(450, 333)
                             */
                            bindingItem.itemCard.layoutParams = Constraints.LayoutParams(450, 562)
                        }
                        "Large" -> {
                            /**
                             * tablet size (sw600dp)
                             * (width,height)(650, 619)
                             */
                            bindingItem.itemCard.layoutParams = Constraints.LayoutParams(650, 812)
                        }
                        "X-Large" -> {
                            /**
                             * Custom X-Large size
                             *
                             * (width,height)(850, 809)
                             */
                            bindingItem.itemCard.layoutParams = Constraints.LayoutParams(850, 1062)

                        }

                    }

                })


                viewModel.previewItemBackground.observe(viewLifecycleOwner, Observer {
                    if (it != 0) {
                        bindingItem.itemCard.setBackgroundColor(it)
                        binding.bottomItemEditorBackgroundBtn.setBackgroundColor(it)
                    }
                })

                viewModel.previewItemNameSize.observe(viewLifecycleOwner, Observer {
                    when (it) {
                        "Small" -> {
                            bindingItem.itemName.textSize = 14F
                        }
                        "Medium" -> {
                            bindingItem.itemName.textSize = 20F
                        }
                        "Large" -> {
                            bindingItem.itemName.textSize = 24F
                        }
                        "X-Large" -> {
                            bindingItem.itemName.textSize = 30F
                        }
                        else -> {

                        }
                    }
                })
                viewModel.previewItemNameColor.observe(viewLifecycleOwner, Observer {
                    if (it != 0) {
                        bindingItem.itemName.setTextColor(it)
                        binding.bottomItemEditorNameColorBtn.setBackgroundColor(it)
                    }
                })


                viewModel.previewItemSizeTextSize.observe(viewLifecycleOwner, Observer {
                    when (it) {
                        "Small" -> {
                            bindingItem.itemSize.textSize = 14F
                        }
                        "Medium" -> {
                            bindingItem.itemSize.textSize = 20F
                        }
                        "Large" -> {
                            bindingItem.itemSize.textSize = 24F
                        }
                        "X-Large" -> {
                            bindingItem.itemSize.textSize = 30F
                        }
                        else -> {

                        }
                    }
                })
                viewModel.previewItemSizeTextColor.observe(viewLifecycleOwner, Observer {
                    if (it != 0) {
                        bindingItem.itemSize.setTextColor(it)
                        binding.bottomItemEditorSizeTxtColorBtn.setBackgroundColor(it)
                    }
                })

                viewModel.previewItemConcurrencySize.observe(viewLifecycleOwner, Observer {
                    when (it) {
                        "Small" -> {
                            bindingItem.itemPrice.textSize = 14F
                        }
                        "Medium" -> {
                            bindingItem.itemPrice.textSize = 20F
                        }
                        "Large" -> {
                            bindingItem.itemPrice.textSize = 24F
                        }
                        "X-Large" -> {
                            bindingItem.itemPrice.textSize = 30F
                        }
                        else -> {

                        }
                    }
                })
                viewModel.previewItemConcurrencyColor.observe(viewLifecycleOwner, Observer {
                    if (it != 0) {
                        bindingItem.itemPrice.setTextColor(it)
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
            is ItemBlureProBinding -> {
                viewModel.previewItemSize.observe(viewLifecycleOwner, Observer { itemSize->
                    when (itemSize) {
                        ProductListItem.DEFAULT_ITEM_SIZE -> {
                            // stay on the default Size
                        }
                        "Small" -> {
                            /**
                             * Custom small size
                            (width,height)(250, 238)
                             */
                            bindingItem.itemMainLayout.layoutParams=LinearLayout.LayoutParams(363,150)
                            bindingItem.itemCard.layoutParams = LinearLayout.LayoutParams(300, 140)
                        }
                        "Medium" -> {
                            /**
                             * mobile size
                            (width,height)(450, 333)
                             */
                            bindingItem.itemMainLayout.layoutParams=LinearLayout.LayoutParams(605,250)
                            bindingItem.itemCard.layoutParams = LinearLayout.LayoutParams(500, 240)
                        }
                        "Large" -> {
                            /**
                             * tablet size (sw600dp)
                             * (width,height)(650, 619)
                             */
                            bindingItem.itemMainLayout.layoutParams=LinearLayout.LayoutParams(847,350)
                            bindingItem.itemCard.layoutParams = LinearLayout.LayoutParams(700, 340)
                        }
                        "X-Large" -> {
                            /**
                             * Custom X-Large size
                             *
                             * (width,height)(850, 809)
                             */
                            bindingItem.itemMainLayout.layoutParams=LinearLayout.LayoutParams(1089,450)
                            bindingItem.itemCard.layoutParams = LinearLayout.LayoutParams(900, 440)


                        }

                    }

                })

                viewModel.previewItemBackground.observe(viewLifecycleOwner, Observer {
                    if (it != 0) {
                        bindingItem.itemCard.setBackgroundColor(it)
                        binding.bottomItemEditorBackgroundBtn.setBackgroundColor(it)
                    }
                })

                viewModel.previewItemNameSize.observe(viewLifecycleOwner, Observer {
                    when (it) {
                        "Small" -> {
                            bindingItem.itemName.textSize = 14F
                        }
                        "Medium" -> {
                            bindingItem.itemName.textSize = 20F
                        }
                        "Large" -> {
                            bindingItem.itemName.textSize = 24F
                        }
                        "X-Large" -> {
                            bindingItem.itemName.textSize = 30F
                        }
                        else -> {

                        }
                    }
                })
                viewModel.previewItemNameColor.observe(viewLifecycleOwner, Observer {
                    if (it != 0) {
                        bindingItem.itemName.setTextColor(it)
                        binding.bottomItemEditorNameColorBtn.setBackgroundColor(it)
                    }
                })

                viewModel.previewItemDescriptionSize.observe(viewLifecycleOwner, Observer {
                    when (it) {
                        "Small" -> {
                            bindingItem.itemDescription.textSize = 14F
                        }
                        "Medium" -> {
                            bindingItem.itemDescription.textSize = 20F
                        }
                        "Large" -> {
                            bindingItem.itemDescription.textSize = 24F
                        }
                        "X-Large" -> {
                            bindingItem.itemDescription.textSize = 30F
                        }
                        else -> {

                        }
                    }
                })
                viewModel.previewItemDescriptionColor.observe(viewLifecycleOwner, Observer {
                    if (it != 0) {
                        bindingItem.itemDescription.setTextColor(it)
                        binding.bottomItemEditorDescriptionColorBtn.setBackgroundColor(it)
                    }
                })

                viewModel.previewItemSizeTextSize.observe(viewLifecycleOwner, Observer {
                    when (it) {
                        "Small" -> {
                            bindingItem.itemSize.textSize = 14F
                        }
                        "Medium" -> {
                            bindingItem.itemSize.textSize = 20F
                        }
                        "Large" -> {
                            bindingItem.itemSize.textSize = 24F
                        }
                        "X-Large" -> {
                            bindingItem.itemSize.textSize = 30F
                        }
                        else -> {

                        }
                    }
                })
                viewModel.previewItemSizeTextColor.observe(viewLifecycleOwner, Observer {
                    if (it != 0) {
                        bindingItem.itemSize.setTextColor(it)
                        binding.bottomItemEditorSizeTxtColorBtn.setBackgroundColor(it)
                    }
                })

                viewModel.previewItemConcurrencySize.observe(viewLifecycleOwner, Observer {
                    when (it) {
                        "Small" -> {
                            bindingItem.itemPrice.textSize = 14F
                        }
                        "Medium" -> {
                            bindingItem.itemPrice.textSize = 20F
                        }
                        "Large" -> {
                            bindingItem.itemPrice.textSize = 24F
                        }
                        "X-Large" -> {
                            bindingItem.itemPrice.textSize = 30F
                        }
                        else -> {

                        }
                    }
                })
                viewModel.previewItemConcurrencyColor.observe(viewLifecycleOwner, Observer {
                    if (it != 0) {
                        bindingItem.itemPrice.setTextColor(it)
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

    private fun applyItemStyleToBottomSheetControllers(item: ProductListItem?) {
        binding.bottomItemEditorSizeControllerAuto.hint=item!!.item_size

        binding.bottomItemEditorBackgroundBtn.setBackgroundColor(item.background_color)

        binding.bottomItemEditorNameInputField.hint = item.name_text_size
        binding.bottomItemEditorNameColorBtn.setBackgroundColor(item.name_text_color)

        binding.bottomItemEditorDescriptionInputField.hint =
            item.description_text_size
        binding.bottomItemEditorDescriptionColorBtn.setBackgroundColor(item.description_text_color)

        binding.bottomItemEditorSizeInputField.hint = item.size_text_size
        binding.bottomItemEditorSizeTxtColorBtn.setBackgroundColor(item.size_text_color)

        binding.bottomItemEditorConcurrencyTextInputField.hint =
            item.concurrency_type_text
        binding.bottomItemEditorConcurrencySizeInputField.hint =
            item.concurrency_text_size
        binding.bottomItemEditorConcurrencyColorBtn.setBackgroundColor(item.concurrency_text_Color)


    }

    private fun setUpDropDawnMenus() {
        val sizes = resources.getStringArray(R.array.sizes)
        val sizesAdapter = ArrayAdapter(requireContext(), R.layout.drop_dwon_item, sizes)
        binding.bottomItemEditorSizeControllerAuto.setAdapter(sizesAdapter)
        binding.bottomItemEditorNameSizeAuto.setAdapter(sizesAdapter)
        binding.bottomItemEditorDescriptionSizeAuto.setAdapter(sizesAdapter)
        binding.bottomItemEditorConcurrencySizeAuto.setAdapter(sizesAdapter)
        binding.bottomItemEditorSizeAuto.setAdapter(sizesAdapter)
        val currencies=resources.getStringArray(R.array.currencies)
        val currenciesAdapter=ArrayAdapter(requireContext(),R.layout.drop_dwon_item,currencies)
        binding.bottomItemEditorConcurrencyTextAuto.setAdapter(currenciesAdapter)
    }

    private fun applyItemStyleTOBinding(itemBinding: Any, previewItem: ProductListItem) {
        when (itemBinding) {
            is ItemDefaultBinding -> {
                DefaultProductViewHolder.observeStyleAttr(itemBinding,previewItem)
            }
            is ItemMaterialBinding -> {
                MaterialProductViewHolder.observeStyleAttr(itemBinding,previewItem)
            }
            is ItemBakeryBinding -> {
                BakeryProductViewHolder.observeStyleAttr(itemBinding,previewItem)
            }
            is ItemBlureProBinding -> {
                BlurProductViewHolder.observeStyleAttr(itemBinding,previewItem)
            }
            else -> {

            }
        }

    }


    private fun initialStylesChooserList(styleChooserRequestCode: Int): MutableList<StyleChooserItem> {
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
                when (viewModel.style.value!!.product_list_item.style.itemStyleCode) {
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


    private fun checkForNewDataChanges() {
        when (viewModel.visibleBottomSheetLayout.value) {
            AttrVisibleViews.NOTHING -> {
                //nothing to do
            }
            AttrVisibleViews.STYLE_CHOOSER -> {
                Toast.makeText(requireContext(), "style Choser", Toast.LENGTH_SHORT).show()
                when (viewModel.tempStyleChoice) {
                    99 -> {

                    }
                    ChosenStyle.DEFAULT.styleCode -> {
                        val tempStyle = Style(attributes = Style.DEFAULT_VALUE, styleCode = ChosenStyle.DEFAULT)
                        viewModel.addAttrChanges(tempStyle)
                        viewModel.tempStyleChoice = 99
                    }
                    ChosenStyle.BLUR_PRO.styleCode -> {
                        val tempStyle = Style(attributes = Style.DEFAULT_VALUE,styleCode = ChosenStyle.BLUR_PRO)
                        viewModel.addAttrChanges(tempStyle)
                        viewModel.tempStyleChoice = 99
                    }
                    ChosenStyle.BAKERY_BLACK.styleCode -> {
                        val tempStyle = Style(attributes = Style.DEFAULT_VALUE,styleCode = ChosenStyle.BAKERY_BLACK)
                        viewModel.addAttrChanges(tempStyle)
                        viewModel.tempStyleChoice = 99
                    }
                    ChosenStyle.STANDARD_MATERIAL_DETAILS_ITEMS.styleCode -> {
                        val tempStyle = Style(attributes = Style.DEFAULT_VALUE,styleCode = ChosenStyle.STANDARD_MATERIAL_DETAILS_ITEMS)
                        viewModel.addAttrChanges(tempStyle)
                        viewModel.tempStyleChoice = 99
                    }
                    ChosenStyle.COLORIZES_CATEGORIES_DETAILS_ITEMS.styleCode -> {
                        val tempStyle = Style(attributes = Style.DEFAULT_VALUE,styleCode = ChosenStyle.COLORIZES_CATEGORIES_DETAILS_ITEMS)
                        viewModel.addAttrChanges(tempStyle)
                        viewModel.tempStyleChoice = 99
                    }
                    ChosenStyle.CATEGORIES_LIST_SWEETS_DETAILS_ITEMS.styleCode -> {
                        val tempStyle = Style(attributes = Style.DEFAULT_VALUE,styleCode = ChosenStyle.CATEGORIES_LIST_SWEETS_DETAILS_ITEMS)
                        viewModel.addAttrChanges(tempStyle)
                        viewModel.tempStyleChoice = 99
                    }
                }
//
            }
            AttrVisibleViews.BACKGROUND_CHOOSER -> {
                val tempStyle=viewModel.style.value
                when (viewModel.backgroundChooserResult) {
                    null -> {

                    }
                    is String -> {
                        tempStyle!!.backgroundChoice = Style.BACKGROUND_IMAGE_CHOICE
                        tempStyle.mainBackgroundImage = viewModel.backgroundChooserResult as String
                        viewModel.backgroundChooserResult = null
                        tempStyle.attributes=Style.CUSTOM_ATTRIBUTES
                        viewModel.addAttrChanges(tempStyle)
                    }
                    is Int -> {
                        tempStyle!!.backgroundChoice = Style.BACKGROUND_COLOR_CHOICE
                        tempStyle.mainBackgroundColor = viewModel.backgroundChooserResult as Int
                        viewModel.backgroundChooserResult = null
                        tempStyle.attributes=Style.CUSTOM_ATTRIBUTES
                        viewModel.addAttrChanges(tempStyle)
                    }
                }

            }
            AttrVisibleViews.WELCOME_EDIT_TXT_LAYOUT -> {
                val tempStyle=viewModel.style.value
                if (binding.bottomWelcomeTxtEdit.text!!.isNotEmpty()) {
                    tempStyle!!.welcomeText = binding.bottomWelcomeTxtEdit.text.toString()
                    tempStyle.attributes=Style.CUSTOM_ATTRIBUTES
                    viewModel.addAttrChanges(tempStyle)
                }

                if (binding.bottomWelcomeTxtSizeAuto.text.isNotEmpty()) {
                    tempStyle!!.welcomeTextSize = binding.bottomWelcomeTxtSizeAuto.text.toString()
                    tempStyle.attributes=Style.CUSTOM_ATTRIBUTES
                    viewModel.addAttrChanges(tempStyle)
                }

                if (welcomeTxtColorRegistry != null) {
                    tempStyle!!.welcomeTextColor = welcomeTxtColorRegistry!!
                    tempStyle.attributes=Style.CUSTOM_ATTRIBUTES
                    viewModel.addAttrChanges(tempStyle)
                }


                when (viewModel.tempTextAlignValue) {
                    Style.ALIGN_LEFT -> {
                        tempStyle!!.welcomeTextAlign = viewModel.tempTextAlignValue
                        viewModel.addAttrChanges(tempStyle)
                    }
                    Style.ALIGN_RIGHT -> {
                        tempStyle!!.welcomeTextAlign = viewModel.tempTextAlignValue
                        tempStyle.attributes=Style.CUSTOM_ATTRIBUTES
                        viewModel.addAttrChanges(tempStyle)
                    }
                }

            }
            AttrVisibleViews.ITEM_EDITOR_LAYOUT -> {
                viewModel.calculateAndApplyItemPreviewValues()
                viewModel.onSecondPreviewItemChoice = false
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
                "drawerUncheckedChipsTxtColorBtn", null
            ),
            ClickableView(
                binding.saveAndApplyStyleBtn,
                "click_save_and_apply_style_btn",
                null
            ),

            ClickableView(
                binding.drawerRawCount,
                "drawer_raw_count",
                null
            )
        )
    }


}

















