package com.uri.bolanope.activities.tourney

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.uri.bolanope.ui.theme.Green80
import com.uri.bolanope.utils.SharedPreferencesManager

@Composable
fun ExploreTourneys(navController: NavHostController){
    val tourneys = remember { mutableStateOf<List<TourneyModel>?>(null) }
    val context = LocalContext.current
    val userRole = SharedPreferencesManager.getUserRole(context)

    LaunchedEffect(Unit) {
        getAllTourneys { result ->
            if (result != null) {
                tourneys.value = result
            } else {
                Toast.makeText(context, "Falha ao carregar os torneios.", Toast.LENGTH_LONG).show()
            }
        }
    }


    Scaffold(
        topBar = { TopBar("Torneios") },
        floatingActionButton = {
            if (userRole == "admin"){
                FloatingActionButton(
                    modifier = Modifier
                        .padding(vertical = 16.dp),
                    onClick = {
                        navController.navigate("createTourney")
                    },
                    containerColor = Green80,
                    shape = CircleShape

                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Tourney", tint = Color.White)
                }
            }
        },
        content = { paddingValues ->
            tourneys.value?.let { tourneyList ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                ) {
                    items(tourneyList) { tourney ->
                        TourneyCard(tourney, navController)
                    }
                }
            } ?: run {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

        }
    )
}

@Composable
fun TourneyCard(tourney: TourneyModel, navController: NavHostController){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        onClick = {
            navController.navigate("tourney/${tourney._id}")
        },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = tourney.name,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = tourney.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Premio: ${tourney.prize}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}

fun getAllTourneys(callback: (List<TourneyModel>?) -> Unit) {
    val call = ApiClient.apiService.getAllTourneys()
    apiCall(call, callback)
}
