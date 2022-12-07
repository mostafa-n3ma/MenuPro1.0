package com.mostafan3ma.android.menupro10.oporations.utils

import com.mostafan3ma.android.menupro10.oporations.data_Entities.*
import com.mostafan3ma.android.menupro10.oporations.data_Entities.cache_Entities.CacheCategory
import com.mostafan3ma.android.menupro10.oporations.data_Entities.cache_Entities.CacheItem
import com.mostafan3ma.android.menupro10.oporations.data_Entities.cache_Entities.CacheShop
import javax.inject.Inject

class EntitiesMapper
@Inject
constructor() {
    fun mapDomainToNetworkEntity(domainModel: DomainModel): NetworkEntity {
        return NetworkEntity(
            userId = domainModel.userId,
            name = domainModel.name,
            type = domainModel.type,
            phoneNumber = domainModel.phoneNumber,
            logoImageName = domainModel.logoImageName,
            logoImageUri = domainModel.logoImageUri,
            categories_list = domainModel.categories_list,
            items = domainModel.items
        )
    }

    fun mapNetworkToDomainModel(networkEntity: NetworkEntity): DomainModel {
        return DomainModel(
            userId = networkEntity.userId,
            name = networkEntity.name,
            type = networkEntity.type,
            phoneNumber = networkEntity.phoneNumber,
            logoImageName = networkEntity.logoImageName,
            logoImageUri = networkEntity.logoImageUri,
            categories_list = networkEntity.categories_list!!,
            items = networkEntity.items!!
        )
    }


    fun getShopFromDomainModel(domainModel: DomainModel): CacheShop {
        return CacheShop(
            id = domainModel.userId,
            name = domainModel.name,
            type = domainModel.type,
            phoneNumber = domainModel.phoneNumber,
            logoImageName = domainModel.logoImageName,
            logoImageUri = domainModel.logoImageUri,
        )
    }

    fun getCacheCategoriesFromDomainModel(domainModel: DomainModel): List<CacheCategory> {
        return domainModel.categories_list!!.map {
            it.getCacheCategory(it)
        }
    }

    fun getCacheItems(domainModel: DomainModel): List<CacheItem> {
        return domainModel.items!!.map {
            it.getCacheItem(it)
        }
    }


    fun buildDomainFromCache(
        cacheShop: CacheShop, cacheCategoryList: List<CacheCategory>?=null, cacheItemList: List<CacheItem>?=null
    ): DomainModel {
        val categoryList:List<Category>? = cacheCategoryList?.map {
            it.getCategory(it) }
        val itemList:List<Item>?=cacheItemList?.map { it.getItem(it) }
        return DomainModel(
            userId = cacheShop.id,
            name = cacheShop.name,
            type = cacheShop.type,
            phoneNumber = cacheShop.phoneNumber,
            logoImageName = cacheShop.logoImageName,
            logoImageUri = cacheShop.logoImageUri,
            categories_list = categoryList,
            items = itemList
        )

    }


}