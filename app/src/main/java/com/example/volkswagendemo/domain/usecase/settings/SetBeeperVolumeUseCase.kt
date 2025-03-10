package com.example.volkswagendemo.domain.usecase.settings

import com.example.volkswagendemo.domain.repository.SettingsRepository
import javax.inject.Inject

class SetBeeperVolumeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(volume: Int) {
        settingsRepository.setBeeperVolume(volume)
    }

}