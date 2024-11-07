package com.uri.bolanope.activities.field

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.util.Log
import android.widget.TimePicker
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.TextButton
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoNotDisturb
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.uri.bolanope.components.TopBar
import com.uri.bolanope.model.FieldModel
import com.uri.bolanope.model.ReserveModel
import com.uri.bolanope.services.ApiClient
import com.uri.bolanope.services.apiCall
import com.uri.bolanope.ui.theme.Green80
import com.uri.bolanope.utils.SharedPreferencesManager
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ReserveField(navController: NavHostController, fieldId: String?) {

    var _id by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var available by remember { mutableStateOf(true) }
    var open_time by remember { mutableStateOf("") }
    var close_time by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var obs by remember { mutableStateOf("") }
    var value_hour by remember { mutableStateOf("") }
    var reserve_day by remember { mutableStateOf("") }
    val userRole = SharedPreferencesManager.getUserRole(LocalContext.current)

    val field = FieldModel(
        _id = _id,
        name = name,
        available =  available,
        open_time = open_time,
        close_time = close_time,
        location = location,
        obs = obs,
        value_hour = value_hour,
        image = null
    )

    LaunchedEffect(fieldId) {
        if(fieldId != null){
            getFieldById(fieldId) { field ->
                field?.let {
                    _id = it._id.toString()
                    name = it.name
                    available =  it.available
                    open_time = it.open_time
                    close_time = it.close_time
                    location = it.location
                    obs = it.obs
                    value_hour = it.value_hour
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopBar("Reservar Quadra")
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))
            FieldDetailRow(
                icon = if (available) Icons.Default.Check else Icons.Default.DoNotDisturb,
                label = "Disponível:",
                value = if (available) "Sim" else "Não",
                color = if (available) Green80 else Color.Red
            )

            FieldDetailRow(
                icon = Icons.Default.Schedule,
                label = "Horário:",
                value = "${open_time} - ${close_time}"
            )

            FieldDetailRow(
                icon = Icons.Default.Place,
                label = "Localização:",
                value = location
            )

            FieldDetailRow(
                icon = Icons.Default.Info,
                label = "Observação:",
                value = obs
            )

            FieldDetailRow(
                icon = Icons.Default.AttachMoney,
                label = "Valor por hora:",
                value = value_hour
            )

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.Gray, thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            var showDialog by remember { mutableStateOf(false) }

            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            ) {
                if (userRole == "admin"){
                    Button(
                        onClick = {
                            navController.navigate("fieldHistory/${fieldId}")
                        },
                    ) {
                        Text("Histórico")
                    }

                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {showDialog = true},
                ) {
                    Text("Alugar")
                }
            }

            if (showDialog) {
                ReservePopup(onDismiss = { showDialog = false }, field, fieldId)
            }
        }
    }
}

@Composable
fun FieldDetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String, color: Color = Color.Black) {
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp), tint = color)
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, fontWeight = FontWeight.Bold, color = color)
        Spacer(modifier = Modifier.width(4.dp))
        Text(value, color = color)
    }
}

fun getFieldById(id: String, callback: (FieldModel?) -> Unit) {
    val call = ApiClient.apiService.getFieldById(id)
    apiCall(call, callback)
}

