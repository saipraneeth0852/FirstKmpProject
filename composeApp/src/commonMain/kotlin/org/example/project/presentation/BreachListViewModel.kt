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

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private var allBreaches: List<Breach> = emptyList()

    private val _filterState = MutableStateFlow(FilterState.ALL)
    val filterState = _filterState.asStateFlow()

    enum class FilterState {
        ALL, VERIFIED, NOT_VERIFIED
    }

    fun setFilterState(state: FilterState) {
        _filterState.value = state
        filterBreaches()
    }

    private fun filterBreaches() {
        val query = _searchQuery.value.lowercase()
        val filteredBySearch = if (query.isEmpty()) {
            allBreaches
        } else {
            allBreaches.filter { breach ->
                breach.name.lowercase().contains(query) ||
                        breach.title.lowercase().contains(query) ||
                        breach.domain.lowercase().contains(query)
            }
        }

        _breaches.value = when (_filterState.value) {
            FilterState.ALL -> filteredBySearch
            FilterState.VERIFIED -> filteredBySearch.filter { it.isVerified }
            FilterState.NOT_VERIFIED -> filteredBySearch.filter { !it.isVerified }
        }
    }

    fun loadBreaches() {
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                allBreaches = withContext(Dispatchers.Default) {
                    getBreachesUseCase().getOrThrow()
                }
                filterBreaches()
            } catch (e: Exception) {
                _error.value = e.message ?: "An unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        filterBreaches()
    }

//    private fun filterBreaches() {
//        val query = _searchQuery.value.lowercase()
//        _breaches.value = if (query.isEmpty()) {
//            allBreaches
//        } else {
//            allBreaches.filter { breach ->
//                breach.name.lowercase().contains(query) ||
//                        breach.title.lowercase().contains(query) ||
//                        breach.domain.lowercase().contains(query)
//            }
//        }
//    }

    fun retryLoading() {
        _error.value = null
        loadBreaches()
    }
}