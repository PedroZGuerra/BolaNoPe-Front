package com.uri.bolanope.activities.user

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.uri.bolanope.components.TopBar
import com.uri.bolanope.model.ChangePasswordModel
import com.uri.bolanope.model.UserModel
import com.uri.bolanope.services.ApiClient
import com.uri.bolanope.services.apiCall
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

@Composable
fun ChangePasswordActivity(navController: NavHostController, userId: String?) {
    var newPassword by remember { mutableStateOf("") }
    var context = LocalContext.current

    Scaffold(
        topBar = {
            TopBar(
                "Mudar senha",
            )
        },

        content ={ innerpadding ->
            Column(
                modifier = Modifier
                    .padding(innerpadding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Nova Senha") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (newPassword.isNotEmpty()) {
                            val changePasswordModel = ChangePasswordModel(
                                id = userId!!,
                                password = newPassword
                            )
                            changePassword(
                                userModel = changePasswordModel
                            ) { result ->
                                if (result != null) {
                                    Toast.makeText(context, "Senha alterada com sucesso", Toast.LENGTH_LONG).show()
                                    navController.popBackStack()
                                }
                                Toast.makeText(context, "Erro ao alterar senha", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                ) {
                    Text("Mudar senha")
                }
            }
        }
    )
}
fun changePassword(
    userModel: ChangePasswordModel,
    callback: (UserModel?) -> Unit
) {
    val passwordPart = userModel.password.toRequestBody("text/plain".toMediaTypeOrNull())

    val call = ApiClient.apiService.changeUserPassword(
        id = userModel.id,
        password = passwordPart,
    )
    apiCall(call, callback)
}
