package com.mostafan3ma.android.menupro10.data_Entities.cache_Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CacheCategory(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "name")
    var name: String="",
    @ColumnInfo(name="image")
    var image:String="",
    @ColumnInfo(name="description")
    var description:String=""
)