package com.mostafan3ma.android.menupro10.presentation.fragments.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import com.mostafan3ma.android.menupro10.databinding.ListItemItemChooserBinding
import com.mostafan3ma.android.menupro10.databinding.ListItemStyleChooserBinding




abstract class StyleChooserDefaultViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)




class MainStyleChooserViewHolder(private val binding: ListItemStyleChooserBinding) :
    StyleChooserDefaultViewHolder(binding.root) {
    fun bind(item:StyleChooserItem){
        binding.item=item
    }
    companion object{
        fun from(parent: ViewGroup):MainStyleChooserViewHolder{
            val layoutInflater=LayoutInflater.from(parent.context)
            val binding=ListItemStyleChooserBinding.inflate(layoutInflater,parent,false)
            return MainStyleChooserViewHolder(binding)
        }
    }
}

class ItemStyleChooserViewHolder(private val binding: ListItemItemChooserBinding):StyleChooserDefaultViewHolder(binding.root){

    fun bind(item:StyleChooserItem){
        binding.item=item
    }

    companion object{
        fun from(parent: ViewGroup):ItemStyleChooserViewHolder{
            val layoutInflater=LayoutInflater.from(parent.context)
            val binding=ListItemItemChooserBinding.inflate(layoutInflater,parent,false)
            return ItemStyleChooserViewHolder(binding)
        }
    }



}





class StyleChooserAdapter(private val styleChooserListener: StyleChooserListener,private val viewHolder:Int)
    :ListAdapter<StyleChooserItem, StyleChooserDefaultViewHolder>(
    StyleChooserDiffCallBack()
) {

    companion object{
        const val MAIN_STYLE_VIEW_HOLDER=1
        const val ITEM_STYLE_VIEW_HOLDER=2
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StyleChooserDefaultViewHolder {
        return when(viewHolder){
            MAIN_STYLE_VIEW_HOLDER->{
                MainStyleChooserViewHolder.from(parent)
            }
            ITEM_STYLE_VIEW_HOLDER->{
                ItemStyleChooserViewHolder.from(parent)
            }
            else->{
                MainStyleChooserViewHolder.from(parent)
            }
        }
    }

    override fun onBindViewHolder(holder: StyleChooserDefaultViewHolder, position: Int) {
        val styleChooserItem: StyleChooserItem =getItem(position)
       when(holder){
          is MainStyleChooserViewHolder->{
              holder.bind(styleChooserItem)
          }
           is ItemStyleChooserViewHolder->{
               holder.bind(styleChooserItem)
           }
       }
    //implement the clickListener
        holder.itemView.setOnClickListener {
            styleChooserListener.listener(styleChooserItem.styleCode)
        }
    }


}


class StyleChooserDiffCallBack:DiffUtil.ItemCallback<StyleChooserItem>(){
    override fun areItemsTheSame(oldItem: StyleChooserItem, newItem: StyleChooserItem): Boolean {
       return oldItem.isChecked==newItem.isChecked
    }

    override fun areContentsTheSame(oldItem: StyleChooserItem, newItem: StyleChooserItem): Boolean {
        return oldItem==newItem
    }

}



data class StyleChooserItem(
    val styleName: String,
    val styleImage: Drawable?,
    var isChecked: Boolean,
    var styleCode:Int
)


class StyleChooserListener(val listener:(styleCode:Int)->Unit){
    fun onClick(styleCode:Int)=listener(styleCode)
}


@BindingAdapter("markerChecked")
fun markerChecked(imageView: ImageView, isChecked: Boolean) {
    when(isChecked){
        true->imageView.visibility=View.VISIBLE
        false->imageView.visibility=View.INVISIBLE
    }
}
