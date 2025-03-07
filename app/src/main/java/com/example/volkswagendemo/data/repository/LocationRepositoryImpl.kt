package com.example.volkswagendemo.data.repository

import com.example.volkswagendemo.data.models.LocationData
import com.example.volkswagendemo.domain.provider.DataStoreManager
import com.example.volkswagendemo.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val dataStoreManager: DataStoreManager
): LocationRepository {

    override suspend fun setLocationSaved(isSaved: Boolean) {
        dataStoreManager.setLocationSaved(isSaved)
    }

    override fun getLocationSaved(): Flow<Boolean> {
        return dataStoreManager.getLocationSaved()
    }

    override suspend fun setLocation(location: LocationData) {
        dataStoreManager.setLocation(location)
    }

    override fun getLocation(): Flow<LocationData?> {
        return dataStoreManager.getLocation()
    }

    override suspend fun setAddress(address: String) {
        dataStoreManager.setAddress(address)
    }

    override fun getAddress(): Flow<String> {
        return dataStoreManager.getAddress()
    }

}