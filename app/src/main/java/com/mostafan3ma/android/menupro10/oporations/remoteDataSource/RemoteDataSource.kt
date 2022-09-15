package com.mostafan3ma.android.menupro10.oporations.remoteDataSource

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.mostafan3ma.android.menupro10.oporations.data_Entities.NetworkEntity
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RemoteDataSource
@Inject
constructor(private var db: FirebaseFirestore) : DefaultRemoteDataSource {
    override suspend fun downloadRemoteData(
        collectionName: String,
        shopName: String
    ): NetworkEntity? {
        var downloadedEntity:NetworkEntity?=null

        val task = db.collection(collectionName).document(shopName).get()
            .addOnSuccessListener {
                downloadedEntity=it.toObject(NetworkEntity::class.java)
                Log.d("RemoteDataSource", "downloadRemoteData: Success")
            }
            .addOnFailureListener{

            }.await()


        return downloadedEntity
    }

    override suspend fun uploadRemoteData(collectionName: String, shopName: String,entitiy:NetworkEntity) {
        db.collection(collectionName).document(shopName).set(entitiy)
            .addOnSuccessListener {
                Log.d("RemoteDataSource", "uploadRemoteData: Success")
            }

    }
}