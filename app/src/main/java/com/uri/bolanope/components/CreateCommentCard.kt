package com.uri.bolanope.components

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.uri.bolanope.activities.field.RatingBar
import com.uri.bolanope.model.CommentModel
import com.uri.bolanope.model.PostRatingModel
import com.uri.bolanope.services.ApiClient
import com.uri.bolanope.services.apiCall

@Composable
fun CreateComment(
    teamId: String?,
    fieldId: String?,
    userToken: String,
    userId: String,
    isField: Boolean,
    onCommentSubmitted: (String) -> Unit
) {
    var commentText by remember { mutableStateOf("") }
    val context = LocalContext.current

    // rating do usuario pra quadra
    val userRating = remember { mutableFloatStateOf(0.0f) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        if(isField) {
            RatingBar(userRating.floatValue,
                onRatingChanged = {
                    userRating.floatValue = it
                },
            )
        }
        Text(
            text = "Adicionar Comentário",
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = commentText,
            onValueChange = { commentText = it },
            placeholder = { Text("Escreva seu comentário...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (commentText.isNotEmpty()) {
                    if(fieldId != null && userRating.floatValue == 0.0f) {
                        Toast.makeText(context, "Você deve avaliar a quadra.", Toast.LENGTH_LONG).show()
                        return@Button
                    }
                    createComment(teamId, fieldId, userToken, userId, commentText) { result ->
                        if (result != null) {
                            Toast.makeText(context, "Comentário enviado com sucesso!", Toast.LENGTH_LONG).show()
                            if (fieldId != null) {
                                result._id?.let {
                                    postFieldRating(fieldId, userRating.floatValue, it, userToken) {
                                        Log.d("TAG", "CreateComment: teste rating")
                                    }
                                }
                            }
                            onCommentSubmitted(commentText)
                            commentText = ""
                        } else {
                            Toast.makeText(context, "Erro ao enviar comentário", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "O comentário não pode estar vazio.", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier.align(Alignment.End),
            enabled = commentText.isNotEmpty()
        ) {
            Text("Enviar")
        }
    }
}

fun createComment(
    teamId: String?,
    fieldId: String?,
    token: String,
    userId: String,
    comment: String,
    callback: (CommentModel?) -> Unit
) {
    val body = CommentModel(
        _id = null,
        team_id = teamId,
        field_id = fieldId,
        user_id = userId,
        comment = comment,
        created_at = System.currentTimeMillis().toString()
    )

    val call = ApiClient.apiService.createComment(body, "Bearer $token")
    apiCall(call) { response ->
        if (response != null) {
            callback(response)
        } else {
            callback(null)
        }
    }
}

fun postFieldRating(fieldId: String, rating: Float, commentId: String, token: String, callback: (PostRatingModel?) -> Unit) {
    val body = PostRatingModel(
        field_id = fieldId,
        rating = rating,
        comment_id = commentId,
        created_at = System.currentTimeMillis().toString()
    )
    val call = ApiClient.apiService.postFieldRating(body, "Bearer $token")
    apiCall(call, callback)
}

