package com.mostafan3ma.android.menupro10.presentation.startingFragments

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mostafan3ma.android.menupro10.databinding.FragmentAddCatagoriesBinding
import com.mostafan3ma.android.menupro10.oporations.data_Entities.Category
import com.mostafan3ma.android.menupro10.oporations.utils.SuperImageController
import com.mostafan3ma.android.menupro10.oporations.utils.hideKeyboard
import com.mostafan3ma.android.menupro10.presentation.startingFragments.adapters.AddCategoriesAdapter
import com.mostafan3ma.android.menupro10.presentation.startingFragments.adapters.CategoryListener
import com.mostafan3ma.android.menupro10.presentation.startingFragments.viewModels.AddCategoriesViewModel
import com.mostafan3ma.android.menupro10.presentation.startingFragments.viewModels.AddCategoryViewModelEvents
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.google.protobuf.Empty as Empty1

@AndroidEntryPoint
class AddCategoriesFragment
@Inject
constructor(
    private val superImageController: SuperImageController
) : Fragment() {

    private lateinit var binding: FragmentAddCatagoriesBinding

    private lateinit var categoriesAdapter: AddCategoriesAdapter

    private lateinit var categoryBottomSheet: BottomSheetBehavior<LinearLayout>

    val viewModel: AddCategoriesViewModel by viewModels()
    var clickableEnabled: Boolean = true


    companion object {
        const val TAG = "AddCategoriesFragment"
    }


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onResume() {
        super.onResume()
        viewModel.returnedImgUri.value = viewModel.superImageController.returnedUri
    }


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddCatagoriesBinding.inflate(inflater)

        categoriesAdapter = AddCategoriesAdapter(requireContext(), lifecycleScope,
            CategoryListener { position: Int ->
                if (clickableEnabled) {
                    viewModel.getClickedCategory(position)
                    viewModel.position = position
                    viewModel.setEvent(AddCategoryViewModelEvents.ItemClicked)
                }
            }
        )

        binding.adapter = categoriesAdapter
        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner


        categoryBottomSheet = setUpCategoryBottomSheet()




        subscribeObservers()
        viewModel.superImageController.register(this)
        binding.bottomSheetImg.setOnClickListener {
            viewModel.superImageController.launchRegistrar()
        }
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun subscribeObservers() {

        viewModel.savingImagesRequest.observe(viewLifecycleOwner, Observer { savingRequested ->
            if (savingRequested) {
                val readyCategoriesList: List<Category> = getPreparedCategoriesList()
                val bitmapList = mutableListOf<Bitmap>()
                for (category: Category in readyCategoriesList) {
                    val tempBitmap: Bitmap = superImageController.getBitmapFromUri(
                        this.requireContext(),
                        category.imageUri.toUri()
                    )
                    lifecycleScope.launch {
                        superImageController.saveImageToInternalStorage(
                            this@AddCategoriesFragment.requireContext(),
                            tempBitmap,
                            category.imageName
                        )
                    }
                }
            }

        })

        viewModel.navigateToAddItemFragment.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate) {
                findNavController().navigate(AddCategoriesFragmentDirections.actionAddCategoriesFragmentToAddProductsFragment())
            }
        })


        viewModel.categoriesList.observe(viewLifecycleOwner, Observer { categoriesList ->
            categoriesAdapter.submitList(categoriesList)
            categoriesAdapter.notifyDataSetChanged()
        })

        viewModel.clickedCategory.observe(viewLifecycleOwner, Observer { clickedCategory ->
            binding.clickedCategory = clickedCategory

        })


        viewModel.bottomSheetLaunched.observe(viewLifecycleOwner, Observer { isLaunched ->
            Log.d(TAG, "subscribeObservers: $isLaunched")
            if (isLaunched) {
                categoryBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                categoryBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
            }

        })

        viewModel.hideKeyBord.observe(viewLifecycleOwner, Observer { hide ->
            if (hide) {
                hideKeyboard()
            }
        })

        viewModel.clickableEnabled.observe(viewLifecycleOwner, Observer {
            clickableEnabled = it
        })


        viewModel.returnedImgUri.observe(viewLifecycleOwner, Observer {
            binding.bottomSheetImg.setImageURI(it)
        })


    }

    private fun getPreparedCategoriesList(): List<Category> {
        val isolatedList = mutableListOf<Category>()
        for (category: Category in viewModel.categoriesList.value!!) {
            when (category) {
                Category() -> {

                }
                else -> {
                    isolatedList.add(category)
                }
            }
        }
        return isolatedList as List<Category>
    }

    private fun setUpCategoryBottomSheet(): BottomSheetBehavior<LinearLayout> {
        val categoryBottomSheet = BottomSheetBehavior.from(binding.categoryBottomSheet).apply {
            isDraggable = false
        }
        return categoryBottomSheet
    }


}