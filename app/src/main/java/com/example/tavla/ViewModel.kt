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
                            _stops.value = response.features ?: emptyList()
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

}