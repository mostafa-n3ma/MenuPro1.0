package com.mostafan3ma.android.menupro10.presentation.fragments.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mostafan3ma.android.menupro10.databinding.ItemAddProductsBinding
import com.mostafan3ma.android.menupro10.oporations.data_Entities.Item

class AddProductsAdapter(private val context: Context,private val productsListener: ProductsListener)
    :ListAdapter<Item,AddProductsAdapter.ProductViewHolder>(ProductDiffCallBack())  {




    class ProductViewHolder(private val binding:ItemAddProductsBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(item:Item,context: Context){
            binding.item=item
            when(item){
                Item()->{
                    binding.itemCard!!.visibility=View.GONE
                    binding.addItemImg!!.visibility=View.VISIBLE
                }
                else->{
                    binding.itemCard!!.visibility=View.VISIBLE
                    binding.addItemImg!!.visibility=View.GONE
                }
            }
        }


        companion object {
            fun from(parent: ViewGroup): ProductViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemAddProductsBinding.inflate(layoutInflater, parent, false)
                return ProductViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val productItem=getItem(position)
        holder.bind(productItem,context)
        holder.itemView.setOnClickListener {
            productsListener.listener(position)
        }
    }
}

class ProductDiffCallBack:DiffUtil.ItemCallback<Item>(){
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem.name==newItem.name
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem==newItem
    }
    }



class ProductsListener(val listener:(position:Int)-> Unit){
    fun onClick(item: Item,position: Int)=listener(position)
}