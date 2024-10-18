package com.uri.bolanope.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.uri.bolanope.R
import com.uri.bolanope.model.NotificationModel
import com.uri.bolanope.model.TourneyModel
import com.uri.bolanope.services.ApiClient
import com.uri.bolanope.services.apiCall
import retrofit2.await

fun sendNotification(channelId: String, title: String, descriptionText: String,  context: Context, importance: Int?) {
    val notificationId = 1

    var importance = importance
    if (importance == null){
        importance = NotificationManager.IMPORTANCE_DEFAULT
    }
    val channel = NotificationChannel(channelId, title, importance).apply {
        description = descriptionText
    }

    val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_logo_bolanope)
        .setContentTitle(title)
        .setContentText(descriptionText)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    with(NotificationManagerCompat.from(context)) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(context, "Sem permissão de notificação.", Toast.LENGTH_LONG).show()
        }
        notify(notificationId, builder.build())
    }


}

fun readNotification(id: String, callback: (NotificationModel?) -> Unit) {
    val call = ApiClient.apiService.readNotification(id)
    apiCall(call, callback)
}
