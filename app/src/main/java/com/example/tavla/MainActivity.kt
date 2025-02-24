package com.example.tavla

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.tavla.network.GeocoderApi
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : ComponentActivity() {
    private val geocoderApi = GeocoderApi.create()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val viewModel by viewModels<ViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return ViewModel(geocoderApi) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                fetchCurrentLocation()
            }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            fetchCurrentLocation()
        }


        setContent {
            val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "search") {
                    composable("search") { SearchScreen(navController, viewModel) }
                    composable("lines/{stopId}") { backStackEntry ->
                        val stopId = backStackEntry.arguments?.getString("stopId") ?: return@composable
                        LinesScreen(stopId, navController, viewModel) { navController.popBackStack() }
                    }

                }
        }
    }
    private fun fetchCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        viewModel.setUserLocation(latitude, longitude)
                        println("Fetched location: Latitude=$latitude, Longitude=$longitude")
                    }
                }
                .addOnFailureListener { e ->
                    println("Failed to get location: ${e.localizedMessage}")
                }
        }
    }
}
