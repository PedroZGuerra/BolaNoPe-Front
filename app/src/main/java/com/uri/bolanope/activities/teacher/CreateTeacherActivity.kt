package com.uri.bolanope.activities.teacher

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.uri.bolanope.activities.user.onClickButtonSignUp
import com.uri.bolanope.model.UserModel
import com.uri.bolanope.utils.SharedPreferencesManager
import com.uri.bolanope.utils.decodeJWT
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.uri.bolanope.activities.field.base64ToBitmap
import com.uri.bolanope.activities.field.getFileName
import com.uri.bolanope.components.TopBar
import com.uri.bolanope.model.CreateUserResponseModel
import com.uri.bolanope.services.ApiClient
import com.uri.bolanope.services.apiCall
import com.uri.bolanope.utils.MaskVisualTransformation
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.InputStream

@Composable
fun CreateTeacherActivity(navController: NavHostController) {
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var cpf by remember { mutableStateOf("") }
    var birth by remember { mutableStateOf("") }
    var cep by remember { mutableStateOf("") }

    var weekday by remember { mutableStateOf("") }
    var horario by remember { mutableStateOf("") }

    val cpf_mask = "###.###.###-##"
    val cpf_len = 11

    val date_mask = "##/##/####"
    val date_len = 8

    val cep_mask = "#####-###"
    val cep_len = 8

    val horario_mask = "##"
    val horario_len = 2

    Scaffold(
        topBar = {
            TopBar("Criar Professor")
        },
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        content = { padd ->
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {


                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Email") },
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

                OutlinedTextField(
                    value = weekday,
                    onValueChange = { weekday = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Dia da Semana") },
                )

                OutlinedTextField(
                    value = horario,
                    onValueChange = {
                        if (it.length <= horario_len) {
                            horario = it.filter { it.isDigit() }
                        }
                        horario = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = MaskVisualTransformation(horario_mask),
                    label = { Text("Dia da Semana") },
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(onClick = {
                    val userModel = UserModel(
                        _id = null,
                        name = name,
                        cpf = cpf,
                        birth = birth,
                        email = email,
                        password = password,
                        cep = cep,
                        role = "Teacher",
                        patio = null,
                        complement = null,
                        neighborhood = null,
                        locality = null,
                        uf = null,
                        image = null
                    )

                    onClickButtonCreateTeacher(context, userModel) { response ->
                        if (response != null) {
                            navController.navigate("homeAdmin")


                        } else {
                            Toast.makeText(context, "Falha ao criar conta", Toast.LENGTH_LONG).show()
                        }
                    }
                }) {
                    Text("Criar Professor")
                }
            }
        }
    )
}

fun onClickButtonCreateTeacher(context: Context, userModel: UserModel, callback: (CreateUserResponseModel?) -> Unit) {
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
            file_url = null
        )
        apiCall(call, callback)
}