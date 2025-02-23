package com.example.tavla

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.tavla.ui.theme.TavlaTheme
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinesScreen(
    stopId: String,
    navController: NavHostController,
    viewModel: ViewModel,
    onBackClick: () -> Unit
) {
    val stopData by viewModel.stopDetails.collectAsState()
    val selectedLine by viewModel.selectedLine.collectAsState()
    val departuresByLine by viewModel.departuresByLine.collectAsState()

    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
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
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (departuresByLine.keys.size > 1) {
                    LazyRow {
                        items(departuresByLine.keys.toList()) { line ->

                            val transportMode = departuresByLine[line]?.firstOrNull()
                                ?.serviceJourney?.journeyPattern?.line?.transportMode

                            val category = viewModel.sortTransportMode(
                                transportMode.toString()
                            )
                            val backgroundColor = viewModel.getCategoryColor(category)
                            val isSelected = selectedLine == line
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isSelected) Color.White else backgroundColor,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.selectLine(line) }
                                    .padding(if(isSelected) 12.dp else 8.dp)
                            ) {
                                Text(
                                    text = line,
                                    color = if (isSelected) Color.Black else Color.White,
                                    fontSize = if (isSelected) 24.sp else 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                selectedLine?.let { line ->
                    departuresByLine[line]?.firstOrNull()?.let { firstCall ->
                        val lineName = firstCall.serviceJourney.journeyPattern?.line?.name
                        val transportMode = firstCall.serviceJourney.journeyPattern?.line?.transportMode
                        val category = viewModel.sortTransportMode(transportMode.toString())
                        val backgroundColor = viewModel.getCategoryColor(category)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        backgroundColor,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = line,
                                    fontSize = 24.sp,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))

                            Column {
                                Text(
                                    text = lineName ?: "Unknown Line",
                                    fontSize = 20.sp
                                )
                                Text(
                                    text = "($category)",
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Avgang", modifier = Modifier.weight(2f))
                                Text(text = "Avgangstid", modifier = Modifier.weight(2f))
                                Text(text = "Sanntid", modifier = Modifier.weight(1f))
                            }

                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                color = Color.Gray
                            )

                    }

                    selectedLine?.let { line ->
                        departuresByLine[line]?.let { departures ->
                            LazyColumn {
                                items(departures) { call ->
                                    val aimedTime = ZonedDateTime.parse(call.aimedDepartureTime.toString())
                                        .format(timeFormatter)
                                    val expectedTime = ZonedDateTime.parse(call.expectedDepartureTime.toString())
                                        .format(timeFormatter)
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        elevation = CardDefaults.cardElevation(4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = call.destinationDisplay?.frontText ?: "",
                                                modifier = Modifier.weight(2f)
                                            )

                                            Row(modifier = Modifier.weight(2f)) {
                                                if (aimedTime == expectedTime) {
                                                    Text(text = aimedTime)
                                                } else {
                                                    Text(
                                                        text = aimedTime,
                                                        textDecoration = TextDecoration.LineThrough,
                                                        color = Color.Red
                                                    )
                                                    Text(text = expectedTime, color = Color(0xFF43A047))
                                                }
                                            }
                                            Row(
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(12.dp)
                                                        .background(
                                                            if (call.realtime) Color(0xFF43A047) else Color.Red,
                                                            shape = CircleShape
                                                        )
                                                        .align(Alignment.CenterVertically)
                                                ) {
                                                    Spacer(modifier = Modifier.width(24.dp))
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
        }
    }
}
