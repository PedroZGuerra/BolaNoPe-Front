package com.uri.bolanope.model

data class NotificationModel(
    val _id: String?,
    val userId: String,
    val title: String,
    val message: String,
    var read: Boolean
)