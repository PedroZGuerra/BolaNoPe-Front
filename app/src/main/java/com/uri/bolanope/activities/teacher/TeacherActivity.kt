package com.uri.bolanope.activities.teacher

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.uri.bolanope.activities.user.getUserById
import com.uri.bolanope.components.TopBar
import com.uri.bolanope.model.UserModel
import com.uri.bolanope.utils.SharedPreferencesManager

@Composable
fun TeacherActivity(navController: NavHostController, teacherId: String) {
    val context = LocalContext.current
    var teacherName by remember { mutableStateOf("") }
    val userRole = SharedPreferencesManager.getUserRole(context)

    LaunchedEffect(teacherId) {
        if (teacherId != null) {
            getUserById(teacherId) { user ->
                user?.let {
                    teacherName = it.name
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopBar("Professor: $teacherName".ifEmpty { "Carregando..." })
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
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Professor: $teacherName".ifEmpty { "Carregando..." },
                    style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Dia da semana: Quarta-Feira",
                    style = MaterialTheme.typography.body1
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Horário: 14:00",
                    style = MaterialTheme.typography.body1
                )

                Spacer(modifier = Modifier.height(32.dp))
                if(userRole != "admin") {
                    Button(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally),
                        onClick = {
                            navController.navigate("registerStudent/$teacherId")
                        }
                    ) {
                        Text("Matricular meu filho", color = Color.White)
                    }
                }
            }
        }
    )
}