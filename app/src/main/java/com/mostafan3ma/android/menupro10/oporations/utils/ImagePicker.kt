package com.mostafan3ma.android.menupro10.oporations.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.fragment.app.Fragment
import com.mostafan3ma.android.menupro10.MainActivity
import javax.inject.Inject

class ImagePicker
@Inject
constructor() {

    /**
     * - it initialize in onResume fun of the host
     * - else it wil still null

     */
    var returnedUri: Uri? = null
    private lateinit var registrar: ActivityResultLauncher<String>


    /**
     * - register the ActivityResultLauncher<String>
     * - set listener and get the uri
     */
    fun register(host: Any) {
        when (host) {
            is ComponentActivity -> {
                this.registrar =
                    host.registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                        // Handle the returned Uri
                        returnedUri = uri!!
                    }
            }
            is Fragment -> {
                this.registrar =
                    host.registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                        // Handle the returned Uri
                        returnedUri = uri!!
                    }
            }
            else -> {

            }
        }
    }

    /**
     * launch the registrar and open the image chooser
     */
    fun launchRegistrar() {
        registrar.launch("image/*")
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun getBitmap(context: Context):Bitmap{
        val source: ImageDecoder.Source = ImageDecoder.createSource(context.contentResolver,returnedUri!!)
        return ImageDecoder.decodeBitmap(source)
    }


    /**
     * works in an Activity(ComponentActivity)
     * use it to open Image chooser and let the user to pick image from the gallery and then display it on an imageView
     */
    @RequiresApi(Build.VERSION_CODES.P)
    fun pickImgFromActivityAndDisplay(
        component: ComponentActivity,
        img: ImageView,
        btn: Button
    ) {
        val getContent =
            component.registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                // Handle the returned Uri
                val source: ImageDecoder.Source = ImageDecoder.createSource(component.contentResolver,returnedUri!!)
                img.setImageBitmap(ImageDecoder.decodeBitmap(source))
            }

        btn.setOnClickListener {
            //            chooseImage()
            getContent.launch("image/*")
        }
    }


    /**
     * works in a Fragment
    use it to open Image chooser and let the user to pick image from the gallery and then display it on an imageView
     **/
    @RequiresApi(Build.VERSION_CODES.P)
    fun pickImgFromFragmentAndDisplay(
        component: Fragment,
        context: Context,
        img: ImageView,
        btn: Button
    ) {
        val getContent =
            component.registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                // Handle the returned Uri
                val source: ImageDecoder.Source = ImageDecoder.createSource(context.contentResolver,returnedUri!!)
                img.setImageBitmap(ImageDecoder.decodeBitmap(source))
            }

        btn.setOnClickListener {
            //            chooseImage()
            getContent.launch("image/*")
        }
    }


}

