package com.uri.bolanope.activities.team

import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.uri.bolanope.activities.field.base64ToBitmap
import com.uri.bolanope.components.TopBar
import com.uri.bolanope.activities.user.getUserById
import com.uri.bolanope.model.RequestBody
import com.uri.bolanope.model.RequestModel
import com.uri.bolanope.model.TeamModel
import com.uri.bolanope.model.UserModel
import com.uri.bolanope.services.ApiClient
import com.uri.bolanope.services.apiCall
import com.uri.bolanope.utils.SharedPreferencesManager

@Composable
fun Team(navController: NavHostController, teamId: String?) {
    val context = LocalContext.current
    val team = remember { mutableStateOf<TeamModel?>(null) }
    val members = remember { mutableStateListOf<UserModel>() }
    val user_id = SharedPreferencesManager.getUserId(context)
    var leader_id by remember { mutableStateOf<String>("") }
    val user_token = SharedPreferencesManager.getToken(context)
    val showDeleteDialog = remember { mutableStateOf(false) }

    LaunchedEffect(teamId) {
        if (teamId != null) {
            getTeamById(teamId) { result ->
                if (result != null) {
                    team.value = result
                    leader_id = result.leader_id ?: ""

                    result.members_id!!.forEach { memberId ->
                        getUserById(memberId) { user ->
                            user?.let {
                                members.add(it)
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "Falha ao carregar o time.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    Scaffold(
        topBar = { team.value?.let { TopBar(it.name!!) } },
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
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Descrição:",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = team.value?.description ?: "",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Participantes:", style = MaterialTheme.typography.titleMedium)

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        val sortedMembers = members.sortedBy { if (it._id == leader_id) 0 else 1 }

                        items(sortedMembers.size) { index ->
                            val member = sortedMembers[index]
                            var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
                            imageBitmap = member.image?.let { base64ToBitmap(it) }
                            val isLeader = member._id?.equals(leader_id) == true

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    imageBitmap?.let {
                                        Image(
                                            bitmap = it.asImageBitmap(),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(64.dp)
                                                .padding(end = 8.dp),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                    Column {
                                        Text(text = member.name)
                                        Text(text = if (isLeader) "Líder" else "Membro")
                                    }
                                }
                            }
                        }
                    }

                    if (team.value?.members_id?.contains(user_id) == false) {
                        Button(
                            onClick = {
                                Log.d("TAG", "Team: entrar no time mt fera")
                                createTeamRequest(teamId!!, user_token!!){ result ->
                                    Log.d("TAG", "$teamId, $user_token")
                                    if (result != null) {
                                        Toast.makeText(context, "Pedido enviado com sucesso!", Toast.LENGTH_LONG).show()
                                    }else {
                                        Toast.makeText(context, "Erro ao enviar pedido", Toast.LENGTH_LONG).show()
                                    }
                                }
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Quero participar")
                        }
                    }


                    if (user_id == team.value?.leader_id) {
                        Column{
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Button(onClick = {
                                    navController.navigate("editTeam/$teamId")
                                }) {
                                    Text("Editar Time")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        showDeleteDialog.value = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                                ) {
                                    Text("Deletar Time")
                                }
                            }
                            Button(
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                onClick = {
                                    navController.navigate("teamRequests/${team.value!!._id}")
                                }
                            ){
                                Text("Visualizar pedidos de entrada")
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

    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = { Text("Confirmar Exclusão") },
            text = { Text("Você tem certeza que deseja deletar este time?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog.value = false
                        team.value?.let {
                            val authHeader = SharedPreferencesManager.getToken(context)
                            deleteTeam(it._id!!, authHeader!!) { result ->
                                Toast.makeText(context, "Time deletado com sucesso.", Toast.LENGTH_LONG).show()
                                navController.popBackStack()
                            }
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

fun getTeamById(team_id: String, callback: (TeamModel?) -> Unit) {
    val call = ApiClient.apiService.getTeamById(team_id)
    apiCall(call, callback)
}

fun deleteTeam(id: String, authHeader: String, callback: (Void?) -> Unit) {
    val call = ApiClient.apiService.deleteTeam(id, "Bearer $authHeader")
    apiCall(call, callback)
}

fun createTeamRequest(teamId: String, authHeader: String, callback: (RequestModel?) -> Unit){
    val requestBody = RequestBody(
        teamId
    )

    val call = ApiClient.apiService.createTeamRequest(requestBody, "Bearer $authHeader")
    apiCall(call, callback)
}