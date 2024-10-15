package org.example.project.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.project.domain.GetBreachesUseCase
import org.example.project.domain.Breach

class BreachListViewModel(private val getBreachesUseCase: GetBreachesUseCase) : ViewModel() {
    private val _breaches = MutableStateFlow<List<Breach>>(emptyList())
    val breaches = _breaches.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private var currentPage = 0
    private val pageSize = 20
    private var isLastPage = false

    fun loadBreaches() {
        if (_isLoading.value || isLastPage) return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val newBreaches = withContext(Dispatchers.Default) {
                    getBreachesUseCase().getOrThrow()
                        .drop(currentPage * pageSize)
                        .take(pageSize)
                }

                if (newBreaches.isEmpty()) {
                    isLastPage = true
                } else {
                    _breaches.value = _breaches.value + newBreaches
                    currentPage++
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun retryLoading() {
        _error.value = null
        loadBreaches()
    }
}