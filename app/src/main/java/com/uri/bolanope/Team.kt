package com.uri.bolanope

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.uri.bolanope.model.TeamModel
import com.uri.bolanope.services.ApiClient
import com.uri.bolanope.services.apiCall
import com.uri.bolanope.utils.SharedPreferencesManager

@Composable
fun Team(navController: NavHostController, teamId: String?) {
    val context = LocalContext.current
    val team = remember { mutableStateOf<TeamModel?>(null) }
    val user_id = SharedPreferencesManager.getUserId(context)

    LaunchedEffect(teamId) {
        if (teamId != null) {
            getTeamById(teamId) { result ->
                if (result != null) {
                    team.value = result
                } else {
                    Toast.makeText(context, "Falha ao carregar o time.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    Scaffold(
        topBar = { team.value?.let { TopBar(it.name) } },
        content = { paddingValues ->
            if (team.value != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(text = "${team.value?.description}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Líder: ${team.value?.leader_id}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (team.value?.members_id?.isNotEmpty() == true) {
                        Text(text = "Members:", style = MaterialTheme.typography.titleMedium)
                        team.value?.members_id?.forEach { memberId ->
                            TextButton(onClick = {
                                // todo
                            }) {
                                Text(
                                    text = memberId,
                                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Blue)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    } else {
                        Text(text = "Sem membros no time", style = MaterialTheme.typography.bodyMedium)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    if (user_id == team.value?.leader_id){
                        Row {
                            Button(onClick = {
                                // todo
                            }) {
                                Text("Editar Time")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = {
                                // todo
                            }) {
                                Text("Deletar Time")
                            }
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    )
}

fun getTeamById(team_id: String, callback: (TeamModel?) -> Unit) {
    val call = ApiClient.apiService.getTeamById(team_id)
    apiCall(call, callback)
}
