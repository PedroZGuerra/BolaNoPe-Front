package com.uri.bolanope.activities.user

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.uri.bolanope.activities.field.base64ToBitmap
import com.uri.bolanope.activities.field.getFileName
import com.uri.bolanope.utils.MaskVisualTransformation
import com.uri.bolanope.components.TopBar
import com.uri.bolanope.model.CreateUserResponseModel
import com.uri.bolanope.model.EditUserModel
import com.uri.bolanope.model.UserModel
import com.uri.bolanope.services.ApiClient
import com.uri.bolanope.services.apiCall
import com.uri.bolanope.utils.SharedPreferencesManager
import com.uri.bolanope.utils.decodeJWT
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.InputStream

@Composable
fun UserProfile(navController: NavHostController, userId: String?) {
    val activityMode = if (userId.isNullOrEmpty()) "CREATE" else "UPDATE"
    val topBarTitle = if (userId.isNullOrEmpty()) "Cadastro" else "Editar Perfil"

    val context = LocalContext.current

    val userId = SharedPreferencesManager.getUserId(context)

    var showDialog by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var cpf by remember { mutableStateOf("") }
    var birth by remember { mutableStateOf("") }
    var cep by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var base64String by remember { mutableStateOf("") }

    val cpf_mask = "###.###.###-##"
    val cpf_len = 11

    val date_mask = "##/##/####"
    val date_len = 8

    val cep_mask = "#####-###"
    val cep_len = 8

    LaunchedEffect(userId) {
        if (activityMode == "UPDATE" && userId != null) {
            getUserById(userId) { user ->
                user?.let {
                    email = it.email
                    password = it.password
                    name = it.name
                    cpf = it.cpf
                    birth = it.birth
                    cep = it.cep
                    base64String = it.image.toString()
                }
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
        topBar = {
            TopBar(topBarTitle)
        },
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding(),
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
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
                        } else if (base64String != "") {
                            val bitmap = base64ToBitmap(base64String)
                            bitmap?.let {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = "Imagem Carregada",
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        } else {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Selecionar Imagem",
                                tint = Color.Black,
                                modifier = Modifier.size(50.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Email") },
                    enabled = activityMode != "UPDATE",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nome") }
                )

                OutlinedTextField(
                    value = cpf,
                    onValueChange = { it ->
                        if (it.length <= cpf_len) {
                            cpf = it.filter { it.isDigit() }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("CPF") },
                    visualTransformation = MaskVisualTransformation(cpf_mask),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = birth,
                    onValueChange = { it ->
                        if (it.length <= date_len) {
                            birth = it.filter { it.isDigit() }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Data de nascimento") },
                    visualTransformation = MaskVisualTransformation(date_mask),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = cep,
                    onValueChange = { it ->
                        if (it.length <= cep_len) {
                            cep = it.filter { it.isDigit() }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("CEP") },
                    visualTransformation = MaskVisualTransformation(cep_mask),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                if (activityMode == "CREATE") {
                    Button(onClick = {
                        val userModel = UserModel(
                            _id = null,
                            name = name,
                            cpf = cpf,
                            birth = birth,
                            email = email,
                            password = password,
                            cep = cep,
                            role = null,
                            patio = null,
                            complement = null,
                            neighborhood = null,
                            locality = null,
                            uf = null,
                            image = null
                        )

                        onClickButtonSignUp(context,userModel, selectedImageUri) { response ->
                            if (response != null) {
                                val (userIdCreate, role) = decodeJWT(response.token)

                                if (userIdCreate != null && role != null) {
                                    SharedPreferencesManager.saveUserId(context, userIdCreate)
                                    SharedPreferencesManager.saveUserRole(context, role)
                                    SharedPreferencesManager.saveToken(context, response.token)

                                    navController.navigate("home") {
                                        popUpTo(navController.currentDestination?.id ?: 0) { inclusive = true }
                                    }
                                }

                            } else {
                                Toast.makeText(context, "Falha ao criar conta", Toast.LENGTH_LONG).show()
                            }
                        }
                    }) {
                        Text("Cadastrar")
                    }
                }

                if (activityMode == "UPDATE") {

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            val userModel = EditUserModel(
                                name = name,
                                birth = birth,
                                email = email,
                                cep = cep,
                                image = null
                            )

                            if (userId != null) {
                                onClickButtonUpdateUser(userId, context, userModel, selectedImageUri) { }
                            }
                        }, modifier = Modifier.width(150.dp)
                    ) {
                        Text("Finalizar edição")
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    Button(
                        onClick = {
                            navController.navigate("changePassword/$userId")
                        },
                        modifier = Modifier.width(150.dp)
                    ) {
                        Text("Mudar Senha")
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(32.dp)
                    ) {
                        Button(
                            onClick = {
                                showDialog = true
                            },
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = Color.Red,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.width(150.dp)
                        ) {
                            Text("Apagar conta")
                        }

                        Button(
                            onClick = {
                                navController.navigate("welcome") {
                                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                }
                                SharedPreferencesManager.clearUserId(context)
                                SharedPreferencesManager.clearToken(context)
                                SharedPreferencesManager.clearUserRole(context)
                            },
                            modifier = Modifier.width(150.dp)
                        ) {
                            Text("Sair")
                        }
                    }
                }
            }
        }
    )


    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmação de exclusão") },
            text = { Text("Tem certeza de que deseja excluir sua conta?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        if (userId != null) {
                            onClickButtonDeleteUser(userId) {
                                SharedPreferencesManager.clearUserId(context)
                                SharedPreferencesManager.clearToken(context)
                                SharedPreferencesManager.clearUserRole(context)

                                navController.navigate("welcome")
                            }
                        }
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}


fun onClickButtonSignUp(
    context: Context,
    userModel: UserModel,
    imageUri: Uri?,
    callback: (CreateUserResponseModel?) -> Unit
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

    val emailPart = userModel.email.toRequestBody("text/plain".toMediaTypeOrNull())
    val namePart = userModel.name.toRequestBody("text/plain".toMediaTypeOrNull())
    val passwordPart = userModel.password.toRequestBody("text/plain".toMediaTypeOrNull())
    val cpfPart = userModel.cpf.toRequestBody("text/plain".toMediaTypeOrNull())
    val birthPart = userModel.birth.toRequestBody("text/plain".toMediaTypeOrNull())
    val cepPart = userModel.cep.toRequestBody("text/plain".toMediaTypeOrNull())


    val call = ApiClient.apiService.postUser(
        emailPart,
        namePart,
        passwordPart,
        cpfPart,
        birthPart,
        cepPart,
        imagePart
    )
    apiCall(call, callback)
}


fun getUserById(id: String, callback: (UserModel?) -> Unit) {
    val call = ApiClient.apiService.getUserById(id)
    apiCall(call, callback)
}

fun onClickButtonUpdateUser(
    id: String,
    context: Context,
    userModel: EditUserModel,
    imageUri: Uri?,
    callback: (UserModel?) -> Unit
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

    val emailPart = userModel.email.toRequestBody("text/plain".toMediaTypeOrNull())
    val namePart = userModel.name.toRequestBody("text/plain".toMediaTypeOrNull())
    val birthPart = userModel.birth.toRequestBody("text/plain".toMediaTypeOrNull())
    val cepPart = userModel.cep.toRequestBody("text/plain".toMediaTypeOrNull())

    val call = ApiClient.apiService.putUserById(
        id = id,
        email = emailPart,
        name = namePart,
        birth = birthPart,
        cep = cepPart,
        file_url = imagePart,
    )
    apiCall(call, callback)
}

fun onClickButtonDeleteUser(id: String, callback: (UserModel?) -> Unit){
    val call = ApiClient.apiService.deleteUserById(id)
    apiCall(call, callback)
}