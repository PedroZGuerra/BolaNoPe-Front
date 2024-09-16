package com.uri.bolanope

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.uri.bolanope.model.FieldModel
import com.uri.bolanope.model.ReserveModel
import com.uri.bolanope.services.ApiClient
import com.uri.bolanope.services.apiCall

@Composable
fun FieldHistory(navController: NavHostController, fieldId: String?) {
    val context = LocalContext.current

    val reserves = remember { mutableStateOf<List<ReserveModel>?>(null) }
    val isLoading = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (fieldId != null) {
            getFieldHistory(fieldId) { result ->
                reserves.value = result
                Log.d("FieldHistory", "${result}")
                isLoading.value = false
            }
        }
    }

    if (isLoading.value) {
        Text("Loading history...")
    } else {
        Column {
            reserves.value?.let { reserveList ->
                reserveList.forEach { reserve ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = reserve.reserve_day.toString(),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Column(
                                    modifier = Modifier.weight(0.5f),
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Button(
                                        onClick = {
                                            navController.navigate("reserve/${field._id}")
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = Color(0xFF4CAF50)
                                        )
                                    ) {
                                        Text("Editar")
                                    }

                                    Button(
                                        onClick = {
                                            fieldToDelete = field
                                            showDialog = true
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = Color(0xFFC8473F)
                                        )
                                    ) {
                                        Text("Excluir")
                                    }
                                }
                            }
                        }
                    }
                }
            } ?: run {
                Toast.makeText(context, "No reservations found", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

fun getFieldHistory(fieldId: String, callback: (List<ReserveModel>?) -> Unit) {
    val call = ApiClient.apiService.getFieldHistory(fieldId)
    Log.d("FieldHistory", fieldId)
    Log.d("FieldHistory", "${callback}")
    apiCall(call, callback)
}
