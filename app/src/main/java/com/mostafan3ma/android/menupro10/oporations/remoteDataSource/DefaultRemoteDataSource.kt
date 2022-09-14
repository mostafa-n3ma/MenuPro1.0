package com.mostafan3ma.android.menupro10.oporations.remoteDataSource

import com.mostafan3ma.android.menupro10.oporations.data_Entities.NetworkEntity

interface DefaultRemoteDataSource {
    suspend fun downloadRemoteData(collectionName:String, shopName:String):NetworkEntity?
    suspend fun uploadRemoteData(collectionName:String, shopName:String,entity:NetworkEntity)
}