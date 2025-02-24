package com.example.tavla.network

import com.example.tavla.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

object OpenRouteApi {
    private const val BASE_URL = "https://api.openrouteservice.org/v2/directions/foot-walking"
    private const val apiKey: String = BuildConfig.API_KEY


    suspend fun getWalkingDistance(
        originLat: Double,
        originLon: Double,
        destLat: Double,
        destLon: Double
    ): Pair<Float, String>? {
        val url = "$BASE_URL?api_key=$apiKey&start=$originLon,$originLat&end=$destLon,$destLat"

        return withContext(Dispatchers.IO) {
            try {
                val response = URL(url).readText()
                val jsonObject = JSONObject(response)
                val features = jsonObject.getJSONArray("features")
                if (features.length() > 0) {
                    val firstFeature = features.getJSONObject(0)
                    val properties = firstFeature.getJSONObject("properties")
                    val segments = properties.getJSONArray("segments")
                    if (segments.length() > 0) {
                        val firstSegment = segments.getJSONObject(0)
                        val distanceMeters = firstSegment.getDouble("distance").toFloat()
                        val durationSeconds = firstSegment.getDouble("duration").toInt()
                        val durationText = "${durationSeconds / 60} min" // Convert to minutes

                        return@withContext Pair(distanceMeters, durationText)
                    }
                }
                null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

}
