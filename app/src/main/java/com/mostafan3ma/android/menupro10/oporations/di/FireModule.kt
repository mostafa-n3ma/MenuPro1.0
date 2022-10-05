package com.mostafan3ma.android.menupro10.oporations.di

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FireModule {

    @Singleton
    @Provides
    fun provideFireStoreInstance():FirebaseFirestore{
        return FirebaseFirestore.getInstance()
    }

    @Singleton
    @Provides
    fun provideFireStorageReference(): StorageReference {
        return FirebaseStorage.getInstance().reference
    }
}