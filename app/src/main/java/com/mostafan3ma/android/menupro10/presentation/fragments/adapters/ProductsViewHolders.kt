package com.mostafan3ma.android.menupro10.presentation.fragments.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.createBitmap
import androidx.recyclerview.widget.RecyclerView
import com.mostafan3ma.android.menupro10.databinding.ItemBakeryBinding
import com.mostafan3ma.android.menupro10.databinding.ItemBlureProBinding
import com.mostafan3ma.android.menupro10.databinding.ItemDefaultBinding
import com.mostafan3ma.android.menupro10.databinding.ItemMaterialBinding
import com.mostafan3ma.android.menupro10.oporations.data_Entities.Item
import com.mostafan3ma.android.menupro10.oporations.utils.ProductListItem
import com.mostafan3ma.android.menupro10.oporations.utils.Style
import kotlin.math.log

abstract class ProductsViewHolders(itemView: View) : RecyclerView.ViewHolder(itemView)

class DefaultProductViewHolder(private val binding: ItemDefaultBinding) :
    ProductsViewHolders(binding.root) {
    fun bind(item: Item, productListItem: ProductListItem) {
        val check = item.price.endsWith(productListItem.concurrency_type_text)
        if (item.price.endsWith(productListItem.concurrency_type_text)) {
            binding.item = item
        } else {
            item.price = "${item.price} ${productListItem.concurrency_type_text}"
            binding.item = item
        }

    }

    companion object {
        fun from(parent: ViewGroup, productListItem: ProductListItem): DefaultProductViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemDefaultBinding.inflate(layoutInflater, parent, false)
            observeStyleAttr(binding, productListItem)
            return DefaultProductViewHolder(binding)
        }

        private fun observeStyleAttr(
            binding: ItemDefaultBinding,
            productListItem: ProductListItem
        ) {
            when (productListItem.background_color) {
                ProductListItem.DEFAULT_COLOR_VALUE -> {}
                else -> {
                    binding.itemCard.setBackgroundColor(productListItem.background_color)
                }
            }
            when (productListItem.name_text_size) {
                ProductListItem.DEFAULT_SIZE -> {}
                "Small" -> {
                    //18
                    binding.itemName.textSize = 18F
                }
                "Medium" -> {
                    //20
                    binding.itemName.textSize = 20F
                }
                "Large" -> {
                    //30sp
                    binding.itemName.textSize = 30F
                }
                "X-Large" -> {
                    binding.itemName.textSize = 35F
                }
                else -> {
                    //also stay on the default textSize
                }
            }
            when (productListItem.name_text_color) {
                ProductListItem.DEFAULT_COLOR_VALUE -> {}
                else -> {
                    binding.itemName.setTextColor(productListItem.name_text_color)
                }
            }
            when (productListItem.description_text_size) {
                ProductListItem.DEFAULT_SIZE -> {}
                "Small" -> {
                    //18
                    binding.itemDescription.textSize = 18F
                }
                "Medium" -> {
                    //20
                    binding.itemDescription.textSize = 20F
                }
                "Large" -> {
                    //30sp
                    binding.itemDescription.textSize = 30F
                }
                "X-Large" -> {
                    binding.itemDescription.textSize = 35F
                }
                else -> {
                    //also stay on the default textSize
                }
            }
            when (productListItem.description_text_color) {
                ProductListItem.DEFAULT_COLOR_VALUE -> {}
                else -> {
                    binding.itemDescription.setTextColor(productListItem.description_text_color)
                }
            }
            when (productListItem.concurrency_text_size) {
                ProductListItem.DEFAULT_SIZE -> {}
                "Small" -> {
                    //18
                    binding.itemPrice.textSize = 18F
                }
                "Medium" -> {
                    //20
                    binding.itemPrice.textSize = 20F
                }
                "Large" -> {
                    //30sp
                    binding.itemPrice.textSize = 30F
                }
                "X-Large" -> {
                    binding.itemPrice.textSize = 35F
                }
                else -> {
                    //also stay on the default textSize
                }
            }
            when (productListItem.concurrency_text_Color) {
                ProductListItem.DEFAULT_COLOR_VALUE -> {}
                else -> {
                    binding.itemPrice.setTextColor(productListItem.concurrency_text_Color)
                }
            }
            when (productListItem.size_text_size) {
                ProductListItem.DEFAULT_SIZE -> {}
                "Small" -> {
                    //18
                    binding.itemSize.textSize = 18F
                }
                "Medium" -> {
                    //20
                    binding.itemSize.textSize = 20F
                }
                "Large" -> {
                    //30sp
                    binding.itemSize.textSize = 30F
                }
                "X-Large" -> {
                    binding.itemSize.textSize = 35F
                }
                else -> {
                    //also stay on the default textSize
                }
            }
            when (productListItem.size_text_color) {
                ProductListItem.DEFAULT_COLOR_VALUE -> {}
                else -> {
                    binding.itemSize.setTextColor(productListItem.size_text_color)
                }
            }

        }
    }
}

