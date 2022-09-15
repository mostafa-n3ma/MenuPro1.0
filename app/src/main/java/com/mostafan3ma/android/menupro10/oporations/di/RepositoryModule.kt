package com.mostafan3ma.android.menupro10.oporations.di

import com.mostafan3ma.android.menupro10.oporations.localDataSource.LocalDataSource
import com.mostafan3ma.android.menupro10.oporations.remoteDataSource.RemoteDataSource
import com.mostafan3ma.android.menupro10.oporations.repository.ShopRepository
import com.mostafan3ma.android.menupro10.oporations.utils.EntitiesMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideRepository(localDB: LocalDataSource,
                          remoteDB: RemoteDataSource,
                          entityMapper: EntitiesMapper):ShopRepository{
        return ShopRepository(localDB, remoteDB, entityMapper)
    }




}