package com.uri.bolanope

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavigationBar(userId: String) {
    val context = LocalContext.current

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
            label = { Text("Home", color= Color.White) },
            selected = false,
            onClick = { onClickHome(context) }
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Filled.AccountCircle, contentDescription = "Profile", tint= Color.White) },
            label = { Text("Perfil", color= Color.White) },
            selected = false,
            onClick = { onClickProfile(context, userId) }
        )
    }

}

fun onClickProfile(context: Context, userId: String){
    val intent = Intent(context, UserProfileActivity()::class.java).apply {
        putExtra("ACTIVITY_MODE", "UPDATE")
        putExtra("USER_ID", userId)
    }
    context.startActivity(intent)
}

fun onClickHome(context: Context){
    val intent = Intent(context, HomeActivity()::class.java).apply {
        putExtra("ACTIVITY_MODE", "HOME")
    }
    context.startActivity(intent)
}

