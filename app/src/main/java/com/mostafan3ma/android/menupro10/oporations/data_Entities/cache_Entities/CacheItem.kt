package com.mostafan3ma.android.menupro10.oporations.data_Entities.cache_Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mostafan3ma.android.menupro10.oporations.data_Entities.Category

@Entity(tableName = "items")
data class CacheItem(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "name")
    var name: String="",
    @ColumnInfo(name="description")
    var description:String="",
    @ColumnInfo(name="category")
    var category: Category,
    @ColumnInfo(name="image")
    var image:String="",
    @ColumnInfo(name="price")
    var price:String="",
    @ColumnInfo(name="size")
    var size:String=""
)



