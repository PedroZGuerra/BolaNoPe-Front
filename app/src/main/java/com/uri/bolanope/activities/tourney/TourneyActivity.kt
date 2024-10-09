package com.uri.bolanope.activities.tourney

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.uri.bolanope.activities.team.deleteTeam
import com.uri.bolanope.activities.team.getTeamById
import com.uri.bolanope.activities.team.getTeamsByLeader
import com.uri.bolanope.model.TeamModel
import com.uri.bolanope.model.TourneyModel
import com.uri.bolanope.model.UserModel
import com.uri.bolanope.services.ApiClient
import com.uri.bolanope.services.apiCall
import com.uri.bolanope.ui.theme.Green80
import com.uri.bolanope.utils.SharedPreferencesManager

@Composable
fun Tourney(tourneyId: String, navController: NavHostController) {
    val teamNames = remember { mutableStateListOf<String>() }
    val teamsUserIsLeader = remember { mutableStateOf<List<TeamModel>?>(null) }
    val context = LocalContext.current
    val userRole = SharedPreferencesManager.getUserRole(context)
    val userToken = SharedPreferencesManager.getToken(context)
    val showDeleteDialog = remember { mutableStateOf(false) }
    val tourney = remember { mutableStateOf<TourneyModel?>(null) }

    var selectedTeams by remember { mutableStateOf<List<TeamModel>?>(null) }
    var showTeamPopup by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(tourneyId) {
        getTourneyById(tourneyId) { result ->
            if (result != null) {
                tourney.value = result
                result.id_teams.forEach { teamId ->
                    getTeamById(teamId) { teamResult ->
                        if (teamResult != null) {
                            teamNames.add(teamResult.name!!)
                        } else {
                            Toast.makeText(context, "Time ${teamId} não encontrado", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } else {
                Toast.makeText(context, "Torneio não encontrado", Toast.LENGTH_LONG).show()
            }
        }
        getTeamsByLeader(userToken!!) { result ->
            if (result != null) {
                teamsUserIsLeader.value = result
            } else {
                Toast.makeText(context, "Falha ao carregar os times.", Toast.LENGTH_LONG).show()
            }
        }
    }

    tourney.value?.let { currentTourney ->
        Scaffold(
            topBar = { TopBar(currentTourney.name) },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Descrição:",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = currentTourney.description ?: "",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Premiação:",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = currentTourney.prize,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Times Participantes:", style = MaterialTheme.typography.titleMedium)

                    val tourneySize = currentTourney.id_teams.size
                    if (tourneySize > 0){
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            items(currentTourney.id_teams.size) { index ->
                                val teamName = teamNames.getOrNull(index) ?: "Carregando..."
                                val cardColor = Green80
                                val textColor = Color.White
                                Card(
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .width(120.dp)
                                        .height(60.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = cardColor
                                    )
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Text(
                                            text = teamName,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = textColor
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Text("Nenhum time participante")
                    }

                    if (userRole == "admin") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(onClick = {
                                navController.navigate("editTourney/${currentTourney._id}")
                            }) {
                                Text("Editar Torneio")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    showDeleteDialog.value = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                            ) {
                                Text("Deletar Torneio")
                            }
                        }
                    }
                    if ((teamsUserIsLeader.value?.size ?: 0) > 0) {
                        Button(
                            onClick = {
                                showTeamPopup = true  // Set to true to show the popup
                            }
                        ) {
                            Text("Inscrever meu time")
                        }
                    }
                }
            }
        )
        if (showTeamPopup) {
            TeamSelectionPopup(
                teams = teamsUserIsLeader.value ?: emptyList(),
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                selectedTeams = selectedTeams?.filterNotNull() ?: emptyList(),
                onMemberToggle = { team, isSelected ->
                    // Handle team selection toggle logic here
                },
                onDismiss = { showTeamPopup = false }  // Close the popup
            )
        }

        if (showDeleteDialog.value) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog.value = false },
                title = { Text("Confirmar Exclusão") },
                text = { Text("Você tem certeza que deseja deletar este torneio?") },
                confirmButton = {
                    Button(
                        onClick = {
                            showDeleteDialog.value = false
                            val authHeader = SharedPreferencesManager.getToken(context)
                            deleteTeam(currentTourney._id!!, authHeader!!) { result ->
                                Toast.makeText(context, "Tourneio deletado com sucesso.", Toast.LENGTH_LONG).show()
                                navController.popBackStack()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Deletar")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDeleteDialog.value = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun TeamSelectionPopup(
    teams: List<TeamModel>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedTeams: List<TeamModel>,
    onMemberToggle: (TeamModel, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val filteredUsers = teams.filter { team ->
        team.name!!.contains(searchQuery, ignoreCase = true)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Selecione o Time") },
        text = {
            Column {
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    label = { Text("Pesquisar") }
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn {
                    items(filteredUsers) { user ->
                        val isChecked = selectedTeams.contains(user)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { isSelected ->
                                    onMemberToggle(user, isSelected)
                                }
                            )
                            Text(user.name!!)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Concluído")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.Red
                )
            ) {
                Text("Cancelar")
            }
        }
    )
}
fun getTourneyById(id: String, callback: (TourneyModel?) -> Unit) {
    val call = ApiClient.apiService.getTourneyById(id)
    apiCall(call, callback)
}
