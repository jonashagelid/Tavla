package com.example.tavla.network

import com.example.tavla.data.StopPlacesResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface GeocoderApi {

    @GET("geocoder/v1/autocomplete")
    suspend fun autocomplete(
        @Query("text") query: String,
        @Query("size") size: Int = 10,
        @Query("layers") layers: String = "venue"
    ): StopPlacesResponse

    companion object {
        fun create(): GeocoderApi {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.entur.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(GeocoderApi::class.java)
        }
    }
}
