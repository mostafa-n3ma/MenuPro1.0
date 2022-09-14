package com.mostafan3ma.android.menupro10.oporations.data_Entities

import com.mostafan3ma.android.menupro10.oporations.data_Entities.cache_Entities.CacheCategory
import com.mostafan3ma.android.menupro10.oporations.data_Entities.cache_Entities.CacheItem


data class Category(
    var name: String="",
    var image:String="",
    var description:String=""
)

fun Category.mapToCacheCategory(category: Category):CacheCategory{
    return CacheCategory(
        name=category.name,
        image=category.image,
        description=category.description
    )
}

fun CacheCategory.getCategory(cacheCategory: CacheCategory):Category{
    return Category(
        name=cacheCategory.name,
        image=cacheCategory.image,
        description=cacheCategory.description
    )
}




data class Item(
    var name: String="",
    var description:String="",
    var category: Category?=null,
    var image:String="",
    var price:String="",
    var size:String=""
)

fun Item.getCacheItem(item: Item):CacheItem{
    return CacheItem(
        name=item.name,
        description=item.description,
        category=item.category!!,
        image=item.image,
        price=item.price,
        size = item.size
    )
}

fun CacheItem.getItem(cacheItem: CacheItem):Item{
    return Item(
        name=cacheItem.name,
        description=cacheItem.description,
        category=cacheItem.category,
        image=cacheItem.image,
        price=cacheItem.price,
        size = cacheItem.size
    )
}