package com.uri.bolanope

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.uri.bolanope.model.UserModel
import com.uri.bolanope.ui.theme.BolaNoPeTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UserProfileActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        val activityMode = intent.getStringExtra("ACTIVITY_MODE")
        val userId = intent.getStringExtra("USER_ID")

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BolaNoPeTheme {
                UserProfile(activityMode, userId)
            }
        }
    }
}

@Composable
fun UserProfile(activityMode: String?, userId: String?){

    val context = LocalContext.current

    var showDialog by remember{
        mutableStateOf(false)
    }

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var name by remember {
        mutableStateOf("")
    }

    var cpf by remember {
        mutableStateOf("")
    }

    var birth by remember {
        mutableStateOf("")
    }

    var cep by remember {
        mutableStateOf("")
    }

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

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Email") },
            enabled = activityMode != "UPDATE"
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
            onValueChange = { cpf = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("CPF") }
        )

        OutlinedTextField(
            value = birth,
            onValueChange = { birth = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Data de nascimento") }
        )

        OutlinedTextField(
            value = cep,
            onValueChange = { input ->
                if (input.length <= 8) {
                    cep = input
                }
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("CEP") }
        )

        if("CREATE" == activityMode){
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

                onClickButtonSignUp(userModel) }) {
                Text("Cadastrar")
            }
        }

        if("UPDATE" == activityMode){
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

                if (userId != null) {
                    onClickButtonUpdateUser(userId ,userModel){

                    }
                }
            }) {
                Text("Editar")
            }
        }

        if("UPDATE" == activityMode){
            Button(onClick = {
                showDialog = true
            }) {
                Text("Excluir")
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Confirmação de exclusão") },
                    text = { Text("Tem certeza de que deseja excluir este usuário?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDialog = false
                                if (userId != null) {
                                    onClickButtonDeleteUser(userId) {
                                        val intent = Intent(context, MainActivity::class.java)
                                        context.startActivity(intent)
                                    }
                                }
                            }
                        ) {
                            Text("Confirmar")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showDialog = false
                            }
                        ) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }

}

fun onClickButtonSignUp(userModel: UserModel) {
    val call = ApiClient.apiService.postUser(userModel)

    call.enqueue(object : Callback<UserModel> {
        override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
            if (response.isSuccessful) {
                val post = response.body()
            } else {
                val errorBody = response.errorBody()?.string()
                Log.d("Erro na requisição", "Código: ${response.code()}, Erro: $errorBody")
            }
        }

        override fun onFailure(call: Call<UserModel>, t: Throwable) {
            Log.d("Falha na requisição", "Erro: ${t.message}")
        }
    })
}

fun getUserById(id: String, callback: (UserModel?) -> Unit) {
    val call = ApiClient.apiService.getUserById(id)

    call.enqueue(object : Callback<UserModel> {
        override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
            if (response.isSuccessful) {
                val post = response.body()
                callback(post)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.d("Erro na requisição", "Código: ${response.code()}, Erro: $errorBody")
                callback(null)
            }
        }

        override fun onFailure(call: Call<UserModel>, t: Throwable) {
            Log.d("Falha na requisição", "Erro: ${t.message}")
            callback(null)
        }
    })
}

fun onClickButtonUpdateUser(id: String, userModel: UserModel, callback: (UserModel?) -> Unit) {
    val call = ApiClient.apiService.putUserById(id, userModel)

    call.enqueue(object : Callback<UserModel> {
        override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
            if (response.isSuccessful) {
                val post = response.body()
                callback(post)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.d("Erro na requisição", "Código: ${response.code()}, Erro: $errorBody")
                callback(null)
            }
        }

        override fun onFailure(call: Call<UserModel>, t: Throwable) {
            Log.d("Falha na requisição", "Erro: ${t.message}")
            callback(null)
        }
    })
}

fun onClickButtonDeleteUser(id: String, callback: (UserModel?) -> Unit){
    val call = ApiClient.apiService.deleteUserById(id)


    call.enqueue(object : Callback<UserModel> {
        override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
            if (response.isSuccessful) {
                val post = response.body()
                Log.d("Deu bom", "foi a requisicao")
                callback(post)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.d("Erro na requisição", "Código: ${response.code()}, Erro: $errorBody")
                callback(null)
            }
        }

        override fun onFailure(call: Call<UserModel>, t: Throwable) {
            Log.d("Falha na requisição", "Erro: ${t.message}")
            callback(null)
        }
    })
}