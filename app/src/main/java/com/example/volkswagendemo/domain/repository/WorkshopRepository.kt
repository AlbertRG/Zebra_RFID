package com.example.volkswagendemo.domain.repository

import kotlinx.coroutines.flow.Flow

interface WorkshopRepository {

    suspend fun setWorkshopName(name: String)
    fun getWorkshopName(): Flow<String>

}