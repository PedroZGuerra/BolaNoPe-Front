package com.uri.bolanope.model

data class FieldModel(
    val _id: String?,
    val name: String,
    val available: Boolean,
    val open_time: String,
    val close_time: String,
    val location: String,
    val obs: String,
    val value_hour: String,
)