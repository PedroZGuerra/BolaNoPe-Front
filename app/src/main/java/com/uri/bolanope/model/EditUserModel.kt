package com.uri.bolanope.model

data class EditUserModel(
    val name: String,
    val birth: String,
    val email: String,
    val cep: String,
    val image: String?
)