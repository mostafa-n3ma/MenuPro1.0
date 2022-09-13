package com.mostafan3ma.android.menupro10.localDataSource

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mostafan3ma.android.menupro10.data_Entities.cache_Entities.CacheCategory
import com.mostafan3ma.android.menupro10.data_Entities.cache_Entities.CacheItem
import com.mostafan3ma.android.menupro10.data_Entities.cache_Entities.CacheShop
import com.mostafan3ma.android.menupro10.data_Entities.cache_Entities.CategoryConverter

@Database(entities = [CacheShop::class,CacheCategory::class,CacheItem::class], version = 1, exportSchema = false)
@TypeConverters(CategoryConverter::class)
abstract class ShopDatabase:RoomDatabase() {

    abstract fun getDao():ShopDao

    companion object{
        const val DATABASE_NAME="ShopDatabase"
    }

}



