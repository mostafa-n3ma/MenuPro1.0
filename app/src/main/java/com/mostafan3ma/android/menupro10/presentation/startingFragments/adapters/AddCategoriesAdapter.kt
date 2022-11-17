package com.mostafan3ma.android.menupro10.presentation.startingFragments.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mostafan3ma.android.menupro10.R
import com.mostafan3ma.android.menupro10.databinding.ItemAddCategoreyBinding
import com.mostafan3ma.android.menupro10.oporations.data_Entities.Category
import com.mostafan3ma.android.menupro10.oporations.utils.SuperImageController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddCategoriesAdapter(private val context:Context,private val coroutineScope:CoroutineScope,private val categoryListener: CategoryListener)
    :ListAdapter<Category,AddCategoriesAdapter.CategoryViewHolder>(CategoryDiffCallBack()) {

    val defaultCategoryItem=Category()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val categoryItem=getItem(position)
        holder.bind(categoryItem,context,coroutineScope)
        holder.itemView.setOnClickListener {
            categoryListener.clickListener(position)
        }



    }


    class CategoryViewHolder(private val binding:ItemAddCategoreyBinding):RecyclerView.ViewHolder(binding.root){
        val superImageController =SuperImageController()
        fun bind(item: Category, context: Context, coroutineScope: CoroutineScope){
            binding.categoryItem=item
            when(item){
                Category()->{
                    binding.categoryCard!!.visibility=View.GONE
                    binding.addImg!!.visibility=View.VISIBLE
                }
                else->{
                    binding.categoryCard!!.visibility=View.VISIBLE
                    binding.addImg!!.visibility=View.GONE

                    coroutineScope.launch{
//                        binding.categoryImg!!.setImageBitmap(
//                            superImageController
//                                .getImageFromInternalStorage(
//                                    context,
//                                    item.imageName,
//                                    R.drawable.default_category
//                                )
//                        )
                    }
                }
            }


            binding.executePendingBindings()
        }

        companion object{
            fun from(parent: ViewGroup):CategoryViewHolder{
                val layoutInflater=LayoutInflater.from(parent.context)
                val binding=ItemAddCategoreyBinding.inflate(layoutInflater,parent,false)
                return CategoryViewHolder(binding)
            }
        }


    }

}

class CategoryDiffCallBack:DiffUtil.ItemCallback<Category>() {
    override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem==newItem
    }

}

class CategoryListener(val clickListener: (position:Int)-> Unit ){

    fun onClick(category: Category, position:Int)=clickListener(position)
}




