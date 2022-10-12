package com.mostafan3ma.android.menupro10.oporations.di

import com.mostafan3ma.android.menupro10.oporations.DataManagment.localDataSource.LocalDataSource
import com.mostafan3ma.android.menupro10.oporations.DataManagment.remoteDataSource.RemoteDataSource
import com.mostafan3ma.android.menupro10.oporations.DataManagment.repository.DefaultShopRepository
import com.mostafan3ma.android.menupro10.oporations.DataManagment.repository.ShopRepository
import com.mostafan3ma.android.menupro10.oporations.utils.EntitiesMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    @RealRepository
    fun provideRepository(localDB: LocalDataSource,
                          remoteDB: RemoteDataSource,
                          entityMapper: EntitiesMapper):DefaultShopRepository{
        return ShopRepository(localDB, remoteDB, entityMapper)
    }

}
