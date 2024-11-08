package com.uri.bolanope.activities.admin

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.uri.bolanope.components.BarChartComposable
import com.uri.bolanope.components.PieChartData
import com.uri.bolanope.components.TopBar
import com.uri.bolanope.model.StudentsCount
import com.uri.bolanope.model.UserModel
import com.uri.bolanope.services.ApiClient
import com.uri.bolanope.services.apiCall
import kotlin.math.log

@Composable
fun StudentsByTeacher(navController: NavHostController) {
    val context = LocalContext.current
    val chartData = remember { mutableStateOf<List<PieChartData>>(emptyList()) }

    val teachers = remember { mutableStateOf<List<UserModel>?>(null) }
    val pieChartDataList = mutableListOf<PieChartData>()
    val completedRequests = remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        getAllUsersByRole("professor") { result ->
            teachers.value = result
            val totalRequests = result?.size ?: 0

            if (totalRequests > 0) {
                result?.forEach { teacher ->
                    val teacherId = teacher._id
                    val teacherName = teacher.name

                    if (teacherId != null) {
                        getStudentsByTeacher(teacherId) { studentResult ->
                            val studentCount = studentResult?.studentCount
                            if (studentCount != null) {
                                pieChartDataList.add(PieChartData(teacherName, studentCount.toFloat()))
                            }
                            completedRequests.value++

                            if (completedRequests.value == totalRequests) {
                                chartData.value = pieChartDataList
                            }
                        }
                    } else {
                        completedRequests.value++
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = { TopBar("Número de alunos por professor") },
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding(),
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            if (completedRequests.value == (teachers.value?.size ?: 0) && chartData.value.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxSize()) {
                    BarChartComposable(chartData.value, context)
                }
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text("Carregando dados...")
                }
            }
        }
    }
}

fun getStudentsByTeacher(id: String, callback: (StudentsCount?) -> Unit) {
    val call = ApiClient.apiService.getStudentsByTeacher(id)
    apiCall(call, callback)
}