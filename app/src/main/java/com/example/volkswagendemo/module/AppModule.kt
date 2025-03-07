package com.example.volkswagendemo.module

import android.app.Application
import com.example.volkswagendemo.utils.ConversionUtils
import com.example.volkswagendemo.utils.ExcelUtils
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
    fun provideExcelUtils(application: Application): ExcelUtils {
        return ExcelUtils(application, ConversionUtils())
    }

    @Provides
    @Singleton
    fun provideHexToAscii(): ConversionUtils {
        return ConversionUtils()
    }

}