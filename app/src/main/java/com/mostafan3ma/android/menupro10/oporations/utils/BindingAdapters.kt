package com.mostafan3ma.android.menupro10.oporations.utils

import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mostafan3ma.android.menupro10.R

@BindingAdapter("setAdapter")
fun setAdapter(recyclerView: RecyclerView, adapter: ListAdapter<Any,RecyclerView.ViewHolder>) {
    recyclerView.adapter = adapter
}


@BindingAdapter("imgName")
fun imgName(imageView: ImageView, imgName: String?) {
    val superImageController = SuperImageController()
    var safetyImageName = "noImg"
    if (imgName != null) {
        safetyImageName = imgName
    }
    imageView.setImageBitmap(
        superImageController.getImageFromInternalStorage(
            imageView.context, safetyImageName,
            R.drawable.add_new_img
        )
    )
}

@BindingAdapter("displayUri")
fun displayUri(imageView: ImageView, uriString: String?="") {
    when (uriString) {
        "" -> {
            imageView.setImageResource(R.drawable.add_new_img)
        }
        else -> {
            imageView.setImageURI(uriString!!.toUri())}}}



@BindingAdapter("getInternalImg")
fun getInternalImg(imageView: ImageView,imgName:String?){
    val superImageController=SuperImageController()
    if (imgName!=null) {
        val tempBitMap = superImageController.getImageFromInternalStorage(
            imageView.context,
            imgName,
            R.drawable.add_new_img
        )
        imageView.setImageBitmap(tempBitMap)
    }



}

















