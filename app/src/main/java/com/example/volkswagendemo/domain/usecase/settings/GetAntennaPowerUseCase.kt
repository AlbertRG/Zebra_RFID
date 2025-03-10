package com.example.volkswagendemo.domain.usecase.settings

import com.example.volkswagendemo.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAntennaPowerUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    operator fun invoke(): Flow<Float> {
        return settingsRepository.getAntennaPower()
    }

}