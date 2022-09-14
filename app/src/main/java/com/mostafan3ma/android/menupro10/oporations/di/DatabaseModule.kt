package com.mostafan3ma.android.menupro10.oporations.di

import android.content.Context
import androidx.room.Room
import com.mostafan3ma.android.menupro10.oporations.localDataSource.RoomDatabase.ShopDao
import com.mostafan3ma.android.menupro10.oporations.localDataSource.RoomDatabase.ShopDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {



    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): ShopDatabase {
        return Room.databaseBuilder(
            context,
            ShopDatabase::class.java,
            ShopDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }



    @Singleton
    @Provides
    fun provideShopDao(database: ShopDatabase): ShopDao {
        return database.getDao()
    }



}