package com.mostafan3ma.android.menupro10.oporations.DataManagment.repository

import android.app.Activity
import android.net.Uri
import android.util.Log
import com.mostafan3ma.android.menupro10.oporations.data_Entities.DomainModel
import com.mostafan3ma.android.menupro10.oporations.data_Entities.NetworkEntity
import com.mostafan3ma.android.menupro10.oporations.data_Entities.cache_Entities.CacheCategory
import com.mostafan3ma.android.menupro10.oporations.data_Entities.cache_Entities.CacheItem
import com.mostafan3ma.android.menupro10.oporations.data_Entities.cache_Entities.CacheShop
import com.mostafan3ma.android.menupro10.oporations.DataManagment.localDataSource.LocalDataSource
import com.mostafan3ma.android.menupro10.oporations.DataManagment.remoteDataSource.RemoteDataSource
import com.mostafan3ma.android.menupro10.oporations.utils.DataState
import com.mostafan3ma.android.menupro10.oporations.utils.EntitiesMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import kotlin.Exception

class ShopRepository
@Inject
constructor(private val localDataSource: LocalDataSource,
            private val remoteDataSource: RemoteDataSource,
            private val entityMapper: EntitiesMapper) :
    DefaultShopRepository {

    //Local 1
    //1.1 Shop
    override suspend fun insertShop(cacheShop: CacheShop): Long {
        return localDataSource.insertShop(cacheShop)
    }

    override suspend fun getShop(): Flow<DataState<List<CacheShop>>> = flow {
        emit(DataState.Loading)
            try {
                if (localDataSource.countShopTable() !=0){
                    val shopList: List<CacheShop> = localDataSource.getShop()
                    emit(DataState.Success(shopList))
                }else{
                    return@flow
                }

            }catch (e:Exception){
                emit(DataState.Error(e))
            }


    }

    override suspend fun countShopTable(): Int {
        return localDataSource.countShopTable()
    }

    override suspend fun deleteShop(cacheShop: CacheShop): Int = localDataSource.deleteShop(cacheShop)

    override suspend fun clearShop() {
        localDataSource.clearShop()
    }

    override suspend fun updateShop(cacheShop: CacheShop) {
        localDataSource.updateShop(cacheShop)
    }


    //1.2 Category
    override suspend fun insertCategory(cacheCategory: CacheCategory): Long =
        localDataSource.insertCategory(cacheCategory)

    override suspend fun getCategories(): Flow<DataState<List<CacheCategory>>> = flow {
        emit(DataState.Loading)
        try {
            val categories = localDataSource.getCategories()
            emit(DataState.Success(categories))
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }

    override suspend fun getCategoryByName(name: String): Flow<DataState<CacheCategory?>> = flow {
        emit(DataState.Loading)
        try {
            val category = localDataSource.getCategoryByName(name)
            emit(DataState.Success(category))
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }

    override suspend fun deleteCategory(cacheCategory: CacheCategory): Int =
        localDataSource.deleteCategory(cacheCategory)

    override suspend fun clearCategories() {
        localDataSource.clearCategories()
    }

    override suspend fun updateCategory(cacheCategory: CacheCategory) {
        localDataSource.updateCategory(cacheCategory)
    }


    //1.3 Item
    override suspend fun insertItem(cacheItem: CacheItem): Long = localDataSource.insertItem(cacheItem)

    override suspend fun getItems(): Flow<DataState<List<CacheItem>>> = flow {
        emit(DataState.Loading)
        try {
            val items = localDataSource.getItems()
            emit(DataState.Success(items))
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }

    override suspend fun getItemByName(name: String): Flow<DataState<CacheItem?>> = flow {
        emit(DataState.Loading)
        try {
            val item = localDataSource.getItemByName(name)
            emit(DataState.Success(item))
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }

    override suspend fun deleteItem(cacheItem: CacheItem): Int = localDataSource.deleteItem(cacheItem)

    override suspend fun clearItems() = localDataSource.clearItems()

    override suspend fun updateItem(cacheItem: CacheItem) = localDataSource.updateItem(cacheItem)

    //Local 1
    override suspend fun emptyAllDatabase() {
        super.emptyAllDatabase()
    }


    //2 Remote
    //2.1 fireStore
    override suspend fun downloadRemoteData(
        collectionName: String,
        shopName: String
    ): Flow<DataState<NetworkEntity?>> = flow {
        emit(DataState.Loading)
        try {
            val downloadedEntity: NetworkEntity? =
                remoteDataSource.downloadRemoteData(collectionName, shopName)
            emit(DataState.Success(downloadedEntity))
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }

    override suspend fun uploadCacheData(
        collectionName: String,
        shopName: String,
        entity: NetworkEntity
    ) :Boolean{
       return remoteDataSource.uploadRemoteData(collectionName, shopName, entity)
    }

    override suspend fun refreshCacheDatabase(scope:CoroutineScope) {
        //getting the shop name from RoomDatabase
        var shopName:String=""
        if (countShopTable() !=0){
            shopName=localDataSource.getShop()[0].name
        }else{
            Log.d("ShopRepository", "refreshCacheDatabase: Empty DB")
            return
        }

        // download from firebase
        val downloadedNetworkEntity: NetworkEntity? =
            remoteDataSource.downloadRemoteData("shops",shopName)
        //Convert downloaded data from NetworkEntity type To DomainModel type
        val domainModel: DomainModel =entityMapper.mapNetworkToDomainModel(downloadedNetworkEntity!!)
        //getting cache Entities from DomainModel
        val shop: CacheShop =entityMapper.getShopFromDomainModel(domainModel)
        val categoriesList: List<CacheCategory> =entityMapper.getCacheCategoriesFromDomainModel(domainModel)
        val items=entityMapper.getCacheItems(domainModel)
        //save cache entities to Room
        localDataSource.insertShop(shop)
        categoriesList.map {cacheCategory->
            localDataSource.insertCategory(cacheCategory)
        }
        items.map { cacheItem ->
            localDataSource.insertItem(cacheItem)
        }

    }

    override suspend fun refreshRemoteDatabase(scope:CoroutineScope) {
        //getting cache entities from Room
        val cacheShop: CacheShop =localDataSource.getShop()[0]
        val cacheCategories: List<CacheCategory> =localDataSource.getCategories()
        val cacheItems: List<CacheItem> =localDataSource.getItems()
        //build DomainModel
        val domainModel=entityMapper.buildDomainFromCache(cacheShop,cacheCategories,cacheItems)
        // convert DomainModel to NetworkEntity
        val networkEntity=entityMapper.mapDomainToNetworkEntity(domainModel)
        //get the shop name
        var shopName:String=""
        getShop().onEach {dataState ->
            shopName = when(dataState){
                is DataState.Success-> dataState.data[0].name
                else-> "...?"
            }
        }.launchIn(scope)
        //upload NetworkEntity
        uploadCacheData("shops",shopName,networkEntity)
    }

    override suspend fun getCacheDomainShop(): Flow<DataState<DomainModel>> = flow{
        emit(DataState.Loading)
        try {
            //getting cache entities from Room
            val cacheShop: CacheShop =localDataSource.getShop()[0]
            val cacheCategories: List<CacheCategory> =localDataSource.getCategories()
            val cacheItems: List<CacheItem> =localDataSource.getItems()
            //build DomainModel
            val domainModel=entityMapper.buildDomainFromCache(cacheShop,cacheCategories,cacheItems)
            emit(DataState.Success(domainModel))
        }catch (e:Exception){emit(DataState.Error(e))}
    }



    //2.2 Storage
    override suspend fun uploadImageToFirebaseStorage(
        shopName: String,
        imgName: String,
        imgUri: Uri
    ): Boolean {
        return remoteDataSource.uploadImageToFirebaseStorage(shopName, imgName, imgUri)
    }

    override suspend fun getImgDownloadUri(shopName: String, imgName: String): Uri? {
       return remoteDataSource.getImgDownloadUri(shopName, imgName)
    }



    //2.3 Auth
    override fun getUserPhoneNumber(): String {
        return remoteDataSource.getUserPhoneNumber()
    }
    override fun requestPhoneNumberVerificationCode(phoneNumber: String, activity: Activity) {
        remoteDataSource.requestPhoneNumberVerificationCode(phoneNumber, activity)
    }
    override fun resendCodeToVerifyPhoneNumber(phoneNumber: String, activity: Activity) {
        remoteDataSource.resendCodeToVerifyPhoneNumber(phoneNumber, activity)
    }
    override suspend fun signIn(code: String): String? {
        return remoteDataSource.signIn(code)
    }
    override fun logOut() {
        remoteDataSource.logOut()
    }

}