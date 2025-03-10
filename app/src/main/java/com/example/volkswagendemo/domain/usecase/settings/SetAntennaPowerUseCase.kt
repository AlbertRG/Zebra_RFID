package com.example.volkswagendemo.domain.usecase.settings

import com.example.volkswagendemo.domain.repository.SettingsRepository
import javax.inject.Inject

class SetAntennaPowerUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(power: Float) {
        settingsRepository.setAntennaPower(power)
    }

}