package com.uri.bolanope.model

data class CommentModel(
    var _id: String?,
    var team_id: String?,
    var field_id: String?,
    var user_id: String,
    var comment: String,
    var created_at: String,
)
