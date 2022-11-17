package com.mostafan3ma.android.menupro10.oporations.utils

import android.net.Uri
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mostafan3ma.android.menupro10.R
import com.mostafan3ma.android.menupro10.presentation.startingFragments.adapters.AddCategoriesAdapter

@BindingAdapter("setAdapter")
fun setAdapter(recyclerView: RecyclerView, adapter: AddCategoriesAdapter) {
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
fun displayUri(imageView: ImageView, uriString: String) {
    when (uriString) {
        "" -> {
            imageView.setImageResource(R.drawable.add_new_img)
        }
        else -> {
            imageView.setImageURI(uriString.toUri())
        }

    }


}