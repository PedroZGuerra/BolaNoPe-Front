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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.uri.bolanope.activities.field.getFieldById
import com.uri.bolanope.activities.field.getMostReservedTimes
import com.uri.bolanope.activities.tourney.getAllTourneys
import com.uri.bolanope.components.BarChartComposable
import com.uri.bolanope.components.PieChartData
import com.uri.bolanope.components.TopBar
import com.uri.bolanope.model.FieldModel
import com.uri.bolanope.model.MostReservedTimesItemModel
import com.uri.bolanope.model.MostReservedTimesModel
import com.uri.bolanope.model.TourneyModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun MostReservedTimes(navController: NavHostController, fieldId: String) {
    val context = LocalContext.current
    val mostReservedTimes = remember { mutableStateOf<List<MostReservedTimesModel>>(emptyList()) }
    val field = remember { mutableStateOf<FieldModel?>(null) }

    LaunchedEffect(Unit) {
        getMostReservedTimes { result ->
            mostReservedTimes.value = result ?: emptyList()
        }
        getFieldById(fieldId) { result ->
            field.value = result
        }
    }

    val chartData = mostReservedTimes.value
        .filter { it.id_field == fieldId }
        .flatMap { model ->
            model.most_reserved_times.map { item ->
                PieChartData(item.time, item.count.toFloat())
            }
        }

    Scaffold(
        topBar = { TopBar("Horários Mais Reservados") },
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
            if(field.value != null) {
                Text(
                    text = "Quadra: ${field.value!!.name}",
                    style = MaterialTheme.typography.h6
                )
            }
            if (chartData.isNotEmpty()) {
                Box(modifier = Modifier
                    .fillMaxSize()
                ) {
                    BarChartComposable(chartData, context)
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