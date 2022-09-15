package com.mostafan3ma.android.menupro10

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.coroutineScope
import com.mostafan3ma.android.menupro10.databinding.ActivityMainBinding
import com.mostafan3ma.android.menupro10.oporations.data_Entities.NetworkEntity
import com.mostafan3ma.android.menupro10.oporations.data_Entities.cache_Entities.CacheShop
import com.mostafan3ma.android.menupro10.oporations.localDataSource.LocalDataSource
import com.mostafan3ma.android.menupro10.oporations.remoteDataSource.RemoteDataSource
import com.mostafan3ma.android.menupro10.oporations.repository.ShopRepository
import com.mostafan3ma.android.menupro10.oporations.utils.DataState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var repository: ShopRepository

    @Inject
    lateinit var remoteDataSource: RemoteDataSource

    @Inject
    lateinit var localDataSource: LocalDataSource


    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



       lifecycle.coroutineScope.launch{
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
