package com.uri.bolanope

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
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
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.uri.bolanope.model.LoginModel
import com.uri.bolanope.model.UserModel
import com.uri.bolanope.ui.theme.BolaNoPeTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import okio.ByteString.Companion.decodeBase64
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BolaNoPeTheme {
                Login()
            }
        }
    }
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Composable
fun Login() {
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    TopBar("Login")

    Column(
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

        Button(onClick = {
            onClickLogin(context, email, password)
        }) {
            Text("Entrar")
        }
    }
}

fun onClickLogin(context: Context, email: String, password: String) {
    // exemplo de como funciona o dataStore (tipo um localstorage)
    val key = "email"
    runBlocking {
        saveDataToDataStore(context, key,  email)

        // pra pegar o valor tem que ser nesse runBlocking
        val email2 = getDataFromDataStore(context, key).first()
        Log.d("LoginActivity", email2.toString())
    }

    val loginModel = LoginModel(email, password, null)
    loginReq(loginModel)

    val intent = Intent(context, LoginActivity::class.java)
    context.startActivity(intent)
}

fun loginReq(loginModel: LoginModel){
    val call = ApiClient.apiService.loginUser(loginModel)

    call.enqueue(object: Callback<LoginModel> {
        override fun onResponse(call: Call<LoginModel>, response: Response<LoginModel>) {
            if (response.isSuccessful) {
                val post = response.body()
                val token = post?.token
                val decodedToken = token?.decodeBase64()
                Log.d("sucesso na requisição", "${token}, ${decodedToken} ${post}")
            } else {
                val errorBody = response.errorBody()?.string()
                Log.d("Erro na requisição", "Código: ${response.code()}, Erro: $errorBody")
            }
        }

        override fun onFailure(call: Call<LoginModel>, t: Throwable) {
            Log.d("Falha na requisição", "Erro: ${t.message}")
        }

    })
}

