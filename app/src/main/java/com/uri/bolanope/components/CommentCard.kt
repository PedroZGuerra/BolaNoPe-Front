package com.uri.bolanope.components

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.uri.bolanope.activities.team.deleteTeam
import com.uri.bolanope.activities.user.getUserById
import com.uri.bolanope.model.UserModel
import com.uri.bolanope.services.ApiClient
import com.uri.bolanope.services.apiCall
import com.uri.bolanope.utils.SharedPreferencesManager
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun CommentCard(
    userId: String,
    commentId: String,
    commentText: String,
    time: String,
    onDeleteComment: () -> Unit
) {
    val context = LocalContext.current
    val currentUserId = SharedPreferencesManager.getUserId(context)
    val user = remember { mutableStateOf<UserModel?>(null) }
    val showDeleteDialog = remember { mutableStateOf(false) }

    LaunchedEffect(user) {
        getUserById(userId) { result ->
            user.let {
                user.value = result
            }
        }
    }
    if (user.value != null){
        if (showDeleteDialog.value) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog.value = false },
                title = { Text("Confirmar Exclusão") },
                text = { Text("Você tem certeza que deseja deletar este time?") },
                confirmButton = {
                    Button(
                        onClick = {
                            onDeleteComment()
                            showDeleteDialog.value = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Deletar")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDeleteDialog.value = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }

        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(user.value!!.image),
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = user.value!!.name,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )

                        Text(
                            text = formatDateTime(time),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = commentText,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (userId == currentUserId) {
                        TextButton(
                            onClick = { showDeleteDialog.value = true },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Delete", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

fun formatDateTime(isoTime: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    inputFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
    val date = inputFormat.parse(isoTime)

    val outputFormat = SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault())

    return outputFormat.format(date)

}
