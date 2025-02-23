package com.example.tavla

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.tavla.ui.theme.TavlaTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController, viewModel: ViewModel) {
    val query by viewModel.searchString.collectAsState()
    val stops by viewModel.stops.collectAsState()
    TavlaTheme {
        Scaffold (
            topBar = {
                TopAppBar(
                    title = { Text("Tavla") },
                )
            }
        ) {
            paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
                   ,
            ) {

                Spacer(modifier = Modifier.height(25.dp))
                TextField(
                    value = query,
                    onValueChange = { newValue -> viewModel.onSearchStringChanged(newValue) },
                    label = { Text("Søk etter stoppesteder") },
                    modifier = Modifier.fillMaxWidth()
                )

                LazyColumn {
                    items(stops) { stop ->
                        val name = stop.properties?.label ?: "Empty value"
                        val categories = stop.properties?.category ?: emptyList()
                        val stopId = stop.properties?.id ?: "Empty value"

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate("lines/$stopId")
                                    println("Clicked on stop: $name")
                                }
                                .padding(8.dp),
                            elevation = CardDefaults.cardElevation(4.dp)){
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(text = name)
                                LazyRow {
                                    items(
                                        categories) { category ->

                                        val categoryColor = viewModel.getCategoryColor(category)
                                        Box(
                                            modifier = Modifier
                                                .background(categoryColor, shape = RoundedCornerShape(8.dp))
                                                .padding(4.dp)
                                        ) {
                                            Text(
                                                text = category,
                                                color = Color.White,
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


