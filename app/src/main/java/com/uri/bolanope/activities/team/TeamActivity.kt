package com.uri.bolanope.activities.team

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.uri.bolanope.activities.field.base64ToBitmap
import com.uri.bolanope.activities.user.getUserById
import com.uri.bolanope.components.CommentCard
import com.uri.bolanope.components.CreateComment
import com.uri.bolanope.components.TopBar
import com.uri.bolanope.model.CommentModel
import com.uri.bolanope.model.RequestBody
import com.uri.bolanope.model.RequestModel
import com.uri.bolanope.model.TeamModel
import com.uri.bolanope.model.UserModel
import com.uri.bolanope.model.getNumberOfTeamRequestsModel
import com.uri.bolanope.services.ApiClient
import com.uri.bolanope.services.apiCall
import com.uri.bolanope.utils.SharedPreferencesManager
import java.time.LocalTime

@Composable
fun Team(navController: NavHostController, teamId: String?) {
    val context = LocalContext.current
    val team = remember { mutableStateOf<TeamModel?>(null) }
    val members = remember { mutableStateListOf<UserModel>() }
    val user_id = SharedPreferencesManager.getUserId(context)
    val userRole = SharedPreferencesManager.getUserRole(context)
    var leader_id by remember { mutableStateOf<String>("") }
    val user_token = SharedPreferencesManager.getToken(context)
    val showDeleteDialog = remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val numberOfRequests = remember { mutableStateOf(0) }
    val commentArray = remember { mutableStateListOf<CommentModel>() }
    var base64String by remember { mutableStateOf("") }

    LaunchedEffect(teamId) {
        if (teamId != null) {
            getTeamById(teamId) { result ->
                if (result != null) {
                    team.value = result
                    base64String = result.image.toString()
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
            getCommentsByTeamId(teamId, user_token!!) { result ->
                if (result != null) {
                    commentArray.clear()
                    commentArray.addAll(result)
                    Log.d("tag", "${commentArray}, $result")
                }else {
                    Log.d("tag", "no comments here")

                }
            }
        }
    }

    Scaffold(
        topBar = { team.value?.let { TopBar(it.name!!) } },
        content = { paddingValues ->
            if (team.value != null) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(100.dp)
                            ) {
                                if (base64String.isNotBlank()) {
                                    val bitmap = base64ToBitmap(base64String)
                                    if (bitmap != null) {
                                        Image(
                                            bitmap = bitmap.asImageBitmap(),
                                            contentDescription = "Imagem Carregada",
                                            modifier = Modifier
                                                .size(100.dp),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Icon(
                                            Icons.Default.Groups,
                                            contentDescription = "Selecionar Imagem",
                                            tint = Color.Black,
                                            modifier = Modifier.size(50.dp)
                                        )
                                    }
                                }
                            }
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
                    }

                    item {
                        Text(text = "Participantes:", style = MaterialTheme.typography.titleMedium)
                    }

                    items(members.size) { index ->
                        val member = members[index]
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
                                if (imageBitmap != null) {
                                    Image(
                                        bitmap = imageBitmap!!.asImageBitmap(),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(64.dp)
                                            .padding(end = 8.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Filled.Person,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(64.dp)
                                            .padding(end = 8.dp)
                                    )
                                }
                                Column {
                                    Text(text = member.name)
                                    Text(text = if (isLeader) "Líder" else "Membro")
                                }
                            }
                        }
                    }

                    if(userRole != "admin") {
                        item {
                            if (team.value?.members_id?.contains(user_id) == false) {
                                Button(
                                    onClick = {
                                        createTeamRequest(teamId!!, user_token!!) { result ->
                                            if (result != null) {
                                                Toast.makeText(context, "Pedido enviado com sucesso!", Toast.LENGTH_LONG).show()
                                            } else {
                                                Toast.makeText(context, "Erro ao enviar pedido", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    },
                                ) {
                                    Text("Quero participar")
                                }
                            }
                        }

                        if (user_id == team.value?.leader_id) {
                            item {
                                Column {
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
                                    ) {
                                        getNumberOfTeamRequests(teamId!!, user_token!!) { number ->
                                            if (number != null) {
                                                Log.d("TAG", "$number")
                                                numberOfRequests.value = number.pendingRequests!!
                                            }
                                        }
                                        Text("Visualizar pedidos de entrada (${numberOfRequests.value})")
                                    }
                                }
                            }
                        }

                        item {
                            CreateComment(teamId!!,null, user_token!!, user_id!!, false) { newComment ->
                                commentArray.add(
                                    CommentModel(
                                        _id = "null",
                                        comment = newComment,
                                        team_id = teamId,
                                        field_id = null,
                                        user_id = user_id,
                                        created_at = "agora"
                                    )
                                )
                            }
                        }
                    }
                    if (commentArray.isNotEmpty()) {
                        item {
                            Text(text = "Comentários:", style = MaterialTheme.typography.titleMedium)
                        }
                        items(commentArray.reversed()) { comment ->
                            CommentCard(
                                userId = comment.user_id,
                                commentText = comment.comment,
                                commentId = comment._id!!,
                                time = comment.created_at,
                                rating = null,
                                onDeleteComment = {
                                    deleteComment(comment._id!!, user_token!!) { result ->
                                        Toast.makeText(
                                            context,
                                            "Comentário deletado com sucesso.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                    commentArray.remove(comment)
                                }
                            )
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

fun getCommentsByTeamId(team_id: String, token: String, callback: (List<CommentModel>?) -> Unit) {
    val call = ApiClient.apiService.getComments(team_id, "Bearer $token")
    apiCall(call, callback)
}

fun getNumberOfTeamRequests(team_id: String, token: String, callback: (getNumberOfTeamRequestsModel?) -> Unit) {
    val call = ApiClient.apiService.getNumberOfTeamRequests(team_id, "Bearer $token")
    apiCall(call, callback)
}

fun deleteComment(id: String, authHeader: String, callback: (Void?) -> Unit) {
    val call = ApiClient.apiService.deleteComment(id, "Bearer $authHeader")
    apiCall(call, callback)
}
