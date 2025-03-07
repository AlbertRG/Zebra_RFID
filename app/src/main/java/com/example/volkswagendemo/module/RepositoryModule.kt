package com.example.volkswagendemo.module

import com.example.volkswagendemo.data.repository.LocationRepositoryImpl
import com.example.volkswagendemo.data.repository.WorkshopRepositoryImpl
import com.example.volkswagendemo.domain.repository.LocationRepository
import com.example.volkswagendemo.domain.repository.WorkshopRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindLocationRepository(locationRepositoryImpl: LocationRepositoryImpl): LocationRepository

    @Binds
    @Singleton
    abstract fun bindWorkshopRepository(workshopRepositoryImpl: WorkshopRepositoryImpl): WorkshopRepository
}