package com.example.tavla


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tavla.data.StopPlaceFeature
import com.example.tavla.data.StopPlacesResponse
import com.example.tavla.network.GeocoderApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ViewModel (private val geocoderApi: GeocoderApi) : ViewModel() {


    private val _searchString = MutableStateFlow("")
    val searchString: StateFlow<String> = _searchString.asStateFlow()

    private val _stops = MutableStateFlow<List<StopPlaceFeature>>(emptyList())
    val stops: StateFlow<List<StopPlaceFeature>> = _stops.asStateFlow()

    init {
        viewModelScope.launch {
            _searchString.collect { query ->

                if (query.isNotBlank()) {
                    launch {
                        try {
                            val response: StopPlacesResponse = geocoderApi.autocomplete(query)
                            _stops.value = response.features?.map { feature ->
                                feature.copy(
                                    properties = feature.properties?.copy(
                                        category = feature.properties.category
                                            ?.map { sortCategories(it) }
                                            ?.distinct()
                                    )
                                )
                            } ?: emptyList()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            _stops.value = emptyList()
                        }
                    }
                } else {
                    _stops.value = emptyList()
                }
            }
        }
    }

    fun onSearchStringChanged(newValue: String) {
        _searchString.value = newValue
    }

    private fun sortCategories(category: String): String {
        return when (category) {
            "onstreetBus", "busStation", "coachStation" -> "Buss"
            "onstreetTram", "tramStation" -> "Trikk"
            "railStation", "vehicleRailInterchange" -> "Tog"
            "metroStation" -> "T-bane"
            "harbourPort", "ferryPort", "ferryStop" -> "Ferge"
            "airport" -> "Flyplass"
            "liftStation" -> "Heis?"
            else -> "Annet"
        }
    }

}