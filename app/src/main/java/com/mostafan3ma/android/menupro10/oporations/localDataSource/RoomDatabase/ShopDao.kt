package com.mostafan3ma.android.menupro10.oporations.localDataSource.RoomDatabase

import androidx.room.*
import com.mostafan3ma.android.menupro10.oporations.data_Entities.cache_Entities.CacheCategory
import com.mostafan3ma.android.menupro10.oporations.data_Entities.cache_Entities.CacheItem
import com.mostafan3ma.android.menupro10.oporations.data_Entities.cache_Entities.CacheShop

@Dao
interface ShopDao {

//Shop
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShop(CacheShop: CacheShop):Long

    @Query("SELECT * FROM shop")
    suspend fun getShop():List<CacheShop>

    @Query("SELECT COUNT(*) FROM shop")
    suspend fun countShopTable():Int


    @Delete
    suspend fun deleteShop(cacheShop: CacheShop):Int

    @Query("DELETE FROM SHOP")
    suspend fun clearShop()

    @Update
    suspend fun updateShop(CacheShop: CacheShop)





//Category
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(cacheCategory: CacheCategory):Long

    @Query("SELECT * FROM categories")
    suspend fun getCategories():List<CacheCategory>

    @Query("SELECT * FROM categories WHERE name=:name ")
    suspend fun getCategoryByName(name:String):CacheCategory?

    @Delete
    suspend fun deleteCategory(cacheCategory: CacheCategory):Int

    @Query("DELETE FROM categories")
    suspend fun clearCategories()

    @Update
    suspend fun updateCategory(cacheCategory: CacheCategory)


//Item
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(cacheItem: CacheItem):Long

    @Query("SELECT * FROM items")
    suspend fun getItems():List<CacheItem>

    @Query("SELECT * FROM items WHERE name=:name ")
    suspend fun getItemByName(name:String):CacheItem?

    @Delete
    suspend fun deleteItem(cacheItem: CacheItem):Int

    @Query("DELETE FROM items")
    suspend fun clearItems()

    @Update
    suspend fun updateItem(cacheItem: CacheItem)



}