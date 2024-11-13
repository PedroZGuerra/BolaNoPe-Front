package com.uri.bolanope.activities.tourney

import android.content.Context
import android.content.Intent
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import com.uri.bolanope.model.addTeamToTourneyBody
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
    val userId = SharedPreferencesManager.getUserId(context)
    val showDeleteDialog = remember { mutableStateOf(false) }
    val tourney = remember { mutableStateOf<TourneyModel?>(null) }
    var selectedTeamsAdd by remember { mutableStateOf<List<TeamModel>>(emptyList()) }
    var selectedTeamsRemove by remember { mutableStateOf<List<TeamModel>>(emptyList()) }

    var showAddTeamPopup by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var mode by remember { mutableStateOf("add") }

    LaunchedEffect(tourneyId) {
        getTourneyById(tourneyId) { result ->
            if (result != null) {
                tourney.value = result
                result.id_teams.forEach { teamId ->
                    getTeamById(teamId) { teamResult ->
                        if (teamResult != null) {
                            if (teamResult.leader_id == userId) {
                                selectedTeamsRemove = selectedTeamsRemove + teamResult
                            }
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
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        shareTourney(context, currentTourney)
                    },
                    modifier = Modifier.padding(bottom = 16.dp),
                    containerColor = Green80,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Compartilhar")
                }
            },
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
                    if (tourneySize > 0) {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            items(teamNames) { teamName ->
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
                    if(userRole != "admin") {
                        if (teamsUserIsLeader.value?.isNotEmpty() == true) {
                            Row(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                            ) {
                                Button(
                                    onClick = {
                                        showAddTeamPopup = true
                                        mode = "add"
                                    }
                                ) {
                                    Text("Inscrever meu time")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        showAddTeamPopup = true
                                        mode = "remove"
                                    },
                                    colors = ButtonDefaults.textButtonColors(
                                        containerColor = Color.Red,
                                        contentColor = Color.White
                                    )
                                ) {
                                    Text("Retirar meu time")
                                }
                            }
                        }
                    }
                }
            }
        )
        if (showAddTeamPopup) {
            TeamSelectionPopup(
                tourneyId = tourneyId,
                context = context,
                teams = teamsUserIsLeader.value ?: emptyList(),
                searchQuery = searchQuery,
                navController = navController,
                mode = mode,
                teamNames = teamNames,
                onSearchQueryChange = { searchQuery = it },
                selectedTeamsAdd = selectedTeamsAdd,
                selectedTeamsRemove = selectedTeamsRemove,
                onTeamToggleAdd = { team, isSelected ->
                    if (isSelected) {
                        selectedTeamsAdd = selectedTeamsAdd + team
                    } else {
                        selectedTeamsAdd = selectedTeamsAdd - team
                    }
                },
                onTeamToggleRemove = { team, isSelected ->
                    if (isSelected) {
                        selectedTeamsRemove = selectedTeamsRemove + team
                    } else {
                        selectedTeamsRemove = selectedTeamsRemove - team
                    }
                },
                onDismiss = {
                    showAddTeamPopup = false
                }
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
    tourneyId: String,
    context: Context,
    teams: List<TeamModel>,
    searchQuery: String,
    mode: String,
    teamNames: MutableList<String>,
    navController: NavHostController,
    onSearchQueryChange: (String) -> Unit,
    selectedTeamsAdd: List<TeamModel>,
    selectedTeamsRemove: List<TeamModel>,
    onTeamToggleAdd: (TeamModel, Boolean) -> Unit,
    onTeamToggleRemove: (TeamModel, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val availableTeams = when (mode) {
        "add" -> teams.filterNot { team -> selectedTeamsRemove.contains(team) }
        "remove" -> teams
        else -> emptyList()
    }

    val filteredTeams = availableTeams.filter { team ->
        team.name!!.contains(searchQuery, ignoreCase = true)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (mode == "add") "Selecione o Time para Adicionar" else "Selecione o Time para Remover") },
        text = {
            Column {
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    label = { Text("Pesquisar") }
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn {
                    items(filteredTeams) { team ->
                        val isChecked = when (mode) {
                            "add" -> selectedTeamsAdd.contains(team)
                            "remove" -> selectedTeamsRemove.contains(team)
                            else -> false
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { isSelected ->
                                    when (mode) {
                                        "add" -> onTeamToggleAdd(team, isSelected)
                                        "remove" -> onTeamToggleRemove(team, isSelected)
                                    }
                                }
                            )
                            Text(team.name!!)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (mode == "add") {
                        selectedTeamsAdd.forEach { team ->
                            team._id?.let {
                                addTeamToTourney(it, tourneyId) { result ->
                                    if (result != null) {
                                        teamNames.add(result.name)
                                    } else {
                                        Toast.makeText(context, "Falha ao adicionar os times ao torneio.", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        }
                    } else if (mode == "remove") {
                        selectedTeamsRemove.forEach { team ->
                            team._id?.let {
                                removeTeamFromTourney(it, tourneyId) { result ->

                                }
                            }
                        }
                    }
                    onDismiss()
                    navController.navigate("exploreTourneys")
                }
            ) {
                Text("Concluído")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
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

fun addTeamToTourney(teamId: String, id: String, callback: (TourneyModel?) -> Unit) {
    val body = addTeamToTourneyBody(
        teamId
    )
    val call = ApiClient.apiService.addTeamToTourney(id, body)
    apiCall(call, callback)
}

fun removeTeamFromTourney(teamId: String, id: String, callback: (Void?) -> Unit) {
    val body = addTeamToTourneyBody(
        teamId
    )
    val call = ApiClient.apiService.removeTeamFromTourney(id, body)
    apiCall(call, callback)
}

fun shareTourney(context: Context, tourney: TourneyModel) {
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "Confira o torneio: ${tourney.name}\nDescrição: ${tourney.description}\nPremiação: ${tourney.prize}")
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(shareIntent, "Compartilhar Torneio"))
}