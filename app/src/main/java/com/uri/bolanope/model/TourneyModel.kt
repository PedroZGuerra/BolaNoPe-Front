package com.uri.bolanope.model;

data class TourneyModel(
        val _id: String?,
        val name: String,
        val description: String,
        val prize: String,
        val id_teams: List<String>,
)
