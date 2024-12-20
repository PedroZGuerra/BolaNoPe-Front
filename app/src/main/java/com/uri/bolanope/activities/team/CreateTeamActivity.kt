package com.uri.bolanope.activities.team

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.uri.bolanope.activities.field.base64ToBitmap
import com.uri.bolanope.activities.field.getFileName
import com.uri.bolanope.components.TopBar
import com.uri.bolanope.model.NotificationModel
import com.uri.bolanope.model.TeamModel
import com.uri.bolanope.model.UserModel
import com.uri.bolanope.services.ApiClient
import com.uri.bolanope.services.apiCall
import com.uri.bolanope.utils.SharedPreferencesManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.InputStream

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
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val base64String by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        getAllUsers { result ->
            if (result != null) {
                users.value = result
                val currentUser = users.value?.find { it._id == userId }
                if (currentUser != null) {
                    selectedMembers = selectedMembers.toMutableList().apply {
                        this[0] = currentUser
                    }
                }
            } else {
                Toast.makeText(context, "Falha ao carregar os usuarios.", Toast.LENGTH_LONG).show()
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
        }
    }

    Scaffold(
        topBar = { TopBar("Criar Time") },
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
            IconButton(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.Transparent, shape = CircleShape)
                    .border(1.dp, Color.Black, CircleShape)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                ) {
                    if (selectedImageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(selectedImageUri),
                            contentDescription = "Imagem Selecionada",
                            modifier = Modifier
                                .fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else if (base64String.isNotBlank()) {
                        val bitmap = base64ToBitmap(base64String)
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Imagem Carregada",
                                modifier = Modifier
                                    .fillMaxSize(),
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
                    onClick = {
                        navController.navigate("exploreTeams")
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.Red
                    )
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
                            members_id = selectedMembers.mapNotNull { it?._id },
                            image = null
                        )
                        createTeam(context, teamModel, selectedImageUri, userToken!!) { response ->
                            if (response != null) {
                                Toast.makeText(context, "Time Criado com sucesso", Toast.LENGTH_LONG).show()
                                navController.navigate("home")
                            } else {
                                Toast.makeText(context, "Falha ao criar time", Toast.LENGTH_LONG).show()
                            }
                        }

                        teamModel.members_id?.forEach { member ->
                            if(member != userId){
                                createNotification(member, teamName) { result ->
                                    if (result != null) {
                                    } else {
                                        Toast.makeText(context, "Falha ao criar Notificação", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        }
                    }
                ) {
                    Text("Criar Time")
                }
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
        title = { Text("Selecione os Usuários") },
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
                        val isChecked = selectedMembers.contains(user)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { isSelected ->
                                    onMemberToggle(user, isSelected)
                                }
                            )
                            Text(user.email)
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

fun createTeam(
    context: Context,
    teamModel: TeamModel,
    imageUri: Uri?,
    userToken: String,
    callback: (TeamModel?) -> Unit
) {
    val contentResolver: ContentResolver = context.contentResolver

    val imagePart = imageUri?.let {
        val inputStream: InputStream? = contentResolver.openInputStream(it)
        val file = File(context.cacheDir, getFileName(contentResolver, it) ?: "image_temp.jpg")

        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        MultipartBody.Part.createFormData("file_url", file.name, requestFile)
    }
    val descriptionPart = teamModel.description?.toRequestBody("text/plain".toMediaTypeOrNull())
    val leaderIdPart = teamModel.leader_id?.toRequestBody("text/plain".toMediaTypeOrNull())
    val membersIdParts = teamModel.members_id?.map { memberId ->
        MultipartBody.Part.createFormData("members_id", memberId)
    }
    val namePart = teamModel.name?.toRequestBody("text/plain".toMediaTypeOrNull())
    val call = ApiClient.apiService.createTeam(
        descriptionPart,
        leaderIdPart,
        membersIdParts,
        namePart,
        imagePart,
        "Bearer $userToken")
    apiCall(call, callback)
}

fun getAllUsers(callback: (List<UserModel>?) -> Unit) {
    val call = ApiClient.apiService.getAllUsers()
    apiCall(call, callback)
}

fun createNotification(userId: String, teamName: String, callback: (NotificationModel?) -> Unit){
    val notificationBody = NotificationModel(
        _id = null,
        userId = userId,
        read = false,
        title = "Adicionado ao time",
        message = "Você foi adicionado ao time ${teamName}"
    )
    val call = ApiClient.apiService.sendNotification(notificationBody)
    apiCall(call, callback)
}