package com.example.volkswagendemo.domain.usecase.location

import com.example.volkswagendemo.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocationSavedUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {

    operator fun invoke(): Flow<Boolean> {
        return locationRepository.getLocationSaved()
    }

}