class MaterialProductViewHolder(private val binding: ItemMaterialBinding) :
    ProductsViewHolders(binding.root) {
    fun bind(item: Item, productListItem: ProductListItem) {
        val check = item.price.endsWith(productListItem.concurrency_type_text)
        if (item.price.endsWith(productListItem.concurrency_type_text)) {
            binding.item = item
        } else {
            item.price = "${item.price} ${productListItem.concurrency_type_text}"
            binding.item = item
        }

    }

    companion object {
        fun from(parent: ViewGroup, productListItem: ProductListItem): MaterialProductViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemMaterialBinding.inflate(layoutInflater, parent, false)
            observeStyleAttr(binding, productListItem)
            return MaterialProductViewHolder(binding)
        }

        private fun observeStyleAttr(
            binding: ItemMaterialBinding,
            productListItem: ProductListItem
        ) {
            when (productListItem.background_color) {
                ProductListItem.DEFAULT_COLOR_VALUE -> {}
                else -> {
                    binding.itemCard.setBackgroundColor(productListItem.background_color)
                }
            }
            when (productListItem.name_text_size) {
                ProductListItem.DEFAULT_SIZE -> {}
                "Small" -> {
                    //18
                    binding.itemName.textSize = 18F
                }
                "Medium" -> {
                    //20
                    binding.itemName.textSize = 20F
                }
                "Large" -> {
                    //30sp
                    binding.itemName.textSize = 30F
                }
                "X-Large" -> {
                    binding.itemName.textSize = 35F
                }
                else -> {
                    //also stay on the default textSize
                }
            }
            when (productListItem.name_text_color) {
                ProductListItem.DEFAULT_COLOR_VALUE -> {}
                else -> {
                    binding.itemName.setTextColor(productListItem.name_text_color)
                }
            }
            when (productListItem.concurrency_text_size) {
                ProductListItem.DEFAULT_SIZE -> {}
                "Small" -> {
                    //18
                    binding.itemPrice.textSize = 18F
                }
                "Medium" -> {
                    //20
                    binding.itemPrice.textSize = 20F
                }
                "Large" -> {
                    //30sp
                    binding.itemPrice.textSize = 30F
                }
                "X-Large" -> {
                    binding.itemPrice.textSize = 35F
                }
                else -> {
                    //also stay on the default textSize
                }
            }
            when (productListItem.concurrency_text_Color) {
                ProductListItem.DEFAULT_COLOR_VALUE -> {}
                else -> {
                    binding.itemPrice.setTextColor(productListItem.concurrency_text_Color)
                }
            }
            when (productListItem.size_text_size) {
                ProductListItem.DEFAULT_SIZE -> {}
                "Small" -> {
                    //18
                    binding.itemSize.textSize = 18F
                }
                "Medium" -> {
                    //20
                    binding.itemSize.textSize = 20F
                }
                "Large" -> {
                    //30sp
                    binding.itemSize.textSize = 30F
                }
                "X-Large" -> {
                    binding.itemSize.textSize = 35F
                }
                else -> {
                    //also stay on the default textSize
                }
            }
            when (productListItem.size_text_color) {
                ProductListItem.DEFAULT_COLOR_VALUE -> {}
                else -> {
                    binding.itemSize.setTextColor(productListItem.size_text_color)
                }
            }

        }
    }
}

