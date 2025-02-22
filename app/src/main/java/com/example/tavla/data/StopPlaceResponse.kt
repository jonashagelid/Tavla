package com.example.tavla.data

data class StopPlacesResponse(
    val type: String?,
    val features: List<StopPlaceFeature>?
)

data class StopPlaceFeature(
    val type: String?,
    val geometry: StopPlaceGeometry?,
    val properties: StopPlaceProperties?
)

data class StopPlaceGeometry(
    val type: String?,
    val coordinates: List<Double>?
)

data class StopPlaceProperties(
    val id: String?,
    val name: String?,
    val label: String?,
    val county: String?,
    val locality: String?,
    val category: List<String>?,
)

