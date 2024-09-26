package com.uri.bolanope

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.uri.bolanope.model.FieldModel
import com.uri.bolanope.model.ReserveModel
import com.uri.bolanope.model.TeamModel
import com.uri.bolanope.model.UserModel
import com.uri.bolanope.services.ApiClient
import com.uri.bolanope.services.apiCall
import com.uri.bolanope.utils.SharedPreferencesManager

@Composable
fun CreateTeam(navController: NavHostController) {
    val context = LocalContext.current
    val userId = SharedPreferencesManager.getUserId(context)
    val userToken = SharedPreferencesManager.getToken(context)
    var teamName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val users = remember { mutableStateOf<List<UserModel>?>(null) }
    var selectedMembers by remember { mutableStateOf<List<UserModel?>>(List(5) { null }) }
    var showUserPopup by remember { mutableStateOf<Pair<Boolean, Int>>(Pair(false, -1)) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        getAllUsers { result ->
            if (result != null) {
                users.value = result
                // Set current user as one of the selected members by default
                val currentUser = users.value?.find { it._id == userId }
                if (currentUser != null) {
                    selectedMembers = selectedMembers.toMutableList().apply {
                        this[0] = currentUser // Assign current user to the first slot
                    }
                }
            } else {
                Toast.makeText(context, "Falha ao carregar os usuarios.", Toast.LENGTH_LONG).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally // Centering horizontally
    ) {
        TextField(
            value = teamName,
            onValueChange = { teamName = it },
            label = { Text("Team Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text("Membros do time:", modifier = Modifier.align(Alignment.Start))

        Spacer(modifier = Modifier.height(16.dp))

        for (i in 0 until 5) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = { showUserPopup = Pair(true, i) }) {
                    if (selectedMembers[i]?.name != null) {
                        Text(selectedMembers[i]?.name!!)
                    } else {
                        Icon(Icons.Filled.Add, contentDescription = "Adicionar Usuário", tint = Color.White)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (showUserPopup.first) {
            UserSelectionPopup(
                users = users.value ?: emptyList(),
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                selectedMembers = selectedMembers.filterNotNull(),
                onMemberToggle = { user, isSelected ->
                    // Prevent unselecting the current user
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
                onClick = {
                    navController.navigate("exploreTeams")
                }
            ) {
                Text("Cancelar")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                    val teamModel = TeamModel(
                        _id = null,
                        leader_id = userId.toString(),
                        name = teamName,
                        description = description,
                        members_id = selectedMembers.mapNotNull { it?._id }
                    )
                    createTeam(teamModel, userToken!!) { response ->
                        if (response != null) {
                            Log.d("log fodinhaaaaa", "Team created: ${response}")
                            Toast.makeText(context, "Time Criado com sucesso", Toast.LENGTH_LONG).show()
                            navController.navigate("home")
                        } else {
                            Log.d("log fodinhaaaaa", "Failed to create team: ${response}")
                            Toast.makeText(context, "Falha ao criar reserva", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            ) {
                Text("Criar Time")
            }
        }
    }
}

@Composable
fun UserSelectionPopup(
    users: List<UserModel>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedMembers: List<UserModel>,
    onMemberToggle: (UserModel, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val filteredUsers = users.filter { user ->
        user.name.contains(searchQuery, ignoreCase = true)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Users") },
        text = {
            Column {
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    label = { Text("Search") }
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn {
                    items(filteredUsers) { user ->
                        val isChecked = selectedMembers.contains(user)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { isSelected ->
                                    onMemberToggle(user, isSelected)
                                }
                            )
                            Text(user.name)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Done")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

fun createTeam(teamModel: TeamModel, userToken: String, callback: (TeamModel?) -> Unit) {
    Log.d("log fodinhaaaaa", "${teamModel}, $userToken")
    val call = ApiClient.apiService.createTeam(teamModel, "Bearer $userToken")
    apiCall(call, callback)
}

fun getAllUsers(callback: (List<UserModel>?) -> Unit) {
    val call = ApiClient.apiService.getAllUsers()
    apiCall(call, callback)
}
