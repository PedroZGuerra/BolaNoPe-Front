package com.uri.bolanope.activities.tourney

import android.app.DatePickerDialog
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import com.uri.bolanope.components.TopBar
import com.uri.bolanope.model.TeamModel
import com.uri.bolanope.model.TourneyModel
import com.uri.bolanope.services.ApiClient
import com.uri.bolanope.services.apiCall
import com.uri.bolanope.utils.SharedPreferencesManager
import java.util.Calendar

@Composable
fun CreateTourney(navController: NavHostController) {
    val context = LocalContext.current
    val userId = SharedPreferencesManager.getUserId(context)
    val userToken = SharedPreferencesManager.getToken(context)
    var tourneyName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var prize by remember { mutableStateOf("") }
    val teams = remember { mutableStateOf<List<TeamModel>?>(null) }
    var selectedTeams by remember { mutableStateOf<List<TeamModel?>>(List(5) { null }) }
    var showTeamPopup by remember { mutableStateOf<Pair<Boolean, Int>>(Pair(false, -1)) }
    var searchQuery by remember { mutableStateOf("") }
    var dateFrom by remember { mutableStateOf("") }
    var dateUntil by remember { mutableStateOf("") }
    val calendar = Calendar.getInstance()

    val today = Calendar.getInstance().timeInMillis

    val datePickerDialogFrom = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            dateFrom = "$dayOfMonth/${month + 1}/$year"
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
    ).apply {
        datePicker.minDate = today
    }

    val datePickerDialogUntil = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            dateUntil = "$dayOfMonth/${month + 1}/$year"
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
    ).apply {
        datePicker.minDate = today
    }


    LaunchedEffect(Unit) {
    }

    Scaffold(
        topBar = { TopBar("Criar Torneio") },
        modifier = Modifier.padding(horizontal = 8.dp)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = tourneyName,
                onValueChange = { tourneyName = it },
                label = { Text("Nome do Torneio") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = prize,
                onValueChange = { prize = it },
                label = { Text("Prêmio") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { datePickerDialogFrom.show() }) {
                Text(text = if (dateFrom.isEmpty()) "Selecione a Data de Inicio" else dateFrom)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { datePickerDialogUntil.show() }) {
                Text(text = if (dateUntil.isEmpty()) "Selecione a Data Final" else dateUntil)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                TextButton(
                    onClick = {
                        navController.navigate("exploreTourneys")
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.Red
                    )
                ) {
                    Text("Cancelar")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = {
                        val tourneyModel = TourneyModel(
                            _id = null,
                            name = tourneyName,
                            description = description,
                            prize = prize,
                            id_teams = selectedTeams.mapNotNull { it?._id },
                            date_from = dateFrom,
                            date_until = dateUntil
                        )
                        createTourney(tourneyModel, userToken!!) { response ->
                            if (response != null) {
                                Toast.makeText(context, "Torneio Criado com sucesso", Toast.LENGTH_LONG).show()
                                navController.navigate("homeAdmin")
                            } else {
                                Log.d("log fodinhaaaaa", "Failed to create tourney: ${response}")
                                Toast.makeText(context, "Falha ao criar torneio", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                ) {
                    Text("Criar Torneio")
                }
            }
        }
    }
}

fun createTourney(tourneyModel: TourneyModel, userToken: String, callback: (TourneyModel?) -> Unit) {
    val call = ApiClient.apiService.createTourney(tourneyModel, "Bearer $userToken")
    apiCall(call, callback)
}
