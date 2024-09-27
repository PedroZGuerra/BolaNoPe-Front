package com.uri.bolanope

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.uri.bolanope.model.TeamModel
import com.uri.bolanope.services.ApiClient
import com.uri.bolanope.services.apiCall
import com.uri.bolanope.utils.SharedPreferencesManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Team(navController: NavHostController, teamId: String?) {
    val context = LocalContext.current
    val team = remember { mutableStateOf<TeamModel?>(null) }
    val membersNames = remember { mutableStateListOf<String>() }
    val user_id = SharedPreferencesManager.getUserId(context)
    var leader_name by remember { mutableStateOf("") }

    LaunchedEffect(teamId) {
        if (teamId != null) {
            getTeamById(teamId) { result ->
                if (result != null) {
                    team.value = result
                    result.members_id.forEach { memberId ->
                        getUserById(memberId) { user ->
                            user?.let {
                                membersNames.add(it.name)
                            }
                        }
                    }
                    getUserById(team.value?.leader_id!!) { user ->
                        user?.let {
                            leader_name = it.name
                        }
                    }
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
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = team.value?.description ?: "",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Líder: $leader_name",
                                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Members:", style = MaterialTheme.typography.titleMedium)

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        items(membersNames.size) { index ->
                            val memberName = membersNames[index]
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(60.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Text(
                                        text = memberName,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    if (user_id == team.value?.leader_id) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(onClick = { /* Handle edit */ }) {
                                Text("Editar Time")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { /* Handle delete */ },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                            ) {
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
