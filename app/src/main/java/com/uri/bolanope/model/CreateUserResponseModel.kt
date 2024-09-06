package com.uri.bolanope.model

data class CreateUserResponseModel(
    val user: UserModel,
    val token: String
)