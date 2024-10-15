package org.example.project.data

import org.example.project.domain.Breach
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BreachRepository(private val api: BreachApi) {
    suspend fun getBreaches(): List<Breach> = withContext(Dispatchers.Default) {
        api.getBreaches()
    }
}