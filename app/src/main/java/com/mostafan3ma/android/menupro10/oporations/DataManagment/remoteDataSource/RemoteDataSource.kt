package com.mostafan3ma.android.menupro10.oporations.DataManagment.remoteDataSource

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.mostafan3ma.android.menupro10.oporations.data_Entities.NetworkEntity
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RemoteDataSource
@Inject
constructor(
    private var firestore: FirebaseFirestore,
    private var storageReference: StorageReference
) : DefaultRemoteDataSource {
    companion object{
        const val RemoteDataSourceTAG="RemoteDataSource"
    }



    override suspend fun downloadRemoteData(
        collectionName: String,
        shopName: String
    ): NetworkEntity? {
        var downloadedEntity: NetworkEntity? = null

        val task = firestore.collection(collectionName).document(shopName).get()
            .addOnSuccessListener {
                downloadedEntity = it.toObject(NetworkEntity::class.java)
                Log.d("RemoteDataSource", "downloadRemoteData: Success")
            }
            .addOnFailureListener {

            }.await()


        return downloadedEntity
    }

    override suspend fun uploadRemoteData(
        collectionName: String,
        shopName: String,
        entitiy: NetworkEntity
    ) {
        firestore.collection(collectionName).document(shopName).set(entitiy)
            .addOnSuccessListener {
                Log.d("RemoteDataSource", "uploadRemoteData: Success")
            }

    }

    override suspend fun uploadImageToFirebaseStorage(
        shopName: String,
        imgName: String,
        imgUri: Uri
    ): Boolean {
        Log.d(RemoteDataSourceTAG, "uploadImageToFirebase: Uploading>>>")
        var success = false
        storageReference.child("$shopName/images/$imgName")
            .putFile(imgUri)
            .addOnSuccessListener {
                Log.d(RemoteDataSourceTAG, "uploadImageToFirebase: uploading Success ")
                success = true
            }
            .addOnFailureListener {
                Log.d(RemoteDataSourceTAG, "uploadImageToFirebase: uploading Fail ")
            }.await()
        Log.d(RemoteDataSourceTAG, "uploadImageToFirebase: upload status :${success.toString()} ")

        return success
    }

    override suspend fun getImgDownloadUri(shopName: String, imgName: String): Uri? {
        var downloadUri: Uri? = null
        val something: Uri = storageReference.child("$shopName/images/$imgName").downloadUrl
            .addOnSuccessListener {
                Log.d(RemoteDataSourceTAG, "downloadUrl:got it ")
                downloadUri = it
                Log.d(RemoteDataSourceTAG, "downloadUrl : $it")
            }
            .addOnFailureListener {
                Log.d(RemoteDataSourceTAG, "downloadUrl: fail>>${it.message} ")
            }.await()

        return downloadUri
    }


}