package com.mostafan3ma.android.menupro10.oporations.DataManagment.repository

import android.app.Activity
import android.net.Uri
import com.mostafan3ma.android.menupro10.oporations.data_Entities.DomainModel
import com.mostafan3ma.android.menupro10.oporations.data_Entities.NetworkEntity
import com.mostafan3ma.android.menupro10.oporations.data_Entities.cache_Entities.CacheCategory
import com.mostafan3ma.android.menupro10.oporations.data_Entities.cache_Entities.CacheItem
import com.mostafan3ma.android.menupro10.oporations.data_Entities.cache_Entities.CacheShop
import com.mostafan3ma.android.menupro10.oporations.utils.DataState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface DefaultShopRepository {

    //Local 1
    //1.1 Shop
    suspend fun insertShop(cacheShop: CacheShop): Long
    suspend fun getShop(): Flow<DataState<List<CacheShop>>>
    suspend fun countShopTable(): Int
    suspend fun deleteShop(cacheShop: CacheShop): Int
    suspend fun clearShop()
    suspend fun updateShop(cacheShop: CacheShop)

    //1.2 Category
    suspend fun insertCategory(cacheCategory: CacheCategory): Long
    suspend fun getCategories(): Flow<DataState<List<CacheCategory>>>
    suspend fun getCategoryByName(name: String): Flow<DataState<CacheCategory?>>
    suspend fun deleteCategory(cacheCategory: CacheCategory): Int
    suspend fun clearCategories()
    suspend fun updateCategory(cacheCategory: CacheCategory)

    //1.3 Item
    suspend fun insertItem(cacheItem: CacheItem): Long
    suspend fun getItems(): Flow<DataState<List<CacheItem>>>
    suspend fun getItemByName(name: String): Flow<DataState<CacheItem?>>
    suspend fun deleteItem(cacheItem: CacheItem): Int
    suspend fun clearItems()
    suspend fun updateItem(cacheItem: CacheItem)


    suspend fun emptyAllDatabase() {
        clearShop()
        clearCategories()
        clearItems()
    }


    //2 Remote
    suspend fun downloadRemoteData(collectionName: String, shopName: String): Flow<DataState<NetworkEntity?>>
    suspend fun uploadCacheData(collectionName: String, shopName: String, entity: NetworkEntity): Boolean
    suspend fun refreshCacheDatabase(scope: CoroutineScope)
    suspend fun refreshRemoteDatabase(scope: CoroutineScope)
    suspend fun getCacheDomainShop(): Flow<DataState<DomainModel>>
    suspend fun uploadImageToFirebaseStorage(shopName: String, imgName: String, imgUri: Uri): Boolean
    suspend fun getImgDownloadUri(shopName: String, imgName: String): Uri?
    fun getUserPhoneNumber():String
    fun requestPhoneNumberVerificationCode(phoneNumber: String,activity: Activity)
    fun resendCodeToVerifyPhoneNumber(phoneNumber: String,activity: Activity)
    suspend fun signIn(code:String):String?
    fun logOut()
}