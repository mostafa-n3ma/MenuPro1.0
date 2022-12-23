package com.mostafan3ma.android.menupro10.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.mostafan3ma.android.menupro10.R
import com.mostafan3ma.android.menupro10.databinding.FragmentLastDefaultMenuBinding
import com.mostafan3ma.android.menupro10.oporations.utils.ClickableView
import com.mostafan3ma.android.menupro10.presentation.fragments.adapters.AddProductsAdapter
import com.mostafan3ma.android.menupro10.presentation.fragments.adapters.ProductListener1
import com.mostafan3ma.android.menupro10.presentation.fragments.adapters.ProductsAdapter
import com.mostafan3ma.android.menupro10.presentation.fragments.adapters.ProductsListener
import com.mostafan3ma.android.menupro10.presentation.fragments.viewModels.DefaultLastMenuViewModel
import com.mostafan3ma.android.menupro10.presentation.fragments.viewModels.DefaultViewModelEvent
import dagger.hilt.android.AndroidEntryPoint




@AndroidEntryPoint
class DefaultLastMenuFragment : Fragment() {
    private lateinit var binding: FragmentLastDefaultMenuBinding
    private lateinit var productsAdapter: ProductsAdapter


    val viewModel: DefaultLastMenuViewModel by viewModels()


    companion object {
        const val TAG = "DefaultLastMenuFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productsAdapter = ProductsAdapter( ProductListener1 { position: Int ->
            Toast.makeText(this.requireContext(), "item $position  clicked>>>", Toast.LENGTH_SHORT)
                .show()
            Log.d(TAG, "onCreate: ProductsAdapter init")
        })
        Log.d(TAG, "onCreate: ProductsAdapter init2")
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLastDefaultMenuBinding.inflate(inflater)
        binding.adapter = productsAdapter
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.setSimpleDrawerController(binding.drawerLayout,findNavController())

        binding.menuBtn.setOnClickListener {
            viewModel.setEvent(DefaultViewModelEvent.OpenDrawerEvent)
        }


        viewModel.simpleDrawerController.addClickableViews(
            ClickableView(
                binding.myAccountOption,
                "my account action",
                DefaultLastMenuFragmentDirections.actionDefaultLastMenuFragmentToMyAccountFragment()
            )
        )
        viewModel.simpleDrawerController.drawerEvent.observe(viewLifecycleOwner, Observer { action ->
            when (action) {
                "my account action" -> {
                    Toast.makeText(
                        this.requireContext(),
                        "this message observed from simpleDrawerController",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })

        viewModel.chipList.observe(viewLifecycleOwner, Observer {chipsNamesList->
            val chipGroup=binding.categoriesChips
            val inflater=LayoutInflater.from(chipGroup.context)
            val childrenChips=chipsNamesList.map {chipName->
                val chip=inflater.inflate(R.layout.chip_category_item,chipGroup,false)as Chip
                chip.text=chipName
                chip.tag=chipName
                chip.setOnCheckedChangeListener { compoundButton, ischecked ->
                    if (ischecked) {
                        viewModel.setEvent(DefaultViewModelEvent.ChangeFilterEvent(filter = compoundButton.tag.toString()))
                    }
                }
                chip
            }
            chipGroup.removeAllViews()
            for (chip in childrenChips){
                chipGroup.addView(chip)
            }

        })

        viewModel.filteredProductsList.observe(viewLifecycleOwner, Observer {filteredProductsList->
            productsAdapter.submitList(filteredProductsList)
            productsAdapter.notifyDataSetChanged()
        })


        return binding.root
    }


}