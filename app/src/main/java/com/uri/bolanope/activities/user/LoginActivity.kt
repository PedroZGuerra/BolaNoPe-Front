package com.uri.bolanope.activities.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavHostController
import com.uri.bolanope.R
import com.uri.bolanope.components.TopBar
import com.uri.bolanope.model.LoginModel
import com.uri.bolanope.model.TokenModel
import com.uri.bolanope.services.ApiClient
import com.uri.bolanope.services.apiCall
import com.uri.bolanope.utils.SharedPreferencesManager
import com.uri.bolanope.utils.decodeJWT

@Composable
fun Login(navController: NavHostController) {
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopBar("Entrar")
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
                verticalArrangement = Arrangement.Top
            ) {

                Image(
                    painter = painterResource(id = R.drawable.ic_logo_bolanope),
                    contentDescription = "Logo Bola no Pé",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(top = 100.dp)
                        .size(150.dp)
                )

                Spacer(modifier = Modifier.height(64.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier
                        .fillMaxWidth(),
                    label = { Text("Email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier
                        .fillMaxWidth(),
                    label = { Text("Senha") },
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { onClickLogin(navController, context, email, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text("Entrar")
                }
            }
        }
    )
}


fun onClickLogin(navController: NavHostController, context: Context, email: String, password: String) {
    val loginModel = LoginModel(email, password)

    loginUser(loginModel) { tokenModel ->
        if (tokenModel != null) {
            val (userId, role) = decodeJWT(tokenModel.token)

            if (userId != null && role != null) {
                SharedPreferencesManager.saveUserId(context, userId)
                SharedPreferencesManager.saveUserRole(context, role)
                SharedPreferencesManager.saveToken(context, tokenModel.token)

                if (role == "admin") {
                    navController.navigate("homeAdmin") {
                        popUpTo(navController.currentDestination?.id ?: 0) { inclusive = true }
                    }
                } else {
                    navController.navigate("home") {
                        popUpTo(navController.currentDestination?.id ?: 0) { inclusive = true }
                    }
                }
            }

        } else {
            Toast.makeText(context, "Falha no login. Email/Senha incorretos", Toast.LENGTH_LONG).show()
        }
    }
}

fun loginUser(body: LoginModel, callback: (TokenModel?) -> Unit) {
    val call = ApiClient.apiService.loginUser(body)
    apiCall(call, callback)
}
