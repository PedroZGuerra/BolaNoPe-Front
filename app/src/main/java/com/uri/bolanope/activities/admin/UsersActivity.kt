package com.uri.bolanope.activities.admin

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.uri.bolanope.components.PieChart
import com.uri.bolanope.components.PieChartData
import com.uri.bolanope.components.TopBar
import com.uri.bolanope.model.UserModel
import com.uri.bolanope.services.ApiClient
import com.uri.bolanope.services.apiCall
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun UsersActivity(navController: NavHostController) {
    val context = LocalContext.current

    val regularUsers = remember { mutableStateOf<List<UserModel>?>(null) }
    val pieChartDataUsers = remember { mutableStateOf<List<PieChartData>>(emptyList()) }

    getAllUsersByRole("user") { result ->
        regularUsers.value = result
        val filteredUsers = filterUsersByAge(regularUsers)

        val data = filteredUsers.map { entry ->
            PieChartData(entry.key, entry.value.toFloat())
        }
        pieChartDataUsers.value = data
    }

    Scaffold(
        topBar = { TopBar("Usuários por idade") },
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding(),
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Box(modifier = Modifier.padding(16.dp)) {
                PieChart("", pieChartDataUsers.value, context)
            }
        }
    }
}

fun getAllUsersByRole(role: String, callback: (List<UserModel>?) -> Unit) {
    val call = ApiClient.apiService.getAllUsersByRole(role)
    apiCall(call, callback)
}

fun filterUsersByAge(users: MutableState<List<UserModel>?>): Map<String, Int> {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("ddMMyyyy", Locale.ENGLISH)

    val under18 = mutableListOf<UserModel>()
    val between18And35 = mutableListOf<UserModel>()
    val between36And50 = mutableListOf<UserModel>()
    val above50 = mutableListOf<UserModel>()

    for (user in users.value!!) {
        val birthDate = LocalDate.parse(user.birth, formatter)
        val age = today.year - birthDate.year - (if (today.dayOfYear < birthDate.dayOfYear) 1 else 0)

        when {
            age <= 17 -> under18.add(user)
            age in 18..35 -> between18And35.add(user)
            age in 36..50 -> between36And50.add(user)
            age > 50 -> above50.add(user)
        }
    }

    return mapOf(
        "Abaixo de 18" to under18.size,
        "18 até 35" to between18And35.size,
        "36 até 50" to between36And50.size,
        "Acima de 50" to above50.size
    )
}
