package com.uri.bolanope.activities.admin

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
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.uri.bolanope.activities.field.deleteFieldById
import com.uri.bolanope.activities.field.getAllFields
import com.uri.bolanope.activities.field.getMostReservedTimes
import com.uri.bolanope.components.TopBar
import com.uri.bolanope.model.FieldModel
import com.uri.bolanope.model.MostReservedTimesModel
import com.uri.bolanope.utils.SharedPreferencesManager

@Composable
fun MostReservedTimesFieldList(navController: NavHostController) {
    val fields = remember { mutableStateOf<List<FieldModel>?>(null) }
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var fieldToDelete by remember { mutableStateOf<FieldModel?>(null) }
    val mostReservedTimes = remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(Unit) {
        getAllFields { result ->
            if (result != null) {
                fields.value = result
            } else {
                Toast.makeText(context, "Falha ao carregar os campos.", Toast.LENGTH_LONG).show()
            }
        }
        getMostReservedTimes { result ->
            if (result != null) {
                Log.d("TAG", "Fields: $result")
                val timesMap = result.associate { it.id_field to it.most_reserved_times[0].time }
                mostReservedTimes.value = timesMap
            } else {
                Toast.makeText(context, "Falha ao carregar os horários mais reservados", Toast.LENGTH_LONG).show()
            }
        }
    }

    Scaffold(
        topBar = { TopBar("Quadras") },
        floatingActionButton = {
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
                            onClick = {
                                navController.navigate("most-reserved-times/${field._id}")
                            },
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                                }
                            }
                        }
                    }
                }
            } else {
                Text(text = "Carregando campos...")
            }
        }
    }
}
