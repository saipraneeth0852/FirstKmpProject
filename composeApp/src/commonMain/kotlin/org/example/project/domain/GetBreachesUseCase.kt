package org.example.project.domain

import org.example.project.data.BreachRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetBreachesUseCase(private val repository: BreachRepository) {
    suspend operator fun invoke(): Result<List<Breach>> = withContext(Dispatchers.Default) {
        try {
            val breaches = repository.getBreaches()
            Result.success(breaches)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}