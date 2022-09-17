package com.mostafan3ma.android.menupro10.oporations.data_Entities

import androidx.room.ColumnInfo

data class DomainModel(
    var userId:String,
    var name:String="",
    var type:String="",
    var phoneNumber:String="",
    var logoImageName:String="",
    var logoImageUri:String="",
    var categories_list:List<Category>,
    var items:List<Item>
)

