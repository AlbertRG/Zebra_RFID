package com.example.volkswagendemo.domain.usecase.location

import com.example.volkswagendemo.data.models.LocationData
import com.example.volkswagendemo.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {

    operator fun invoke(): Flow<LocationData?> {
        return locationRepository.getLocation()
    }

}