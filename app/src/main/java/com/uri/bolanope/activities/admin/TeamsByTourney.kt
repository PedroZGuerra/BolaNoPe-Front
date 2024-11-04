package com.uri.bolanope.activities.admin

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.uri.bolanope.ui.theme.Green80
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@Composable
fun TeamsByTourney(navController: NavHostController) {
    val context = LocalContext.current
    val tourneys = remember { mutableStateOf<List<TourneyModel>?>(null) }
    val filteredTourneys = remember { mutableStateOf<List<TourneyModel>>(emptyList()) }

    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    fun openDatePicker(onDateSelected: (String) -> Unit) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                onDateSelected(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    LaunchedEffect(tourneys) {
        getAllTourneys { result ->
            tourneys.value = result
            filteredTourneys.value = result ?: emptyList()
        }
    }

    fun filterTourneys() {
        if (startDate == "" || endDate == "" ) return;

        val start = dateFormat.parse(startDate)
        val end = dateFormat.parse(endDate)

        filteredTourneys.value = tourneys.value?.filter { tourney ->
            val tourneyDateFrom = dateFormat.parse(tourney.date_from)
            tourneyDateFrom.after(start) && tourneyDateFrom.before(end)
        } ?: emptyList()
    }

    val barChartData = filteredTourneys.value.map { tourney ->
        PieChartData(tourney.name, tourney.id_teams.size.toFloat())
    }

    Scaffold(
        topBar = { TopBar("Times Inscritos Por Torneio") },
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding(),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .clickable { openDatePicker { startDate = it } }
                    .padding(8.dp)
            ) {
                Text("Data Inicial: $startDate")
            }

            Row(
                modifier = Modifier
                    .clickable { openDatePicker { endDate = it } }
                    .padding(8.dp)
            ) {
                Text("Data Final: $endDate")
            }

            Button(
                onClick = { filterTourneys() }
            ) {
                Text("Filtrar")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (barChartData.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxSize()) {
                    BarChartComposable(barChartData, context)
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Não existem resultados para o filtro aplicado", color = Color.Gray)
                }
            }
        }
    }
}
