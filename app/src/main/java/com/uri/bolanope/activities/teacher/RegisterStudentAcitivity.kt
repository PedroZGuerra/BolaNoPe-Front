package com.uri.bolanope.activities.teacher

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.uri.bolanope.components.TopBar
import com.uri.bolanope.model.FieldModel
import com.uri.bolanope.model.RegisterStudentModel
import com.uri.bolanope.model.StudentModel
import com.uri.bolanope.model.UserModel
import com.uri.bolanope.services.ApiClient
import com.uri.bolanope.services.ApiService
import com.uri.bolanope.services.apiCall
import com.uri.bolanope.utils.MaskVisualTransformation

@Composable
fun RegisterStudentActivity(navController: NavHostController, teacherId: String) {
    var name by remember { mutableStateOf("") }
    var birth by remember { mutableStateOf("") }
    var cep by remember { mutableStateOf("") }
    var cpf by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    val cpf_mask = "###.###.###-##"
    val cpf_len = 11

    val date_mask = "##/##/####"
    val date_len = 8

    val cep_mask = "#####-###"
    val cep_len = 8

    Scaffold(
        topBar = {
            TopBar("Registrar Aluno")
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
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nome") }
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
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
                Button(
                    onClick = {
                        val student = RegisterStudentModel(
                            name = name,
                            email = email,
                            birth = birth,
                            cep = cep,
                            id_professor = teacherId,
                            cpf = cpf
                        )
                        createStudent(student) { result ->
                            if (result != null) {
                                Toast.makeText(navController.context, "Registrado!", Toast.LENGTH_LONG).show()
                                navController.popBackStack()
                            }else {
                                Toast.makeText(navController.context, "Falha ao registrar aluno.", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Enviar")
                }
            }
        }
    )
}


fun createStudent(student: RegisterStudentModel, callback: (StudentModel?) -> Unit) {
    val call = ApiClient.apiService.createStudent(student)
    apiCall(call, callback)
}