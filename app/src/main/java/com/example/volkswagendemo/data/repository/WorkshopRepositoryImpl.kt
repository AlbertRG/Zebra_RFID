package com.example.volkswagendemo.data.repository

import com.example.volkswagendemo.domain.provider.DataStoreManager
import com.example.volkswagendemo.domain.repository.WorkshopRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WorkshopRepositoryImpl @Inject constructor(
private val dataStoreManager: DataStoreManager
): WorkshopRepository {

    override suspend fun setWorkshopName(name: String) {
        dataStoreManager.setWorkshopName(name)
    }

    override fun getWorkshopName(): Flow<String> {
        return dataStoreManager.getWorkshopName()
    }

}