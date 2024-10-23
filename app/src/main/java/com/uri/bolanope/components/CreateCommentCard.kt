package com.uri.bolanope.components

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.uri.bolanope.model.CommentModel
import com.uri.bolanope.services.ApiClient
import com.uri.bolanope.services.apiCall

@Composable
fun CreateComment(
    teamId: String,
    userToken: String,
    userId: String,
    onCommentSubmitted: (String) -> Unit
) {
    var commentText by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Adicionar Comentário",
            style = typography.subtitle1,
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
                    createComment(teamId, userToken, userId, commentText) { result ->
                        if (result != null) {
                            Toast.makeText(context, "Comentário enviado com sucesso!", Toast.LENGTH_LONG).show()
                            onCommentSubmitted(commentText) // Notify the parent that a comment was submitted
                            commentText = "" // Clear the input field after submission
                        } else {
                            Toast.makeText(context, "Erro ao enviar comentário", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "O comentário não pode estar vazio.", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier.align(Alignment.End),
            enabled = commentText.isNotEmpty() // Enable the button only when text is not empty
        ) {
            Text("Enviar")
        }
    }
}

fun createComment(team_id: String, token: String, user_id: String, comment: String, callback: (CommentModel?) -> Unit) {
    val body = CommentModel(
        team_id = team_id,
        user_id,
        comment,
        created_at = System.currentTimeMillis().toString()
    )
    val call = ApiClient.apiService.createComment(body, "Bearer $token")
    apiCall(call, callback)
}
