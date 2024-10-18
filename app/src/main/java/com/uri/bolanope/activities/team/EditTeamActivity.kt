package com.uri.bolanope.activities.team

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import com.uri.bolanope.activities.user.getUserById
import com.uri.bolanope.model.TeamModel
import com.uri.bolanope.model.UserModel
import com.uri.bolanope.services.ApiClient
import com.uri.bolanope.services.apiCall
import com.uri.bolanope.utils.SharedPreferencesManager

@Composable
fun EditTeam(navController: NavHostController, teamId: String?) {
    val context = LocalContext.current
    val userId = SharedPreferencesManager.getUserId(context)
    val userToken = SharedPreferencesManager.getToken(context)
    val team = remember { mutableStateOf<TeamModel?>(null) }
    var teamName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val users = remember { mutableStateOf<List<UserModel>?>(null) }
    var selectedMembers by remember { mutableStateOf<List<UserModel?>>(List(5) { null }) }
    var showUserPopup by remember { mutableStateOf<Pair<Boolean, Int>>(Pair(false, -1)) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(teamId) {
        if (teamId != null) {
            getTeamById(teamId) { result ->
                team.value = result
                teamName = result?.name ?: ""
                description = result?.description ?: ""
                result?.members_id?.forEachIndexed { index, memberId ->
                    getUserById(memberId) { user ->
                        if (user != null && index < selectedMembers.size) {
                            selectedMembers = selectedMembers.toMutableList().apply {
                                this[index] = user
                            }
                        }
                    }
                }
            }

            getAllUsers { result ->
                if (result != null) {
                    users.value = result
                } else {
                    Toast.makeText(context, "Falha ao carregar os usuários.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    Scaffold(
        topBar = { TopBar("Editar Time") },
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
                value = teamName,
                onValueChange = { teamName = it },
                label = { Text("Nome do Time") },
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

            Text("Membros do time:", modifier = Modifier.align(Alignment.Start))

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                items(5) { i ->
                    Button(onClick = { showUserPopup = Pair(true, i) }) {
                        if (selectedMembers[i]?.name != null) {
                            Text(selectedMembers[i]?.name!!)
                        } else {
                            Icon(Icons.Filled.Add, contentDescription = "Adicionar Usuário", tint = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }

            if (showUserPopup.first) {
                UserSelectionPopup(
                    users = users.value ?: emptyList(),
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    selectedMembers = selectedMembers.filterNotNull(),
                    onMemberToggle = { user, isSelected ->
                        if (user._id != userId) {
                            selectedMembers = selectedMembers.toMutableList().apply {
                                this[showUserPopup.second] = if (isSelected) user else null
                            }
                        }
                    },
                    onDismiss = { showUserPopup = Pair(false, -1) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                TextButton(
                    onClick = { navController.navigate("exploreTeams") },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("Cancelar")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        val updatedTeam = team.value?.copy(
                            _id = null,
                            name = teamName,
                            description = description,
                            members_id = selectedMembers.mapNotNull { it?._id }
                        )
                        updatedTeam?.let {
                            updateTeam(teamId!!, it, userToken!!) { response ->
                                if (response != null) {
                                    Toast.makeText(context, "Time atualizado com sucesso", Toast.LENGTH_LONG).show()
                                    navController.popBackStack()
                                } else {
                                    Toast.makeText(context, "Falha ao atualizar o time", Toast.LENGTH_LONG).show()
                                }
                            }

                            updatedTeam.members_id?.forEach { member ->
                                createNotification(member, teamName) { result ->
                                    if (result != null) {
                                        Toast.makeText(context, "notificacao Criado com sucesso", Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(context, "Falha ao criar notificacao", Toast.LENGTH_LONG).show()
                                    }

                                }
                            }
                        }
                    }
                ) {
                    Text("Salvar Alterações")
                }
            }
        }
    }
}

fun updateTeam(id: String, teamModel: TeamModel, userToken: String, callback: (TeamModel?) -> Unit) {
    val call = ApiClient.apiService.updateTeam(id, teamModel, "Bearer $userToken")
    apiCall(call, callback)
}