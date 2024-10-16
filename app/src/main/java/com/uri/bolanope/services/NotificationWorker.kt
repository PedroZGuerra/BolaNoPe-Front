package com.uri.bolanope.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.uri.bolanope.activities.team.getAllUsers
import com.uri.bolanope.model.NotificationModel
import com.uri.bolanope.model.UserModel
import com.uri.bolanope.utils.SharedPreferencesManager
import com.uri.bolanope.utils.readNotification
import com.uri.bolanope.utils.sendNotification
import retrofit2.await
import java.net.HttpURLConnection
import java.net.URL
import kotlin.contracts.contract

class NotificationWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val userId = SharedPreferencesManager.getUserId(applicationContext)
        if (userId != null){
            val notifications = fetchNotifications(userId)

            if (notifications != null) {
                notifications.forEach { notification ->
                    if(!notification.read){
                        notification._id?.let {
                            sendNotification("1",
                                notification.title, notification.message, applicationContext, 5)
                            readNotification(notification._id) { notificationRead ->

                            }
                        }
                    }
                }
                return Result.success()
            } else {
                return Result.failure()
            }
        }
        return Result.success()
    }

    private suspend fun fetchNotifications(userId: String): List<NotificationModel>? {
        return try {
            val call = ApiClient.apiService.getNotification(userId)
            val response = call.await()
            Log.d("TAG", "chegou $response")
            response
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
