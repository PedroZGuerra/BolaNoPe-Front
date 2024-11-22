package com.uri.bolanope.model

data class RatingFieldModel(
    val _id: String,
    val field_id: String,
    val user_id: String,
    val rating: Int,
    val comment_id: String,
    val created_at: String,
    val updated_at: String
)
