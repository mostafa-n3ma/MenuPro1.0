package com.mostafan3ma.android.menupro10.oporations.data_Entities.cache_Entities

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.mostafan3ma.android.menupro10.oporations.data_Entities.Category

class CategoryConverter {
    @TypeConverter
    fun fromCategory(category: Category?):String?{
        if (category==null){
            return null
        }
        val gson=Gson()
        return gson.toJson(category)
    }

    @TypeConverter
    fun toCategory(string: String?):Category?{
        if (string==null){
            return null
        }
        val gson=Gson()
        return gson.fromJson(string,Category::class.java)
    }




}