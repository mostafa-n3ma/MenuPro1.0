package com.mostafan3ma.android.menupro10.presentation.fragments.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.mostafan3ma.android.menupro10.oporations.data_Entities.Item
import com.mostafan3ma.android.menupro10.oporations.utils.ProductListItem
import com.mostafan3ma.android.menupro10.oporations.utils.ProductListItemStyle.*

class ProductsAdapter(
    private val productListener: ProductListener,
    private val productListItem: ProductListItem
) : ListAdapter<Item, ProductsViewHolders>(Product1DiffCallBack()) {

    companion object{
        const val TAG="ProductsAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolders {
        return when(productListItem.style){
            DEFAULT_ITEM -> {
                DefaultProductViewHolder.from(parent,productListItem)
            }
            MATERIAL_ITEM -> {
                MaterialProductViewHolder.from(parent,productListItem)
            }
            BAKERY_ITEM -> {
                BakeryProductViewHolder.from(parent,productListItem)
            }
            BLUR_ITEM -> {
                BlurProductViewHolder.from(parent,productListItem)
            }
            DEFAULT_CATEGORY_ITEM -> TODO()
            COLORIZES_CATEGORY_ITEM -> TODO()
        }
    }

    override fun onBindViewHolder(holder: ProductsViewHolders, position: Int) {
        val productItem: Item = getItem(position)
        when(holder){
            is DefaultProductViewHolder->{
                holder.bind(productItem,productListItem)
            }
            is MaterialProductViewHolder->{
                holder.bind(productItem,productListItem)
            }
            is BakeryProductViewHolder->{
                holder.bind(productItem,productListItem)
            }
            is BlurProductViewHolder->{
                holder.bind(productItem,productListItem)
            }
        }
        holder.itemView.setOnClickListener {
            productListener.listener(productItem)
        }
    }

}


class Product1DiffCallBack : DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem == newItem
    }


}


class ProductListener(val listener: (item: Item) -> Unit) {
    fun onClick(item: Item) = listener(item)
}













