package com.example.volkswagendemo.domain.usecase.location

import com.example.volkswagendemo.data.models.LocationData
import com.example.volkswagendemo.domain.repository.LocationRepository
import javax.inject.Inject

class SetLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {

    suspend operator fun invoke(location: LocationData) {
        locationRepository.setLocation(location)
    }

}