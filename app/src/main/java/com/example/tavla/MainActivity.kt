package com.example.tavla

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.lifecycle.ViewModelProvider
import com.example.tavla.network.GeocoderApi
import com.example.tavla.ui.theme.TavlaTheme
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
   private val geocoderApi = GeocoderApi.create()


    private val viewModel by viewModels<ViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return ViewModel(geocoderApi) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "search") {
                    composable("search") { SearchScreen(navController, viewModel) }
                    composable("lines") { LinesScreen(navController, viewModel) }
                }

        }
    }
}
