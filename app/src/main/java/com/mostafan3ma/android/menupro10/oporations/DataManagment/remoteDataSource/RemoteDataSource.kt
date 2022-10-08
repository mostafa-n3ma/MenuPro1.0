package com.mostafan3ma.android.menupro10.oporations.DataManagment.remoteDataSource

import android.app.Activity
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.common.io.Files.append
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.mostafan3ma.android.menupro10.oporations.data_Entities.NetworkEntity
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.log

class RemoteDataSource
@Inject
constructor(
    private var firestore: FirebaseFirestore,
    private var storageReference: StorageReference,
    private var firebaseAuth: FirebaseAuth
) : DefaultRemoteDataSource {
    companion object{
        const val RemoteDataSourceTAG="RemoteDataSource"
    }



    //Firestore
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


    //storage
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



    //Auth

     fun getUserStatus():String {
         Log.d(RemoteDataSourceTAG, "getUserStatus ")
         return if (firebaseAuth.currentUser != null) {
             val phoneNumber: String? = firebaseAuth.currentUser!!.phoneNumber
             Log.d(RemoteDataSourceTAG, "getUserStatus :true/phone number: $phoneNumber")
             Log.d(RemoteDataSourceTAG, "getUserStatus /id: ${firebaseAuth.currentUser!!.uid}")
             "signed in with phone number : ${firebaseAuth.currentUser!!.phoneNumber}"
         } else {
             Log.d(RemoteDataSourceTAG, "getUserStatus: not signed in")
             "not signed in ><"
         }
    }

    private  var mForceResendingToken:PhoneAuthProvider.ForceResendingToken?=null
    private  var mVerificationId:String?=null
    private var mCallBack =object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            firebaseAuth.signInWithCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.d(RemoteDataSourceTAG, "onVerificationFailed: ${e.message}")
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(verificationId, token)
            Log.d(RemoteDataSourceTAG, "onCodeSent: ${mVerificationId.toString()} : ${mForceResendingToken.toString()}")
            mVerificationId=verificationId
            mForceResendingToken=token
        }
    }

     fun requestPhoneNumberVerificationCode(phoneNumber: String,activity: Activity) {
         Log.d(RemoteDataSourceTAG, "requestPhoneNumberVerificationCode:")
        val options= PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setActivity(activity)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setCallbacks(mCallBack!!)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

     fun resendCodeToVerifyPhoneNumber(phoneNumber: String,activity: Activity) {
         Log.d(RemoteDataSourceTAG, "resendCodeToVerifyPhoneNumber: ")
        val options=PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setActivity(activity)
            .setTimeout(60L,TimeUnit.SECONDS)
            .setCallbacks(mCallBack!!)
            .setForceResendingToken(mForceResendingToken!!)
            .build()
        if (mForceResendingToken !=null) {
            Log.d(RemoteDataSourceTAG, "resendCodeToVerifyPhoneNumber: not null (resending ")
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }

    suspend fun signIn(code:String):String? {
        Log.d(RemoteDataSourceTAG, "signIn: ")
        val credential=PhoneAuthProvider.getCredential(mVerificationId!!,code)
        var result:String?=null
        firebaseAuth.signInWithCredential(credential)
            .addOnFailureListener {
                Log.d(RemoteDataSourceTAG, "signIn: addOnFailureListener :${it.message.toString()}")
                result="failed to sign in : ${it.message}"
            }
            .addOnSuccessListener {
                if (firebaseAuth.currentUser !=null){
                    Log.d(RemoteDataSourceTAG, "signIn:addOnSuccessListener ")
                    result=firebaseAuth.currentUser!!.phoneNumber
                }
            }.await()

        return result
    }

    fun logOut(){
        Log.d(RemoteDataSourceTAG, "logOut: ")
        firebaseAuth.signOut()
    }

}