package com.mostafan3ma.android.menupro10.presentation.fragments.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mostafan3ma.android.menupro10.databinding.ItemProduct1Binding
import com.mostafan3ma.android.menupro10.oporations.data_Entities.Item

class ProductsAdapter (private val productListener1: ProductListener1)
    :ListAdapter<Item,ProductsAdapter.Product1ViewHolder>(Product1DiffCallBack()){




    class Product1ViewHolder(private val binding:ItemProduct1Binding):RecyclerView.ViewHolder(binding.root){
        fun bind(item: Item){
            binding.item=item
        }

        companion object{
            fun from(parent:ViewGroup):Product1ViewHolder{
                val layoutInflater=LayoutInflater.from(parent.context)
                val binding=ItemProduct1Binding.inflate(layoutInflater,parent,false)
                return Product1ViewHolder(binding)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Product1ViewHolder {
        return Product1ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: Product1ViewHolder, position: Int) {
        val productItem=getItem(position)
        holder.bind(productItem)
        holder.itemView.setOnClickListener {
            productListener1.listener(position)
        }
    }

}






class Product1DiffCallBack:DiffUtil.ItemCallback<Item>(){
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem.name==newItem.name
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem==newItem
    }


}


















class ProductListener1(val listener:(position:Int)->Unit){
    fun onClick(position: Int)=listener(position)
}













