package com.mostafan3ma.android.menupro10.oporations.data_Entities

data class NetworkEntity(
    var userId: String="",
    var name: String = "",
    var type: String = "",
    var phoneNumber: String = "",
    var logo: String = "",
    var categories_list: List<Category>?=null,
    var items: List<Item>?=null
)


