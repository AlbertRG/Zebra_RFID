package com.example.volkswagendemo.data.provider

import com.example.volkswagendemo.data.models.LocationData
import com.example.volkswagendemo.domain.provider.DataStoreManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DataStoreManagerImpl @Inject constructor(
    private val dataStore: DataStore,
) : DataStoreManager {

    override suspend fun setLocationSaved(isSaved: Boolean) {
        dataStore.setLocationSaved(isSaved)
    }

    override fun getLocationSaved(): Flow<Boolean> =
        dataStore.getLocationSaved()

    override suspend fun setLocation(location: LocationData) {
        dataStore.setLocation(location)
    }

    override fun getLocation(): Flow<LocationData?> =
        dataStore.getLocation()

    override suspend fun setAddress(address: String) {
        dataStore.setAddress(address)
    }

    override fun getAddress(): Flow<String> =
        dataStore.getAddress()

    override suspend fun setWorkshopName(name: String) {
        dataStore.setWorkshopName(name)
    }

    override fun getWorkshopName(): Flow<String> =
        dataStore.getWorkshopName()

    override suspend fun setAntennaPower(power: Float) {
        dataStore.setAntennaPower(power)
    }

    override fun getAntennaPower(): Flow<Float> =
        dataStore.getAntennaPower()

    override suspend fun setBeeperVolume(volume: Int) {
        dataStore.setBeeperVolume(volume)
    }

    override fun getBeeperVolume(): Flow<Int> =
        dataStore.getBeeperVolume()

}