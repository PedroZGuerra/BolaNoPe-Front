package com.uri.bolanope.model

data class FieldModel(
    val _id: String?,
    val name: String,
    val location: String,
    val value_hour: String,
    val obs: String?,
    val open_time: String,
    val close_time: String,
    val available: Boolean,
    val image: String?
)