class BakeryProductViewHolder(private val binding: ItemBakeryBinding) :
    ProductsViewHolders(binding.root) {
    fun bind(item: Item, productListItem: ProductListItem) {
        val check = item.price.endsWith(productListItem.concurrency_type_text)
        if (item.price.endsWith(productListItem.concurrency_type_text)) {
            binding.item = item
        } else {
            item.price = "${item.price} ${productListItem.concurrency_type_text}"
            binding.item = item
        }

    }

    companion object {
        fun from(parent: ViewGroup, productListItem: ProductListItem): BakeryProductViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemBakeryBinding.inflate(layoutInflater, parent, false)
            observeStyleAttr(binding, productListItem)
            return BakeryProductViewHolder(binding)
        }

        private fun observeStyleAttr(binding: ItemBakeryBinding, productListItem: ProductListItem)
        {
            when (productListItem.background_color) {
                ProductListItem.DEFAULT_COLOR_VALUE -> {}
                else -> {
                    binding.itemCard.setBackgroundColor(productListItem.background_color)
                }
            }
            when (productListItem.name_text_size) {
                ProductListItem.DEFAULT_SIZE -> {}
                "Small" -> {
                    //18
                    binding.itemName.textSize = 18F
                }
                "Medium" -> {
                    //20
                    binding.itemName.textSize = 20F
                }
                "Large" -> {
                    //30sp
                    binding.itemName.textSize = 30F
                }
                "X-Large" -> {
                    binding.itemName.textSize = 35F
                }
                else -> {
                    //also stay on the default textSize
                }
            }
            when (productListItem.name_text_color) {
                ProductListItem.DEFAULT_COLOR_VALUE -> {}
                else -> {
                    binding.itemName.setTextColor(productListItem.name_text_color)
                }
            }
            when (productListItem.concurrency_text_size) {
                ProductListItem.DEFAULT_SIZE -> {}
                "Small" -> {
                    //18
                    binding.itemPrice.textSize = 18F
                }
                "Medium" -> {
                    //20
                    binding.itemPrice.textSize = 20F
                }
                "Large" -> {
                    //30sp
                    binding.itemPrice.textSize = 30F
                }
                "X-Large" -> {
                    binding.itemPrice.textSize = 35F
                }
                else -> {
                    //also stay on the default textSize
                }
            }
            when (productListItem.concurrency_text_Color) {
                ProductListItem.DEFAULT_COLOR_VALUE -> {}
                else -> {
                    binding.itemPrice.setTextColor(productListItem.concurrency_text_Color)
                }
            }
            when (productListItem.size_text_size) {
                ProductListItem.DEFAULT_SIZE -> {}
                "Small" -> {
                    //18
                    binding.itemSize.textSize = 18F
                }
                "Medium" -> {
                    //20
                    binding.itemSize.textSize = 20F
                }
                "Large" -> {
                    //30sp
                    binding.itemSize.textSize = 30F
                }
                "X-Large" -> {
                    binding.itemSize.textSize = 35F
                }
                else -> {
                    //also stay on the default textSize
                }
            }
            when (productListItem.size_text_color) {
                ProductListItem.DEFAULT_COLOR_VALUE -> {}
                else -> {
                    binding.itemSize.setTextColor(productListItem.size_text_color)
                }
            }

        }
    }
}

