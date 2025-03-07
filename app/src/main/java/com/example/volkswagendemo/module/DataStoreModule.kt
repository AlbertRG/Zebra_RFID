package com.example.volkswagendemo.module

import com.example.volkswagendemo.data.provider.DataStoreManagerImpl
import com.example.volkswagendemo.domain.provider.DataStoreManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataStoreModule {

    @Binds
    @Singleton
    abstract fun provideDataStoreManager(dataStoreManagerImpl: DataStoreManagerImpl): DataStoreManager

}