package com.mostafan3ma.android.menupro10.oporations.localDataSource

import com.mostafan3ma.android.menupro10.oporations.data_Entities.cache_Entities.CacheCategory
import com.mostafan3ma.android.menupro10.oporations.data_Entities.cache_Entities.CacheItem
import com.mostafan3ma.android.menupro10.oporations.data_Entities.cache_Entities.CacheShop

interface DefaultLocalDataSource {

   suspend fun insertShop(cacheShop: CacheShop):Long
   suspend fun getShop():List<CacheShop>
   suspend fun countShopTable():Int
   suspend fun deleteShop(cacheShop: CacheShop):Int
   suspend fun clearShop()
   suspend fun updateShop(cacheShop: CacheShop)



   suspend fun insertCategory(cacheCategory: CacheCategory):Long
   suspend fun getCategories():List<CacheCategory>
   suspend fun getCategoryByName(name:String):CacheCategory?
   suspend fun deleteCategory(cacheCategory: CacheCategory):Int
   suspend fun clearCategories()
   suspend fun updateCategory(cacheCategory: CacheCategory)



   suspend fun insertItem(cacheItem: CacheItem):Long
   suspend fun getItems():List<CacheItem>
   suspend fun getItemByName(name:String):CacheItem?
   suspend fun deleteItem(cacheItem: CacheItem):Int
   suspend fun clearItems()
   suspend fun updateItem(cacheItem: CacheItem)


   suspend fun emptyAllDatabase(){
      clearShop()
      clearCategories()
      clearItems()
   }


}