package com.mostafan3ma.android.menupro10.oporations.DataManagment.remoteDataSource

import android.net.Uri
import com.mostafan3ma.android.menupro10.oporations.data_Entities.NetworkEntity

interface DefaultRemoteDataSource {
    suspend fun downloadRemoteData(collectionName: String, shopName: String): NetworkEntity?
    suspend fun uploadRemoteData(collectionName: String, shopName: String, entity: NetworkEntity)
    suspend fun uploadImageToFirebaseStorage( shopName: String, imgName: String, imgUri: Uri): Boolean
    suspend fun getImgDownloadUri(shopName: String, imgName: String): Uri?
}