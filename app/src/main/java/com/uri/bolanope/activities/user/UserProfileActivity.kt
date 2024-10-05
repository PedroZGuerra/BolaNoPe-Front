package com.uri.bolanope.activities.user

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.uri.bolanope.utils.MaskVisualTransformation
import com.uri.bolanope.components.TopBar
import com.uri.bolanope.model.CreateUserResponseModel
import com.uri.bolanope.model.UserModel
import com.uri.bolanope.services.ApiClient
import com.uri.bolanope.services.apiCall
import com.uri.bolanope.utils.SharedPreferencesManager
import com.uri.bolanope.utils.decodeJWT

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
                }
            }
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
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Email") },
                    enabled = activityMode != "UPDATE",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Senha") },
                    visualTransformation = PasswordVisualTransformation()
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
                            uf = null
                        )

                        onClickButtonSignUp(userModel) { response ->
                            if (response != null) {
                                val (userIdCreate, role) = decodeJWT(response.token)

                                if (userIdCreate != null && role != null) {
                                    SharedPreferencesManager.saveUserId(context, userIdCreate)
                                    SharedPreferencesManager.saveUserRole(context, role)
                                    SharedPreferencesManager.saveToken(context, response.token)

                                    if (role == "admin") {
                                        navController.navigate("homeAdmin")
                                    } else {
                                        navController.navigate("home")
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
                                uf = null
                            )

                            if (userId != null) {
                                onClickButtonUpdateUser(userId, userModel) { }
                            }
                        }, modifier = Modifier.width(150.dp)
                    ) {
                        Text("Finalizar edição")
                    }

                    Spacer(modifier = Modifier.height(64.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(32.dp)
                    ) {
                        Button(
                            onClick = {
                                showDialog = true
                            },
                            modifier = Modifier.width(150.dp)
                        ) {
                            Text("Apagar conta")
                        }

                        Button(
                            onClick = {
                                navController.navigate("welcome")
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


fun onClickButtonSignUp(userModel: UserModel, callback: (CreateUserResponseModel?) -> Unit) {
    val call = ApiClient.apiService.postUser(userModel)
    apiCall(call, callback)
}


fun getUserById(id: String, callback: (UserModel?) -> Unit) {
    val call = ApiClient.apiService.getUserById(id)
    apiCall(call, callback)
}

fun onClickButtonUpdateUser(id: String, userModel: UserModel, callback: (UserModel?) -> Unit) {
    val call = ApiClient.apiService.putUserById(id, userModel)
    apiCall(call, callback)
}

fun onClickButtonDeleteUser(id: String, callback: (UserModel?) -> Unit){
    val call = ApiClient.apiService.deleteUserById(id)
    apiCall(call, callback)
}


fun applyMask(input: String): String {
    val mask = "###.###.###-##"
    val cleanInput = input.replace("[^0-9]".toRegex(), "")
    val maskedInput = StringBuilder()
    var inputIndex = 0
    for (i in mask.indices) {
        if (inputIndex >= cleanInput.length) break
        if (mask[i] == '#') {
            maskedInput.append(cleanInput[inputIndex])
            inputIndex++
        } else {
            maskedInput.append(mask[i])
        }
    }
    return maskedInput.toString()
}