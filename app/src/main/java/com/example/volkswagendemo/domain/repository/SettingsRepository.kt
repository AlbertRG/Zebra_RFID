package com.example.volkswagendemo.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    suspend fun setAntennaPower(power: Float)
    fun getAntennaPower(): Flow<Float>

    suspend fun setBeeperVolume(volume: Int)
    fun getBeeperVolume(): Flow<Int>

}