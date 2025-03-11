package com.example.volkswagendemo.domain.repository

import com.example.volkswagendemo.data.models.SettingsData
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    suspend fun setSettings(settings: SettingsData)
    fun getSettings(): Flow<SettingsData?>

}