package com.uri.bolanope.activities.team

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
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
import com.uri.bolanope.model.TeamModel
import com.uri.bolanope.services.ApiClient
import com.uri.bolanope.services.apiCall
import com.uri.bolanope.ui.theme.Green80
import com.uri.bolanope.utils.SharedPreferencesManager

@Composable
fun MyTeams(navController: NavHostController, isLeader: Boolean) {
    val teams = remember { mutableStateOf<List<TeamModel>?>(null) }
    val context = LocalContext.current
    val userId = SharedPreferencesManager.getUserId(context)
    val userToken = SharedPreferencesManager.getToken(context)

    LaunchedEffect(Unit) {
        if (isLeader){
            getTeamsByLeader(userToken!!) { result ->
                if (result != null) {
                    teams.value = result
                } else {
                    Toast.makeText(context, "Falha ao carregar os times.", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            getTeamsByMember(userId!!) { result ->
                result?.let { teamList ->
                    val leaderTeams = teamList.filter { team ->
                        team.leader_id != userId
                    }

                    teams.value = leaderTeams
                }
            }

        }
    }

    Scaffold(
        topBar = { TopBar("Meus Times") },
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
            Column {
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 8.dp),
                    text = if (isLeader){
                        "Times que você lidera"
                    } else {
                        "Times que você participa"
                    }
                )
                Divider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                teams.value?.let { teamList ->
                    val teamsSize = teams.value?.size!!

                    if (teamsSize > 0) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues),
                        ) {
                            items(teams.value!!) { team ->
                                TeamCard(team, navController, userId!!)
                            }
                        }
                    } else {
                        Text(
                            text = if (isLeader){
                                "Você não lidera nenhum time"
                            } else {
                                "Você não participa de nenhum time"
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp)
                        )
                    }
                }

            }
        }
    )
}

fun getTeamsByMember(id: String, callback: (List<TeamModel>?) -> Unit) {
    val call = ApiClient.apiService.getTeamsByMember(id)
    apiCall(call, callback)
}

fun getTeamsByLeader(token: String, callback: (List<TeamModel>?) -> Unit) {
    val call = ApiClient.apiService.getTeamsByLeader("Bearer $token")
    apiCall(call, callback)
}