class BlurProductViewHolder(private val binding: ItemBlureProBinding) :
    ProductsViewHolders(binding.root) {
    fun bind(item: Item, productListItem: ProductListItem) {
        val check = item.price.endsWith(productListItem.concurrency_type_text)
        if (item.price.endsWith(productListItem.concurrency_type_text)) {
            binding.item = item
        } else {
            item.price = "${item.price} ${productListItem.concurrency_type_text}"
            binding.item = item
        }

    }

    companion object {
        fun from(parent: ViewGroup, productListItem: ProductListItem): BlurProductViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemBlureProBinding.inflate(layoutInflater, parent, false)
            observeStyleAttr(binding, productListItem)
            return BlurProductViewHolder(binding)
        }

        private fun observeStyleAttr(binding: ItemBlureProBinding, productListItem: ProductListItem)
        {
            when (productListItem.background_color) {
                ProductListItem.DEFAULT_COLOR_VALUE -> {}
                else -> {
                    binding.itemCard.setBackgroundColor(productListItem.background_color)
                }
            }
            when (productListItem.name_text_size) {
                ProductListItem.DEFAULT_SIZE -> {}
                "Small" -> {
                    //18
                    binding.itemName.textSize = 18F
                }
                "Medium" -> {
                    //20
                    binding.itemName.textSize = 20F
                }
                "Large" -> {
                    //30sp
                    binding.itemName.textSize = 30F
                }
                "X-Large" -> {
                    binding.itemName.textSize = 35F
                }
                else -> {
                    //also stay on the default textSize
                }
            }
            when (productListItem.name_text_color) {
                ProductListItem.DEFAULT_COLOR_VALUE -> {}
                else -> {
                    binding.itemName.setTextColor(productListItem.name_text_color)
                }
            }
            when (productListItem.description_text_size) {
                ProductListItem.DEFAULT_SIZE -> {}
                "Small" -> {
                    //18
                    binding.itemDescription.textSize = 18F
                }
                "Medium" -> {
                    //20
                    binding.itemDescription.textSize = 20F
                }
                "Large" -> {
                    //30sp
                    binding.itemDescription.textSize = 30F
                }
                "X-Large" -> {
                    binding.itemDescription.textSize = 35F
                }
                else -> {
                    //also stay on the default textSize
                }
            }
            when (productListItem.description_text_color) {
                ProductListItem.DEFAULT_COLOR_VALUE -> {}
                else -> {
                    binding.itemDescription.setTextColor(productListItem.description_text_color)
                }
            }
            when (productListItem.concurrency_text_size) {
                ProductListItem.DEFAULT_SIZE -> {}
                "Small" -> {
                    //18
                    binding.itemPrice.textSize = 18F
                }
                "Medium" -> {
                    //20
                    binding.itemPrice.textSize = 20F
                }
                "Large" -> {
                    //30sp
                    binding.itemPrice.textSize = 30F
                }
                "X-Large" -> {
                    binding.itemPrice.textSize = 35F
                }
                else -> {
                    //also stay on the default textSize
                }
            }
            when (productListItem.concurrency_text_Color) {
                ProductListItem.DEFAULT_COLOR_VALUE -> {}
                else -> {
                    binding.itemPrice.setTextColor(productListItem.concurrency_text_Color)
                }
            }
            when (productListItem.size_text_size) {
                ProductListItem.DEFAULT_SIZE -> {}
                "Small" -> {
                    //18
                    binding.itemSize.textSize = 18F
                }
                "Medium" -> {
                    //20
                    binding.itemSize.textSize = 20F
                }
                "Large" -> {
                    //30sp
                    binding.itemSize.textSize = 30F
                }
                "X-Large" -> {
                    binding.itemSize.textSize = 35F
                }
                else -> {
                    //also stay on the default textSize
                }
            }
            when (productListItem.size_text_color) {
                ProductListItem.DEFAULT_COLOR_VALUE -> {}
                else -> {
                    binding.itemSize.setTextColor(productListItem.size_text_color)
                }
            }

        }
    }
}














