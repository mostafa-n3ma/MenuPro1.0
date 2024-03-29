package com.mostafan3ma.android.menupro10.oporations.DataManagment.localDataSource

import android.util.Log
import com.mostafan3ma.android.menupro10.oporations.data_Entities.cache_Entities.CacheCategory
import com.mostafan3ma.android.menupro10.oporations.data_Entities.cache_Entities.CacheItem
import com.mostafan3ma.android.menupro10.oporations.data_Entities.cache_Entities.CacheShop
import com.mostafan3ma.android.menupro10.oporations.DataManagment.localDataSource.RoomDatabase.ShopDao
import javax.inject.Inject

class LocalDataSource
@Inject
constructor(private val shopDao: ShopDao):DefaultLocalDataSource
{

    companion object{
        const val TAG="LocalDataSource"
    }

    //shop
    override suspend fun insertShop(cacheShop: CacheShop): Long {
        Log.d(TAG, "insertShop: long=${shopDao.insertShop(cacheShop)}")
        return shopDao.insertShop(cacheShop)
    }
    override suspend fun getShop(): List<CacheShop> {
        return shopDao.getShop()
    }

    override suspend fun countShopTable(): Int {
        return shopDao.countShopTable()
    }

    override suspend fun deleteShop(cacheShop: CacheShop): Int {
        return shopDao.deleteShop(cacheShop)
    }
    override suspend fun clearShop() {
         shopDao.clearShop()
    }
    override suspend fun updateShop(cacheShop: CacheShop) {
        shopDao.updateShop(cacheShop)
    }




    //category
    override suspend fun insertCategory(cacheCategory: CacheCategory): Long {
        Log.d(TAG, "insertCategory: $shopDao.insertCategory(cacheCategory)")
        return shopDao.insertCategory(cacheCategory)

    }
    override suspend fun getCategories(): List<CacheCategory> {
        return shopDao.getCategories()
    }
    override suspend fun getCategoryByName(name: String): CacheCategory? {
        return shopDao.getCategoryByName(name)
    }
    override suspend fun deleteCategory(cacheCategory: CacheCategory): Int {
        return shopDao.deleteCategory(cacheCategory)
    }
    override suspend fun clearCategories() {
        shopDao.clearCategories()
    }
    override suspend fun updateCategory(cacheCategory: CacheCategory) {
        shopDao.updateCategory(cacheCategory)
    }




    //item
    override suspend fun insertItem(cacheItem: CacheItem): Long {
        Log.d(TAG, "insertItem: $shopDao.insertItem(cacheItem)")
        return shopDao.insertItem(cacheItem)
    }

    override suspend fun getItems(): List<CacheItem> {
       return shopDao.getItems()
    }

    override suspend fun getItemByName(name: String): CacheItem? {
        return shopDao.getItemByName(name)
    }

    override suspend fun deleteItem(cacheItem: CacheItem): Int {
        return shopDao.deleteItem(cacheItem)
    }

    override suspend fun clearItems() {
        shopDao.clearItems()
    }

    override suspend fun updateItem(cacheItem: CacheItem) {
        shopDao.updateItem(cacheItem)
    }


    override suspend fun emptyAllDatabase() {
        super.emptyAllDatabase()
    }

}