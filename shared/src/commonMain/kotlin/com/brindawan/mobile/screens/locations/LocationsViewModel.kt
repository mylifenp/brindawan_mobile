package com.brindawan.mobile.screens.locations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brindawan.mobile.data.Dealer
import com.brindawan.mobile.data.DealerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class UserLocation(
    val latitude: Double,
    val longitude: Double,
)

data class LocationsUiState(
    val isLoading: Boolean = false,
    val dealers: List<Dealer> = emptyList(),
    val filteredDealers: List<Dealer> = emptyList(),
    val searchQuery: String = "",
    val errorMessage: String? = null,
    val userLocation: UserLocation? = null,
    val nearestDealerId: String? = null,
)

class LocationsViewModel(
    private val dealerRepository: DealerRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LocationsUiState(isLoading = true))
    val uiState: StateFlow<LocationsUiState> = _uiState.asStateFlow()

    init {
        loadDealers()
    }

    fun loadDealers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching { dealerRepository.getDealers() }
                .onSuccess { dealers ->
                    val nearestId = findNearestDealer(dealers)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            dealers = dealers,
                            filteredDealers = dealers,
                            searchQuery = "",
                            errorMessage = null,
                            nearestDealerId = nearestId,
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Failed to load dealers",
                        )
                    }
                }
        }
    }

    fun updateUserLocation(lat: Double, lng: Double) {
        val location = UserLocation(lat, lng)
        _uiState.update { it.copy(userLocation = location) }

        val nearestId = findNearestDealer(_uiState.value.filteredDealers)
        _uiState.update { it.copy(nearestDealerId = nearestId) }
    }

    fun updateSearchQuery(query: String) {
        val trimmedQuery = query.trim()
        _uiState.update { state ->
            val filtered = filterDealers(state.dealers, trimmedQuery)
            state.copy(
                searchQuery = query,
                filteredDealers = filtered,
                nearestDealerId = findNearestDealer(filtered),
            )
        }
    }

    private fun filterDealers(dealers: List<Dealer>, query: String): List<Dealer> {
        if (query.isBlank()) return dealers
        val q = query.lowercase()
        return dealers.filter { dealer ->
            dealer.shopName.lowercase().contains(q) ||
                    dealer.address.city.lowercase().contains(q) ||
                    dealer.address.municipality.lowercase().contains(q) ||
                    dealer.phones.any { it.lowercase().contains(q) }
        }
    }

    private fun findNearestDealer(dealers: List<Dealer>): String? {
        val userLoc = _uiState.value.userLocation ?: return dealers.firstOrNull()?.id

        return dealers
            .mapNotNull { dealer ->
                val coordinate = dealer.address.coordinate
                if (coordinate != null) {
                    val distance = calculateDistance(
                        userLoc.latitude, userLoc.longitude,
                        coordinate.latitude, coordinate.longitude
                    )
                    dealer.id to distance
                } else {
                    null
                }
            }
            .minByOrNull { it.second }
            ?.first
    }

    private fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val earthRadiusKm = 6371.0
        val dLatRad = toRadians(lat2 - lat1)
        val dLonRad = toRadians(lon2 - lon1)
        val a = sin(dLatRad / 2) * sin(dLatRad / 2) +
                cos(toRadians(lat1)) * cos(toRadians(lat2)) *
                sin(dLonRad / 2) * sin(dLonRad / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadiusKm * c
    }

    private fun toRadians(degrees: Double): Double = degrees * PI / 180.0
}
