package com.example.volkswagendemo.domain.usecase.location

import com.example.volkswagendemo.domain.repository.LocationRepository
import javax.inject.Inject

class SetLocationSavedUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {

    suspend operator fun invoke(isSaved: Boolean) {
        locationRepository.setLocationSaved(isSaved)
    }

}