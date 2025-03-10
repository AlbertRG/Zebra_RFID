package com.example.volkswagendemo.domain.usecase.settings

import com.example.volkswagendemo.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBeeperVolumeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    operator fun invoke(): Flow<Int> {
        return settingsRepository.getBeeperVolume()
    }

}