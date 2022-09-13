package com.mostafan3ma.android.menupro10.data_Entities

data class DomainModel(
    var userId:String,
    var name:String="",
    var type:String="",
    var phoneNumber:String="",
    var logo:String="",
    var categories_list:List<Category>,
    var items:List<Item>
)

