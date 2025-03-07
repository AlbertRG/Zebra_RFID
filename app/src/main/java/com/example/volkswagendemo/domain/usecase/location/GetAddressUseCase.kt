package com.example.volkswagendemo.domain.usecase.location

import com.example.volkswagendemo.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAddressUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {

    operator fun invoke(): Flow<String> {
        return locationRepository.getAddress()
    }

}