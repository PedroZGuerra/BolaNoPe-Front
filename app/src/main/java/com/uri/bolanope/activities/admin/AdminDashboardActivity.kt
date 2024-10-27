package com.uri.bolanope.activities.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.uri.bolanope.activities.common.CardAdmin
import com.uri.bolanope.components.TopBar
import com.uri.bolanope.utils.SharedPreferencesManager

@Composable
fun AdminDashboard(navController: NavHostController) {
//    CardAdmin(navController, Icons.Filled.QueryStats, "Horários das Quadras", "adminDashboard", Modifier.weight(1f))
    Scaffold (
        topBar = { TopBar("Dashboard") },
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding(),
    ) { innerPadding ->
        Row {
            CardAdmin(navController, Icons.Filled.QueryStats, "Usuários", "users", Modifier.weight(1f))
        }
    }
}
