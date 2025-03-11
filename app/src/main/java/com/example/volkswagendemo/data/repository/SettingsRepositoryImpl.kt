package com.example.volkswagendemo.data.repository

import com.example.volkswagendemo.data.models.SettingsData
import com.example.volkswagendemo.domain.provider.DataStoreManager
import com.example.volkswagendemo.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : SettingsRepository {

    override suspend fun setSettings(settings: SettingsData) {
        dataStoreManager.setSettings(settings)
    }

    override fun getSettings(): Flow<SettingsData?> {
        return dataStoreManager.getSettings()
    }

}