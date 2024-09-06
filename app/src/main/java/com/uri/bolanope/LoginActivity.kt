package com.uri.bolanope

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.uri.bolanope.model.LoginModel
import com.uri.bolanope.model.TokenModel
import com.uri.bolanope.model.UserModel
import com.uri.bolanope.ui.theme.BolaNoPeTheme
import com.uri.bolanope.utils.decodeJWT
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.log

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
            label = { Text("Senha") },
            visualTransformation = PasswordVisualTransformation()
        )

        Button(onClick = { onClickLogin(context, email, password) }) {
            Text("Entrar")
        }
    }
}

fun onClickLogin(context: Context, email: String, password: String){

    val loginModel: LoginModel = LoginModel(email, password)

    loginUser(loginModel){ tokenModel ->
        if(tokenModel != null){
            val intent = Intent(context, HomeActivity::class.java).apply {
                putExtra("USER_ID", decodeJWT(tokenModel.token))
            }
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "Falha no login. Email/Senha incorretos", Toast.LENGTH_LONG).show()
        }
    }
}

fun loginUser(body: LoginModel, callback: (TokenModel?) -> Unit) {
    val call = ApiClient.apiService.loginUser(body)

    call.enqueue(object : Callback<TokenModel> {
        override fun onResponse(call: Call<TokenModel>, response: Response<TokenModel>) {
            if (response.isSuccessful) {
                val token = response.body()
                callback(token)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.d("Erro na requisição", "Código: ${response.code()}, Erro: $errorBody")
                callback(null)
            }
        }

        override fun onFailure(call: Call<TokenModel>, t: Throwable) {
            Log.d("Falha na requisição", "Erro: ${t.message}")
            callback(null)
        }
    })
}
