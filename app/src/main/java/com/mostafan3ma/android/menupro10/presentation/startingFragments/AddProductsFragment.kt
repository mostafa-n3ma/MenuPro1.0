package com.mostafan3ma.android.menupro10.presentation.startingFragments

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mostafan3ma.android.menupro10.R
import com.mostafan3ma.android.menupro10.databinding.FragmentAddProductsBinding
import com.mostafan3ma.android.menupro10.oporations.DataManagment.repository.ShopRepository
import com.mostafan3ma.android.menupro10.oporations.data_Entities.Category
import com.mostafan3ma.android.menupro10.oporations.data_Entities.Item
import com.mostafan3ma.android.menupro10.oporations.utils.DataState
import com.mostafan3ma.android.menupro10.oporations.utils.SuperImageController
import com.mostafan3ma.android.menupro10.oporations.utils.SuperImageController.Companion.IMAGES_FILE
import com.mostafan3ma.android.menupro10.oporations.utils.hideKeyboard
import com.mostafan3ma.android.menupro10.presentation.startingFragments.adapters.AddProductsAdapter
import com.mostafan3ma.android.menupro10.presentation.startingFragments.adapters.ProductsListener
import com.mostafan3ma.android.menupro10.presentation.startingFragments.viewModels.AddProductsViewModel
import com.mostafan3ma.android.menupro10.presentation.startingFragments.viewModels.AddProductsViewModelEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject


@AndroidEntryPoint
class AddProductsFragment : Fragment() {

    private lateinit var binding: FragmentAddProductsBinding
    lateinit var adapter: AddProductsAdapter

    val viewModel: AddProductsViewModel by viewModels()
    private lateinit var productBottomSheet: BottomSheetBehavior<LinearLayout>

    var clickableEnabled: Boolean = true

    companion object {
        const val TAG = "AddProductsFragment"
    }


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onResume() {
        super.onResume()
        viewModel.returnedImgUri.value = viewModel.superImageController.returnedUri

        Log.d(TAG, "onResume: returnedUri=${viewModel.returnedImgUri.value.toString()}")
    }


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddProductsBinding.inflate(inflater)
        adapter = AddProductsAdapter(this.requireContext(), ProductsListener { position ->
            if (clickableEnabled) {
                viewModel.getClickedProduct(position)
                viewModel.setEvent(AddProductsViewModelEvent.ItemClicked)
            }
        })
        binding.adapter = adapter
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.superImageController.register(this)

        subscribeObservers()

        productBottomSheet = setUpProductBottomSheet()

        setUpDropDownSizeMenu()




        return binding.root
    }

    private fun setUpDropDownSizeMenu() {
        val list = resources.getStringArray(R.array.sizes)
        val adapter = ArrayAdapter(this.requireContext(), R.layout.drop_dwon_item, list)
        binding.productSizeEt.setAdapter(adapter)
    }

    private fun setUpProductBottomSheet(): BottomSheetBehavior<LinearLayout> {
        val bottomSheet = BottomSheetBehavior.from(binding.productBottomSheet).apply {
            isDraggable = false
        }
        return bottomSheet
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun subscribeObservers() {
        viewModel.productsList.observe(viewLifecycleOwner, Observer { productsList ->
            adapter.submitList(productsList)
            adapter.notifyDataSetChanged()
        })
        viewModel.clickedProduct.observe(viewLifecycleOwner, Observer { clickedProduct ->
            binding.clickedProduct = clickedProduct
        })
        viewModel.bottomSheetLaunched.observe(viewLifecycleOwner, Observer { launched ->
            when (launched) {
                true -> {
                    productBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
                }
                false -> {
                    productBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
        })

        viewModel.requestNameFocus.observe(viewLifecycleOwner, Observer { focused ->
            when (focused) {
                true -> binding.productNameField.requestFocus()
                else -> {}
            }
        })

        viewModel.hideKeyBoard.observe(viewLifecycleOwner, Observer { hide ->
            when (hide) {
                true -> hideKeyboard()
                false -> {}
            }
        })
        viewModel.clickableEnabled.observe(viewLifecycleOwner, Observer {
            clickableEnabled = it
        })

        viewModel.categories_list.observe(viewLifecycleOwner, Observer { categoriesList ->
            val list = mutableListOf<String>()
            categoriesList.map { category ->
                list.add(category.name)
            }
            val adapter = ArrayAdapter(this.requireContext(), R.layout.drop_dwon_item, list)
            binding.productCategoryEt.setAdapter(adapter)
        })

        viewModel.launchImgPicker.observe(viewLifecycleOwner, Observer { luanch ->
            when (luanch) {
                true -> {
                    viewModel.superImageController.launchRegistrar()
                }
                false -> {

                }
            }

        })
        viewModel.returnedImgUri.observe(viewLifecycleOwner, Observer { uri ->
            if (uri != null) {
                binding.bottomSheetImg.setImageURI(uri)
            }
        })
        viewModel.savingImagesRequest.observe(viewLifecycleOwner, Observer { savingImgRequested ->
            Log.d(TAG, "subscribeObservers: observing saveImgRequest=$savingImgRequested")
            if (savingImgRequested) {
                val readyProductList: List<Item> = getPreparedProductsList()
                for (product: Item in readyProductList) {
                    val tempBitmap: Bitmap = viewModel.superImageController.getBitmapFromUri(
                        this.requireContext(),
                        product.imageUri.toUri()
                    )
                    lifecycleScope.launch {
                        viewModel.superImageController.saveImageToInternalStorage(requireContext(),tempBitmap,product.imageName,)
                        Log.d(TAG, "subscribeObservers: save Img for product:${product.name}")
                    }
                }
            }
        })
    }

    private fun getPreparedProductsList(): List<Item> {
        val isolatedProductsList = mutableListOf<Item>()
        for (product: Item in viewModel.productsList.value!!) {
            when (product) {
                Item() -> {
                    Log.d(TAG, "getPreparedProductsList: empty Product")
                }
                else -> {
                    Log.d(
                        TAG,
                        "getPreparedProductsList: not empty product adding ${product.name} to iso list"
                    )
                    isolatedProductsList.add(product)
                }
            }
        }

        return isolatedProductsList as List<Item>

    }


}





















