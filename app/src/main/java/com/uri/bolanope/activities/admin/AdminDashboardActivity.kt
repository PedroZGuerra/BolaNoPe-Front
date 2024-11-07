package com.uri.bolanope.activities.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.uri.bolanope.activities.common.CardAdmin
import com.uri.bolanope.components.TopBar

@Composable
fun AdminDashboard(navController: NavHostController) {
    Scaffold(
        topBar = { TopBar("Dashboard") },
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding(),
    ) { innerPadding ->
        Column {
            Row {
                CardAdmin(navController, icon = Icons.Default.Person, "Usuários por idade", "users")
            }
            Row {
                CardAdmin(navController, icon = Icons.Default.EmojiEvents, "Times Inscritos por Torneio", "teamsByTourney")
            }
            Row {
                CardAdmin(navController, icon = Icons.Default.EmojiEvents, "Horários mais reservados por quadra", "most-reserved-times-fields-list")
            }
        }
    }
}