package com.uri.bolanope.model

data class ReserveModel(
    val _id: String?,
    val id_user: String,
    val start_hour: String,
    val end_hour: String,
    val id_field: String,
    val final_value: String?,
    val reserve_day: String?,
)