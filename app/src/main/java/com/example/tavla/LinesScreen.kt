package com.example.tavla

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.tavla.ui.theme.TavlaTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinesScreen(stopId: String, navController: NavHostController, viewModel: ViewModel, onBackClick: () -> Unit) {
    val stopData by viewModel.stopDetails.collectAsState()


    val name = stopData?.data?.stopPlace?.name

    LaunchedEffect(stopId) {
        viewModel.fetchStopDetails(stopId)
    }

  TavlaTheme {
      Scaffold(
          topBar = {
              TopAppBar(
                  title = { Text("Tavla" + (name?.let { " - $it" } ?: "")) },
                  navigationIcon = {
                      IconButton(onClick =
                      { navController.popBackStack() }
                      ) {
                          Icon(imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack, contentDescription = "Back")
                      }
                  }
              )

          }
      ) {
          paddingValues ->

      Text(modifier = Modifier.padding(paddingValues), text = "hei")
      }
  }
}