package com.example.tavla



import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.api.ApolloResponse
import com.example.tavla.data.StopPlaceFeature
import com.example.tavla.data.StopPlacesResponse
import com.example.tavla.network.ApolloClient
import com.example.tavla.network.GeocoderApi
import com.example.tavla.network.OpenRouteApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class ViewModel (private val geocoderApi: GeocoderApi, ) : ViewModel() {

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

    private val _userLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val userLocation: StateFlow<Pair<Double, Double>?> = _userLocation

    private val _stopDurations = MutableStateFlow<Map<String, String>>(emptyMap())
    val stopDurations: StateFlow<Map<String, String>> = _stopDurations

    fun setUserLocation(latitude: Double, longitude: Double) {
        _userLocation.value = Pair(latitude, longitude)
    }

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
    fun sortTransportMode(transportMode: String?): String {
        return when (transportMode) {
            "bus", "coach", "trolleybus" -> "Buss"
            "rail", "monorail", "funicular" -> "Tog"
            "air" -> "Flyplass"
            "water" -> "Ferge"
            "metro", "tram", "cableway" -> "T-bane"
            else -> "Annet"
        }
    }

    fun getCategoryColor(category: String): Color {
        return when (category) {
            "Buss" -> Color(0xFFD32F2F)
            "Tog" -> Color(0xFF1976D2)
            "Flyplass" -> Color(0xFFC2185B)
            "Ferge" -> Color(0xFF0097A7)
            "T-bane", "Trikk" -> Color(0xFF388E3C)
            else -> Color(0xFF757575)
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

    fun fetchWalkingDurationForStops(stopId: String, longitude: Double, latitude: Double) {
        viewModelScope.launch {
            userLocation.value?.let { userLoc ->
                val userLat = userLoc.first
                val userLon = userLoc.second
                val result = OpenRouteApi.getWalkingDistance(userLat, userLon, latitude, longitude)
                result?.let { (_, durationText) ->
                    _stopDurations.value = _stopDurations.value.toMutableMap().apply {
                        put(stopId, durationText)
                    }
                }
            }
        }
    }

}