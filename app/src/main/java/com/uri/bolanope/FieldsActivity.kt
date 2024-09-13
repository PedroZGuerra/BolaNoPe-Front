package com.uri.bolanope

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.uri.bolanope.model.FieldModel
import com.uri.bolanope.services.ApiClient
import com.uri.bolanope.services.apiCall
import com.uri.bolanope.utils.SharedPreferencesManager

@Composable
fun Fields(navController: NavHostController) {
    val fields = remember { mutableStateOf<List<FieldModel>?>(null) }
    val context = LocalContext.current
    val token = SharedPreferencesManager.getToken(context)
    var showDialog by remember { mutableStateOf(false) }
    var fieldToDelete by remember { mutableStateOf<FieldModel?>(null) }

    LaunchedEffect(Unit) {
        getAllFields { result ->
            if (result != null) {
                fields.value = result
            } else {
                Toast.makeText(context, "Falha ao carregar os campos.", Toast.LENGTH_LONG).show()
            }
        }
    }

    Scaffold(
        topBar = { TopBar("Quadras") },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("field")
                },
                backgroundColor = Color(0xFF4CAF50),
                contentColor = Color.White,
                modifier = Modifier.size(72.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Adicionar campo")
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (fields.value != null) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    fields.value?.forEach { field ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            elevation = 4.dp
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
                                            text = field.name,
                                            style = MaterialTheme.typography.h6,
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
                                                navController.navigate("field/${field._id}")
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
                }
            } else {
                Text(text = "Carregando campos...")
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Confirmação de exclusão") },
                text = { Text("Tem certeza de que deseja excluir esta quadra?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            fieldToDelete?._id?.let { fieldId ->
                                deleteFieldById(fieldId, token) {
                                    Log.d("field delete", "Fields: deletou")
                                    fields.value = fields.value?.filterNot { it._id == fieldId }
                                    showDialog = false
                                }
                            }
                        }
                    ) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

fun getAllFields(callback: (List<FieldModel>?) -> Unit) {
    val call = ApiClient.apiService.getAllFields()
    apiCall(call, callback)
}

fun deleteFieldById(fieldId : String, token: String?, callback: (Void?) -> Unit){
    val call = ApiClient.apiService.deleteField(fieldId, "Bearer $token")
    apiCall(call, callback)
}