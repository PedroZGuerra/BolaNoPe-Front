package com.uri.bolanope

import android.content.Context
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
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.uri.bolanope.model.UserModel
import com.uri.bolanope.ui.theme.BolaNoPeTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UserProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BolaNoPeTheme {
                UserProfile()
            }
        }
    }
}

@Composable
fun UserProfile(){
    val context = LocalContext.current

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

    var birthday by remember {
        mutableStateOf("")
    }

    var cep by remember {
        mutableStateOf("")
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
            label = { Text("Email") }
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Senha") }
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
            value = birthday,
            onValueChange = { birthday = it },
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

        Button(onClick = {
            val userModel = UserModel(
                _id = null,
                name = name,
                cpf = cpf,
                birth = birthday,
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

            teste(context, userModel) }) {
            Text("Cadastrar")
        }
    }

}

fun teste(context: Context, userModel: UserModel) {
    val call = ApiClient.apiService.getUserById("66d5f8b146115025622a3b0a")

    call.enqueue(object : Callback<UserModel> {
        override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
            if (response.isSuccessful) {
                val post = response.body()
                Log.d("user retornado", post.toString())
            } else {
                // Logar o corpo de erro em caso de falha
                val errorBody = response.errorBody()?.string() // Pegar o corpo de erro
                Log.d("Erro na requisição", "Código: ${response.code()}, Erro: $errorBody")
            }
        }

        override fun onFailure(call: Call<UserModel>, t: Throwable) {
            // Logar a exceção para verificar o motivo da falha
            Log.d("Falha na requisição", "Erro: ${t.message}")
        }
    })
}