package com.example.volkswagendemo.domain.usecase.settings

import com.example.volkswagendemo.data.models.SettingsData
import com.example.volkswagendemo.domain.repository.SettingsRepository
import javax.inject.Inject

class SetSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(settings: SettingsData) {
        settingsRepository.setSettings(settings)
    }

}