package com.mostafan3ma.android.menupro10.oporations.data_Entities.cache_Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shop")
data class CacheShop(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "shopId")
    var id:String,
    @ColumnInfo(name = "name")
    var name:String="",
    @ColumnInfo(name = "type")
    var type:String="",
    @ColumnInfo(name = "phoneNumber")
    var phoneNumber:String="",
    @ColumnInfo(name = "logo")
    var logo:String=""
)