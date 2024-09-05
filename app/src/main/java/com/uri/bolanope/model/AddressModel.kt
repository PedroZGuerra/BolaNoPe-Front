package com.uri.bolanope.model

data class UserModel(
    val _id: String?,
    val name: String,
    val cpf: String,
    val birth: String,
    val email: String,
    val password: String,
    val cep: String,
    val role: String?,
    val patio: String?,
    val complement: String?,
    val neighborhood: String?,
    val locality: String?,
    val uf: String?
)