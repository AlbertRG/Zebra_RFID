package com.example.volkswagendemo.domain.usecase.workshop

import com.example.volkswagendemo.domain.repository.WorkshopRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWorkshopUseCase @Inject constructor(
    private val workshopRepository: WorkshopRepository
) {

    operator fun invoke(): Flow<String> {
        return workshopRepository.getWorkshopName()
    }

}