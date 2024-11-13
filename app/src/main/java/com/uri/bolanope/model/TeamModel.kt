package com.uri.bolanope.model

data class TeamModel(
    val _id: String?,
    val description: String?,
    val leader_id: String?,
    val members_id: List<String>?,
    val name: String?,
    val image: String?
)