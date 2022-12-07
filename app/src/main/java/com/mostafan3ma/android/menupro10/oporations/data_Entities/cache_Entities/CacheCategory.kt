package com.mostafan3ma.android.menupro10.oporations.data_Entities.cache_Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mostafan3ma.android.menupro10.oporations.data_Entities.Category
import com.mostafan3ma.android.menupro10.oporations.data_Entities.getCategory

@Entity(tableName = "categories")
data class CacheCategory(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "name")
    var name: String="",
    @ColumnInfo(name="imageName")
    var imageName:String="",
    @ColumnInfo(name="ImageUri")
    var imageUri:String="",
    @ColumnInfo(name="description")
    var description:String=""
){

}

fun List<CacheCategory>.mapToCategoriesList():List<Category>{
    val categoriesList= mutableListOf<Category>()
    let { cacheCategoriesList->
        cacheCategoriesList.map { cacheCategory ->
            categoriesList.add(cacheCategory.getCategory(cacheCategory))
        }
    }
    return categoriesList
}