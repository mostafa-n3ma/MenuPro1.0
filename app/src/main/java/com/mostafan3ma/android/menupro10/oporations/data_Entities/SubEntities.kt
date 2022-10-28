package com.mostafan3ma.android.menupro10.oporations.data_Entities

import com.mostafan3ma.android.menupro10.oporations.data_Entities.cache_Entities.CacheCategory
import com.mostafan3ma.android.menupro10.oporations.data_Entities.cache_Entities.CacheItem


data class Category(
    var name: String="",
    var imageName:String="",
    var imageUri:String="",
    var description:String=""
)

fun Category.mapToCacheCategory(category: Category):CacheCategory{
    return CacheCategory(
        name=category.name,
        imageName=category.imageName,
        imageUri=category.imageUri,
        description=category.description
    )
}

fun CacheCategory.getCategory(cacheCategory: CacheCategory):Category{

    return Category(
        name=cacheCategory.name,
        imageName=cacheCategory.imageName,
        imageUri=cacheCategory.imageUri,
        description=cacheCategory.description
    )
}




data class Item(
    var name: String="",
    var description:String="",
    var category: Category?=null,
    var imageName:String="",
    var imageUri: String="",
    var price:String="",
    var size:String=""
)

fun Item.getCacheItem(item: Item):CacheItem{
    return CacheItem(
        name=item.name,
        description=item.description,
        category=item.category!!,
        imageName=item.imageName,
        imageUri=item.imageUri,
        price=item.price,
        size = item.size
    )
}

fun CacheItem.getItem(cacheItem: CacheItem):Item{
    return Item(
        name=cacheItem.name,
        description=cacheItem.description,
        category=cacheItem.category,
        imageName=cacheItem.imageName,
        imageUri=cacheItem.imageUri,
        price=cacheItem.price,
        size = cacheItem.size
    )
}