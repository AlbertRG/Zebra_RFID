package com.example.volkswagendemo.domain.provider

import com.example.volkswagendemo.data.models.LocationData
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

}