@Composable
fun ReservePopup(onDismiss: () -> Unit, field: FieldModel, fieldId: String?) {
    val context = LocalContext.current
    var start_time_selected by remember { mutableStateOf(field.open_time) }
    var end_time_selected by remember { mutableStateOf(field.close_time) }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val today = dateFormat.format(Calendar.getInstance().time)
    var reserve_day by remember { mutableStateOf(today) }
    var total_price by remember { mutableFloatStateOf(0.0F) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Escolha os horários", style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    TimeSelectionDropdown(
                        selectedTime = start_time_selected,
                        onTimeSelected = { start_time_selected = it },
                        startTime = field.open_time,
                        endTime = field.close_time
                    )
                    Text(text = " - ", style = MaterialTheme.typography.h6)
                    TimeSelectionDropdown(
                        selectedTime = end_time_selected,
                        onTimeSelected = { end_time_selected = it },
                        startTime = field.open_time,
                        endTime = field.close_time
                    )
                }

                DateSelectionField(context, reserve_day, onDaySelected = { reserve_day = it})
                total_price = (timeToFloat(end_time_selected) - timeToFloat(start_time_selected)) * priceToFloat(field.value_hour)
                Text("Valor total: R$ %.2f".format(total_price))

                Spacer(modifier = Modifier.height(10.dp))
                val userId = SharedPreferencesManager.getUserId(context)
                Button(
                    onClick = {
                        val reserveModel = ReserveModel(
                            _id = null,
                            id_user = userId.toString(),
                            start_hour = start_time_selected,
                            end_hour = end_time_selected,
                            id_field = fieldId.toString(),
                            final_value = null,
                            reserve_day = reserve_day
                        )
                        postReserve(reserveModel) { response ->
                            if (response != null) {
                                Log.d("ReserveField", "${response}")
                                Toast.makeText(context, "Reserva Criada com sucesso", Toast.LENGTH_LONG).show()
                                onDismiss()
                            } else {
                                Toast.makeText(context, "Falha ao criar reserva", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                ) {
                    Text("Confirmar")
                }

                TextButton(onClick = onDismiss) {
                    Text(
                        text = "Cancelar",
                        style = MaterialTheme.typography.body1.copy(textDecoration = TextDecoration.Underline),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun DateSelectionField(
    context: Context,
    reserveDay: String,
    onDaySelected: (String) -> Unit,
) {

    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDay = dateFormat.format(selectedDate.time)
            onDaySelected(formattedDay)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Row {

        Text(
            "Dia da Reserva:",
            modifier = Modifier
                .align(Alignment.CenterVertically)
        )
        TextButton(onClick = {datePickerDialog.show()}) {
            Text(
                reserveDay,
                style = MaterialTheme.typography.body1.copy(textDecoration = TextDecoration.Underline)
            )
        }
    }
    Spacer(modifier = Modifier.height(10.dp))
}

fun timeToFloat(time: String): Float {
    val parts = time.split(":")
    val hours = parts[0].toFloat()
    val minutes = parts[1].toFloat() / 60
    return hours + minutes
}

fun priceToFloat(price: String): Float {
    Log.d("shoowwww", "chegou aq")
    return price.replace(",", ".").toFloatOrNull() ?: 0.0F
}

fun postReserve(reserveModel: ReserveModel, callback: (ReserveModel?) -> Unit) {
    val call = ApiClient.apiService.postReserve(reserveModel)
    apiCall(call, callback)
}

@Composable
fun TimeSelectionButton(
    selectedTime: String,
    onTimeSelected: (String) -> Unit,
    context: Context
) {
    val calendar = Calendar.getInstance()

    val timePickerDialog = TimePickerDialog(
        context,
        { _: TimePicker, hour: Int, minute: Int ->
            val formattedTime = String.format("%02d:%02d", hour, minute)
            onTimeSelected(formattedTime)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    Column(modifier = Modifier.clickable { timePickerDialog.show() }) {
        Text(text = if (selectedTime.isNotEmpty()) selectedTime else "Nenhum horário selecionado")
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun TimeSelectionDropdown(
    selectedTime: String,
    onTimeSelected: (String) -> Unit,
    startTime: String,
    endTime: String
) {
    var expanded by remember { mutableStateOf(false) }

    val timeOptions = generateTimeSlots(startTime, endTime, intervalMinutes = 60)

    Column(modifier = Modifier.clickable { expanded = true }) {
        Text(
            text = if (selectedTime.isNotEmpty()) selectedTime else "Nenhum horário selecionado",
            style = MaterialTheme.typography.body1.copy(textDecoration = TextDecoration.Underline)
        )
        Spacer(modifier = Modifier.height(8.dp))

        val sizeOfOneItem by remember {
            mutableStateOf(50.dp)
        }
        val configuration = LocalConfiguration.current
        val screenHeight50 by remember {
            val screenHeight = configuration.screenHeightDp.dp
            mutableStateOf(screenHeight / 2)
        }
        val height by remember(timeOptions.size) {
            val itemsSize = sizeOfOneItem * timeOptions.size
            mutableStateOf(minOf(itemsSize, screenHeight50))
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            LazyColumn(
                modifier = Modifier
                    .width(70.dp)
                    .height(height)
            ) {
                items(timeOptions) { time ->
                    DropdownMenuItem(
                        onClick = {
                            onTimeSelected(time)
                            expanded = false
                        },
                        text = {
                            Text(text = time)
                        }
                    )
                }
            }
        }
    }
}

fun generateTimeSlots(startTime: String, endTime: String, intervalMinutes: Int): List<String> {
    val timeSlots = mutableListOf<String>()
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val calendar = Calendar.getInstance()

    val start = dateFormat.parse(startTime)
    val end = dateFormat.parse(endTime)
    if (start != null && end != null) {
        calendar.time = start
        while (!calendar.time.after(end)) {
            timeSlots.add(dateFormat.format(calendar.time))
            calendar.add(Calendar.MINUTE, intervalMinutes)
        }
    }
    return timeSlots
}