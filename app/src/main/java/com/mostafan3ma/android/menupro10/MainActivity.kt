package com.mostafan3ma.android.menupro10

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import com.mostafan3ma.android.menupro10.databinding.ActivityMainBinding
import com.mostafan3ma.android.menupro10.oporations.utils.DataState
import com.mostafan3ma.android.menupro10.oporations.utils.SuperImageController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
     lateinit var imagePicker: SuperImageController



    private lateinit var binding: ActivityMainBinding




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btn.setOnClickListener {
            lifecycle.coroutineScope.launch{
                val url="https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/a1914478-large-1573052607.jpg?crop=0.705xw:0.529xh;0.188xw,0.298xh&resize=980:*"
                imagePicker.downloadUriToBitmap(this@MainActivity,url).onEach { dataState: DataState<Bitmap> ->
                    when(dataState){
                        is DataState.Success->{
                            binding.img.setImageBitmap(dataState.data)
                            binding.imageDownloaderPb.visibility=View.GONE
                            binding.saveBtn.setOnClickListener {
                                lifecycleScope.launch {
                                    imagePicker.saveImageToInternalStorage(this@MainActivity,dataState.data,"imageName2")
                                    Log.d("ImagePicker", "onCreate: image saved Successfully")
                                }
                            }
                        }
                        is DataState.Error->{
                            binding.imageDownloaderPb.visibility=View.GONE
                            Toast.makeText(this@MainActivity,"Error",Toast.LENGTH_SHORT).show()
                            binding.img.setImageResource(R.drawable.broken_image)
                        }
                        is DataState.Loading->{
                            binding.imageDownloaderPb.visibility=View.VISIBLE
                        }
                    }

                }.launchIn(lifecycle.coroutineScope)
            }
        }

        binding.extractBtn.setOnClickListener {
            lifecycleScope.launch {
                val bitmap: Bitmap? =imagePicker.getImageFromInternalStorage(this@MainActivity,"imageName2",R.drawable.broken_image)
                binding.img.setImageBitmap(bitmap)
            }
        }

        binding.deleteBtn.setOnClickListener {
            lifecycleScope.launch {
                if (imagePicker.deleteImage(this@MainActivity,"imageName2")){
                    Toast.makeText(this@MainActivity,"deleted",Toast.LENGTH_SHORT).show()
                }
            }
        }



    }




}


//lifecycle.coroutineScope.launch {
////           repository.insertShop(CacheShop(id = "1", name = "shop1"))
////           repository.refreshCacheDatabase(lifecycle.coroutineScope)
//
////         repository.getShop().onEach {dataState ->
////             txt.text=when(dataState){
////                 is DataState.Success-> dataState.data[0].phoneNumber
////                 is DataState.Error-> "Error"
////                 is DataState.Loading->"Loading"
////             }
////         }.launchIn(lifecycle.coroutineScope)
//}