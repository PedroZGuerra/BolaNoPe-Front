package com.uri.bolanope.activities.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.uri.bolanope.activities.tourney.getAllTourneys
import com.uri.bolanope.components.BarChartComposable
import com.uri.bolanope.components.PieChartData
import com.uri.bolanope.components.TopBar
import com.uri.bolanope.model.TourneyModel


@Composable
fun TeamsByTourney(navController: NavHostController) {
    val context = LocalContext.current
    val tourneys = remember { mutableStateOf<List<TourneyModel>?>(null) }

    LaunchedEffect(tourneys) {
        getAllTourneys { result ->
            tourneys.value = result
        }
    }

    val barChartData = tourneys.value?.map { tourney ->
        PieChartData(tourney.name, tourney.id_teams.size.toFloat())
    } ?: emptyList()

    Scaffold(
        topBar = { TopBar("Times Por Torneio") },
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding(),
    ) { innerPadding ->
        if (barChartData.isNotEmpty()) {
            Box(modifier = Modifier.padding(16.dp)) {
                BarChartComposable(barChartData, context)
            }
        }
    }
}
