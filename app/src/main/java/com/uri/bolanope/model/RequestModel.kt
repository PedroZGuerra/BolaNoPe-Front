package com.uri.bolanope.model

data class RequestModel(
    val _id: String?,
    val requested_at: String?,
    val responded_at: String?,
    val status: String?,
    val team_id: String?,
    val user_id: String?
)

data class RequestBody(
    val teamId: String
)

data class AcceptRequestBody(
    val action: String
)
