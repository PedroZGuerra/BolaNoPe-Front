package com.uri.bolanope.activities.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.uri.bolanope.R
import com.uri.bolanope.components.TopBar
import com.uri.bolanope.utils.SharedPreferencesManager

@Composable
fun HomeAdmin(navController: NavHostController) {
    val context = LocalContext.current
    Scaffold (
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    SharedPreferencesManager.clearToken(context)
                    SharedPreferencesManager.clearUserId(context)
                    SharedPreferencesManager.clearUserRole(context)
                    navController.navigate("welcome") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                backgroundColor = Color(0xFF4CAF50),
                contentColor = Color.White,
                modifier = Modifier.size(72.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Sair")
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding(),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_logo_bolanope),
                contentDescription = "Logo Bola no Pé",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(top = 100.dp)
                    .size(150.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                CardAdmin(navController, Icons.Filled.Home, "Home", "home", Modifier.weight(1f))
                CardAdmin(navController, Icons.Filled.SportsSoccer, "Campos", "fields", Modifier.weight(1f))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                CardAdmin(navController, Icons.Filled.Person, "Criar Professor", "createTeacher", Modifier.weight(1f))
                CardAdmin(navController, Icons.Filled.EmojiEvents, "Torneios", "exploreTourneys", Modifier.weight(1f))
            }
            CardAdmin(navController, Icons.Filled.QueryStats, "Dashboard", "adminDashboard", Modifier.weight(1f))
        }
    }
}

@Composable
fun CardAdmin(navController: NavHostController, icon: ImageVector, text: String, route: String, modifier: Modifier = Modifier) {
    Card(
        onClick = {
            navController.navigate(route)
        },
        modifier = modifier
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = Color.Black,
                modifier = Modifier.size(48.dp)
            )
            Text(text)
            Spacer(Modifier.height(16.dp))
        }
    }
}