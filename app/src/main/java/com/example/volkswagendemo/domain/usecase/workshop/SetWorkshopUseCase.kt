package com.example.volkswagendemo.domain.usecase.workshop

import com.example.volkswagendemo.domain.repository.WorkshopRepository
import javax.inject.Inject

class SetWorkshopUseCase @Inject constructor(
    private val workshopRepository: WorkshopRepository
) {

    suspend operator fun invoke(name: String) {
        workshopRepository.setWorkshopName(name)
    }

}