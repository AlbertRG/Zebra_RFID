package com.example.volkswagendemo.data.repository

import com.example.volkswagendemo.domain.provider.DataStoreManager
import com.example.volkswagendemo.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : SettingsRepository {

    override suspend fun setAntennaPower(power: Float) {
        dataStoreManager.setAntennaPower(power)
    }

    override fun getAntennaPower(): Flow<Float> {
        return dataStoreManager.getAntennaPower()
    }

    override suspend fun setBeeperVolume(volume: Int) {
        dataStoreManager.setBeeperVolume(volume)
    }

    override fun getBeeperVolume(): Flow<Int> {
        return dataStoreManager.getBeeperVolume()
    }

}