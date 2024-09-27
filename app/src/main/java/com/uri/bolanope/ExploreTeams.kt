package com.uri.bolanope

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.uri.bolanope.model.TeamModel
import com.uri.bolanope.services.ApiClient
import com.uri.bolanope.services.apiCall
import com.uri.bolanope.ui.theme.Green80

@Composable
fun ExploreTeams(navController: NavHostController) {
    val teams = remember { mutableStateOf<List<TeamModel>?>(null) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        getAllTeams { result ->
            if (result != null) {
                teams.value = result
            } else {
                Toast.makeText(context, "Falha ao carregar os times.", Toast.LENGTH_LONG).show()
            }
        }
    }

    Scaffold(
        topBar = { TopBar("Times") },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier
                    .padding(vertical = 16.dp),
                onClick = {
                    navController.navigate("createTeam")
            },
                containerColor = Green80,
                shape = CircleShape

            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Team", tint = Color.White)
            }
        },
        content = { paddingValues ->
            teams.value?.let { teamList ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                ) {
                    items(teamList) { team ->
                        TeamCard(team, navController)
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
fun TeamCard(team: TeamModel, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        onClick = {
            navController.navigate("team/${team._id}")
        },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = team.name!!, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = team.description!!, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

fun getAllTeams(callback: (List<TeamModel>?) -> Unit) {
    val call = ApiClient.apiService.getAllTeams()
    apiCall(call, callback)
}