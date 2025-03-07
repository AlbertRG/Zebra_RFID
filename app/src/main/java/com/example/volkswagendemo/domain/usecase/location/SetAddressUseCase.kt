package com.example.volkswagendemo.domain.usecase.location

import com.example.volkswagendemo.domain.repository.LocationRepository
import javax.inject.Inject

class SetAddressUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(address: String) {
        locationRepository.setAddress(address)
    }

}