package com.mostafan3ma.android.menupro10

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.ImageDecoder.decodeBitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.decodeBitmap
import androidx.lifecycle.coroutineScope
import com.mostafan3ma.android.menupro10.databinding.ActivityMainBinding
import com.mostafan3ma.android.menupro10.oporations.localDataSource.LocalDataSource
import com.mostafan3ma.android.menupro10.oporations.remoteDataSource.RemoteDataSource
import com.mostafan3ma.android.menupro10.oporations.repository.ShopRepository
import com.mostafan3ma.android.menupro10.oporations.utils.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    @Inject
    lateinit var imagePicker: ImagePicker


    private lateinit var binding: ActivityMainBinding


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onResume() {
        super.onResume()
        if (imagePicker.returnedUri !=null){
            Toast.makeText(this,imagePicker.returnedUri.toString(),Toast.LENGTH_LONG).show()
            binding.img.setImageBitmap(imagePicker.getBitmap(this))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        imagePicker.register(this)

        binding.chooseImageBtn.setOnClickListener {
            imagePicker.launchRegistrar()
        }




        lifecycle.coroutineScope.launch {
//           repository.insertShop(CacheShop(id = "1", name = "shop1"))
//           repository.refreshCacheDatabase(lifecycle.coroutineScope)

//         repository.getShop().onEach {dataState ->
//             txt.text=when(dataState){
//                 is DataState.Success-> dataState.data[0].phoneNumber
//                 is DataState.Error-> "Error"
//                 is DataState.Loading->"Loading"
//             }
//         }.launchIn(lifecycle.coroutineScope)
        }

    }


}
