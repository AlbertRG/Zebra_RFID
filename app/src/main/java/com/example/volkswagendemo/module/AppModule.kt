package com.example.volkswagendemo.module

import android.app.Application
import com.example.volkswagendemo.utils.HexToAscii
import com.example.volkswagendemo.utils.LocationUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideLocationUtils(application: Application): LocationUtils {
        return LocationUtils(application)
    }

    @Provides
    @Singleton
    fun provideHexToAscii(): HexToAscii {
        return HexToAscii()
    }

}