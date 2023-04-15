package com.mostafan3ma.android.menupro10.presentation.fragments.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mostafan3ma.android.menupro10.databinding.StyleChooserListItemBinding
import androidx.recyclerview.widget.ListAdapter

class StyleChooserAdapter(private val styleChooserListener: StyleChooserListener)
    :ListAdapter<StyleChooserItem, StyleChooserAdapter.StyleChooserViewHolder>(
    StyleChooserDiffCallBack()
) {


    class StyleChooserViewHolder(private val binding: StyleChooserListItemBinding) :
        RecyclerView.ViewHolder(binding.root){
            fun bind(item:StyleChooserItem){
                binding.item=item
            }
        companion object{
            fun from(parent: ViewGroup):StyleChooserViewHolder{
                val layoutInflater=LayoutInflater.from(parent.context)
                val binding=StyleChooserListItemBinding.inflate(layoutInflater,parent,false)
                return StyleChooserViewHolder(binding)
            }
        }
        }






    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StyleChooserViewHolder {
       return StyleChooserViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: StyleChooserViewHolder, position: Int) {
        val styleChooserItem=getItem(position)
        holder.bind(styleChooserItem)
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
