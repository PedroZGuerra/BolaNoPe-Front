package com.uri.bolanope.activities.tourney

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.uri.bolanope.components.TopBar
import com.uri.bolanope.model.TourneyModel
import com.uri.bolanope.services.ApiClient
import com.uri.bolanope.services.apiCall
import com.uri.bolanope.utils.SharedPreferencesManager

@Composable
fun EditTourney(navController: NavHostController, tourneyId: String) {
    val context = LocalContext.current
    val userToken = SharedPreferencesManager.getToken(context)
    val tourney = remember { mutableStateOf<TourneyModel?>(null) }

    LaunchedEffect(tourneyId) {
        getTourneyById(tourneyId) { result ->
            if (result != null) {
                tourney.value = result
            } else {
                Toast.makeText(context, "Torneio não encontrado", Toast.LENGTH_LONG).show()
            }
        }
    }

    Scaffold(
        topBar = { TopBar("Editar Torneio") },
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
            if(tourney.value != null){
                TextField(
                    value = tourney.value!!.name ?: "",
                    onValueChange = { tourney.value!!.name = it },
                    label = { Text("Nome do Torneio") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = tourney.value!!.description,
                    onValueChange = { tourney.value!!.description = it },
                    label = { Text("Descrição") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = tourney.value!!.prize,
                    onValueChange = { tourney.value!!.prize = it },
                    label = { Text("Prêmio") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row {
                    TextButton(
                        onClick = { navController.navigate("exploreTourneys") },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                    ) {
                        Text("Cancelar")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = {
                            val updatedTourney = tourney.value?.copy(
                                _id = null,
                                name = tourney.value!!.name,
                                description = tourney.value!!.description,
                                prize = tourney.value!!.prize,
                            )
                            updatedTourney?.let {
                                updateTourney(tourneyId, it, userToken!!) { response ->
                                    if (response != null) {
                                        Toast.makeText(context, "Torneio atualizado com sucesso", Toast.LENGTH_LONG).show()
                                        navController.popBackStack()
                                    } else {
                                        Toast.makeText(context, "Falha ao atualizar o torneio", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        }
                    ) {
                        Text("Salvar Alterações")
                    }
                }
            }else{
                Text("Carregando...")
            }
        }
    }
}

fun updateTourney(id: String, tourneyModel: TourneyModel, userToken: String, callback: (TourneyModel?) -> Unit) {
    val call = ApiClient.apiService.updateTourney(id, tourneyModel, "Bearer $userToken")
    apiCall(call, callback)
}
