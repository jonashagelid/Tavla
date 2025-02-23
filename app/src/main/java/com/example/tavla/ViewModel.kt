package com.example.tavla


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.api.ApolloResponse
import com.example.tavla.data.StopPlaceFeature
import com.example.tavla.data.StopPlacesResponse
import com.example.tavla.network.ApolloClient
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

    private val _stopDetails = MutableStateFlow<ApolloResponse<StopPlaceQuery.Data>?>(null)
    val stopDetails: StateFlow<ApolloResponse<StopPlaceQuery.Data>?> = _stopDetails.asStateFlow()

    private var _departuresByLine = MutableStateFlow<Map<String, List<StopPlaceQuery.EstimatedCall>>>(emptyMap())
    val departuresByLine: StateFlow<Map<String, List<StopPlaceQuery.EstimatedCall>>> = _departuresByLine.asStateFlow()

    private val _selectedLine = MutableStateFlow<String?>(null)
    val selectedLine: StateFlow<String?> = _selectedLine.asStateFlow()

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

    fun fetchStopDetails(id: String) {
        viewModelScope.launch {
            try {
                val response = ApolloClient.apolloClient
                    .query(StopPlaceQuery(id))
                    .execute()
                _stopDetails.value = response

                response.data?.stopPlace?.estimatedCalls?.let { calls ->
                    val callsByLine: Map<String, List<StopPlaceQuery.EstimatedCall>> = calls
                        .groupBy { it.serviceJourney.journeyPattern?.line?.id?.split(":")?.lastOrNull() ?: "Unknown" }

                    _departuresByLine.value = callsByLine
                    _selectedLine.value = callsByLine.keys.firstOrNull()
                }


            } catch (e: Exception) {
                e.printStackTrace()
                _stopDetails.value = null
            }
        }
    }

    fun selectLine(line: String) {
        _selectedLine.value = line
    }
}