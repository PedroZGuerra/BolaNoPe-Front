package com.uri.bolanope.activities.user

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.uri.bolanope.components.TopBar
import com.uri.bolanope.model.NotificationModel
import com.uri.bolanope.services.ApiClient
import com.uri.bolanope.services.apiCall
import com.uri.bolanope.utils.SharedPreferencesManager
import retrofit2.await

@Composable
fun NotificationPage(navController: NavHostController) {
    val context = LocalContext.current
    val userId = SharedPreferencesManager.getUserId(context)
    val notifications = remember { mutableStateOf<List<NotificationModel>?>(null) }
    LaunchedEffect(Unit) {
        getNotifications(userId!!){ result ->
            if (result != null) {
                notifications.value = result
            } else {
                Toast.makeText(context, "Falha ao carregar as Notificações.", Toast.LENGTH_LONG).show()
            }

        }
    }
    Scaffold(
        topBar = {
            TopBar("Notificações")
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth()
        ) {
            notifications.value?.let {
                for (notification in it) {
                    NotificationItem(notification)
                }
            }
        }


    }
}

fun getNotifications(userId: String, callback: (List<NotificationModel>?) -> Unit) {
    val call = ApiClient.apiService.getNotification(userId)
    apiCall(call, callback)
}

@Composable
fun NotificationItem(notification: NotificationModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = notification.title, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = notification.message, style = MaterialTheme.typography.bodyMedium)
        }
    }

}