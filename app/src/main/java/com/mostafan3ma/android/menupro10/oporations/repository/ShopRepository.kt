package com.mostafan3ma.android.menupro10.oporations.repository

import android.util.Log
import com.google.android.gms.tasks.Task
import com.mostafan3ma.android.menupro10.oporations.data_Entities.DomainModel
import com.mostafan3ma.android.menupro10.oporations.data_Entities.NetworkEntity
import com.mostafan3ma.android.menupro10.oporations.data_Entities.cache_Entities.CacheCategory
import com.mostafan3ma.android.menupro10.oporations.data_Entities.cache_Entities.CacheItem
import com.mostafan3ma.android.menupro10.oporations.data_Entities.cache_Entities.CacheShop
import com.mostafan3ma.android.menupro10.oporations.localDataSource.LocalDataSource
import com.mostafan3ma.android.menupro10.oporations.remoteDataSource.RemoteDataSource
import com.mostafan3ma.android.menupro10.oporations.utils.DataState
import com.mostafan3ma.android.menupro10.oporations.utils.EntitiesMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import kotlin.Exception

class ShopRepository
@Inject
constructor(private val localDB: LocalDataSource,
            private val remoteDB: RemoteDataSource,
            private val entityMapper: EntitiesMapper) :
    DefaultShopRepository {

    //Local 1
    //1.1 Shop
    override suspend fun insertShop(cacheShop: CacheShop): Long {
        return localDB.insertShop(cacheShop)
    }

    override suspend fun getShop(): Flow<DataState<List<CacheShop>>> = flow {
        emit(DataState.Loading)
            try {
                if (localDB.countShopTable() !=0){
                    val shopList: List<CacheShop> = localDB.getShop()
                    emit(DataState.Success(shopList))
                }else{
                    return@flow
                }

            }catch (e:Exception){
                emit(DataState.Error(e))
            }


    }

    override suspend fun countShopTable(): Int {
        return localDB.countShopTable()
    }

    override suspend fun deleteShop(cacheShop: CacheShop): Int = localDB.deleteShop(cacheShop)

    override suspend fun clearShop() {
        localDB.clearShop()
    }

    override suspend fun updateShop(cacheShop: CacheShop) {
        localDB.updateShop(cacheShop)
    }


    //1.2 Category
    override suspend fun insertCategory(cacheCategory: CacheCategory): Long =
        localDB.insertCategory(cacheCategory)

    override suspend fun getCategories(): Flow<DataState<List<CacheCategory>>> = flow {
        emit(DataState.Loading)
        try {
            val categories = localDB.getCategories()
            emit(DataState.Success(categories))
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }

    override suspend fun getCategoryByName(name: String): Flow<DataState<CacheCategory?>> = flow {
        emit(DataState.Loading)
        try {
            val category = localDB.getCategoryByName(name)
            emit(DataState.Success(category))
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }

    override suspend fun deleteCategory(cacheCategory: CacheCategory): Int =
        localDB.deleteCategory(cacheCategory)

    override suspend fun clearCategories() {
        localDB.clearCategories()
    }

    override suspend fun updateCategory(cacheCategory: CacheCategory) {
        localDB.updateCategory(cacheCategory)
    }


    //1.3 Item
    override suspend fun insertItem(cacheItem: CacheItem): Long = localDB.insertItem(cacheItem)

    override suspend fun getItems(): Flow<DataState<List<CacheItem>>> = flow {
        emit(DataState.Loading)
        try {
            val items = localDB.getItems()
            emit(DataState.Success(items))
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }

    override suspend fun getItemByName(name: String): Flow<DataState<CacheItem?>> = flow {
        emit(DataState.Loading)
        try {
            val item = localDB.getItemByName(name)
            emit(DataState.Success(item))
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }

    override suspend fun deleteItem(cacheItem: CacheItem): Int = localDB.deleteItem(cacheItem)

    override suspend fun clearItems() = localDB.clearItems()

    override suspend fun updateItem(cacheItem: CacheItem) = localDB.updateItem(cacheItem)

    //Local 1
    override suspend fun emptyAllDatabase() {
        super.emptyAllDatabase()
    }


    //2 Remote
    override suspend fun downloadRemoteData(
        collectionName: String,
        shopName: String
    ): Flow<DataState<NetworkEntity?>> = flow {
        emit(DataState.Loading)
        try {
            val downloadedEntity: NetworkEntity? =
                remoteDB.downloadRemoteData(collectionName, shopName)
            emit(DataState.Success(downloadedEntity))
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }

    override suspend fun uploadCacheData(
        collectionName: String,
        shopName: String,
        entity: NetworkEntity
    ) {
        remoteDB.uploadRemoteData(collectionName, shopName, entity)
    }

    override suspend fun refreshCacheDatabase(scope:CoroutineScope) {
        //getting the shop name from RoomDatabase
        var shopName:String=""
        if (countShopTable() !=0){
            shopName=localDB.getShop()[0].name
        }else{
            Log.d("ShopRepository", "refreshCacheDatabase: Empty DB")
            return
        }

        // download from firebase
        val downloadedNetworkEntity: NetworkEntity? =
            remoteDB.downloadRemoteData("shops",shopName)
        //Convert downloaded data from NetworkEntity type To DomainModel type
        val domainModel: DomainModel =entityMapper.mapNetworkToDomainModel(downloadedNetworkEntity!!)
        //getting cache Entities from DomainModel
        val shop: CacheShop =entityMapper.getShopFromDomainModel(domainModel)
        val categoriesList: List<CacheCategory> =entityMapper.getCacheCategoriesFromDomainModel(domainModel)
        val items=entityMapper.getCacheItems(domainModel)
        //save cache entities to Room
        localDB.insertShop(shop)
        categoriesList.map {cacheCategory->
            localDB.insertCategory(cacheCategory)
        }
        items.map { cacheItem ->
            localDB.insertItem(cacheItem)
        }

    }

    override suspend fun refreshRemoteDatabase(scope:CoroutineScope) {
        //getting cache entities from Room
        val cacheShop: CacheShop =localDB.getShop()[0]
        val cacheCategories: List<CacheCategory> =localDB.getCategories()
        val cacheItems: List<CacheItem> =localDB.getItems()
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
            val cacheShop: CacheShop =localDB.getShop()[0]
            val cacheCategories: List<CacheCategory> =localDB.getCategories()
            val cacheItems: List<CacheItem> =localDB.getItems()
            //build DomainModel
            val domainModel=entityMapper.buildDomainFromCache(cacheShop,cacheCategories,cacheItems)
            emit(DataState.Success(domainModel))
        }catch (e:Exception){emit(DataState.Error(e))}
    }



}