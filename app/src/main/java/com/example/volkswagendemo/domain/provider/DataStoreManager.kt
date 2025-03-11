package com.example.volkswagendemo.domain.provider

import com.example.volkswagendemo.data.models.LocationData
import com.example.volkswagendemo.data.models.SettingsData
import kotlinx.coroutines.flow.Flow

interface DataStoreManager {

    suspend fun setLocationSaved(isSaved: Boolean)
    fun getLocationSaved(): Flow<Boolean>

    suspend fun setLocation(location: LocationData)
    fun getLocation(): Flow<LocationData?>

    suspend fun setAddress(address: String)
    fun getAddress(): Flow<String>

    suspend fun setWorkshopName(name: String)
    fun getWorkshopName(): Flow<String>

    suspend fun setSettings(settings: SettingsData)
    fun getSettings(): Flow<SettingsData?>

}