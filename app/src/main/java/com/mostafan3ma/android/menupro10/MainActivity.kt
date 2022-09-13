package com.mostafan3ma.android.menupro10

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.mostafan3ma.android.menupro10.data_Entities.cache_Entities.CacheCategory
import com.mostafan3ma.android.menupro10.data_Entities.cache_Entities.CacheItem
import com.mostafan3ma.android.menupro10.data_Entities.cache_Entities.CacheShop
import com.mostafan3ma.android.menupro10.data_Entities.getCategory
import com.mostafan3ma.android.menupro10.localDataSource.ShopDao
import com.mostafan3ma.android.menupro10.localDataSource.ShopDatabase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var databaseDao: ShopDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
}