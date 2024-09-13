package com.uri.bolanope.model

data class FieldModel(
    val _id: String?,
    val name: String,
    val available: Boolean,
    var open_time: String,
    var close_time: String,
    val location: String,
    val obs: String,
    val value_hour: String,
)