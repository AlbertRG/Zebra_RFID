package com.example.volkswagendemo.domain.usecase.settings

import com.example.volkswagendemo.data.models.SettingsData
import com.example.volkswagendemo.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(
private val settingsRepository: SettingsRepository
){

    operator fun invoke(): Flow<SettingsData?> {
        return settingsRepository.getSettings()
    }

}