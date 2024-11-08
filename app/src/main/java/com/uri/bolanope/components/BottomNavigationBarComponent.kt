package com.uri.bolanope.components

import android.content.Context

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.uri.bolanope.utils.SharedPreferencesManager

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val context = LocalContext.current
    val userRole = SharedPreferencesManager.getUserRole(context)

    val labelText = if (userRole == "admin") "Home Admin" else "Home"
    val onClickAction = if (userRole == "admin") {
        { onClickHomeAdmin(navController) }
    } else {
        { onClickHome(navController) }
    }

    BottomNavigation(
        backgroundColor = Color(0xFF77CC5C),
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(vertical = 8.dp)
    )
    {
        BottomNavigationItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home", tint= Color.White) },
            label = { Text(labelText, color= Color.White) },
            selected = false,
            onClick = onClickAction
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Filled.Notifications, contentDescription = "Notificações", tint= Color.White) },
            label = { Text("Notificações", color= Color.White) },
            selected = false,
            onClick = { onClickNotification(navController) }
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Filled.AccountCircle, contentDescription = "Profile", tint= Color.White) },
            label = { Text("Perfil", color= Color.White) },
            selected = false,
            onClick = { onClickProfile(navController, context) }
        )
    }

}

fun onClickProfile(navController: NavHostController, context: Context) {
    val userId = SharedPreferencesManager.getUserId(context)

    if (userId != null) {
        navController.navigate("user/$userId")
    }
}

fun onClickHome(navController: NavHostController){
    navController.navigate("home")
}

fun onClickHomeAdmin(navController: NavHostController){
    navController.navigate("homeAdmin")
}

fun onClickNotification(navController: NavHostController){
    navController.navigate("notifications")
}

