package com.example.tavla

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


@Composable
fun SearchScreen(viewModel: ViewModel, modifier: Modifier = Modifier) {
    val query by viewModel.searchString.collectAsState()
    val stops by viewModel.stops.collectAsState()

    Column(
        modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
        ) {

        Spacer(modifier = Modifier.height(100.dp))
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

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = name)
                LazyRow {
                    items(
                    categories) { category ->
                        Text(text = category, modifier = Modifier.padding(end = 8.dp))                    }
                }
            }
        }
    }
}