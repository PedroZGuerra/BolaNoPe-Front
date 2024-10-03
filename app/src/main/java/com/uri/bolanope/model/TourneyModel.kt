package com.uri.bolanope.model;

data class TourneyModel(
        val _id: String?,
        var name: String,
        var description: String,
        var prize: String,
        val id_teams: List<String>,
        var date_from: String,
        var date_until: String,
)
