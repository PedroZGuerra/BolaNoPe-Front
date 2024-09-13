package com.uri.bolanope

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter

@Composable
fun Field(navController: NavHostController, fieldId: String?) {

    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var value_hour by remember { mutableStateOf("") }
    var obs by remember { mutableStateOf("") }
    var openTime by remember { mutableStateOf("14:00") }
    var closeTime by remember { mutableStateOf("14:00") }
    var imageName by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher to pick an image from the gallery
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageName = getFileNameFromUri(context, it)
            selectedImageUri = it
        }
    }

    Scaffold(
        topBar = { TopBar("Criar Quadra") },
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding(),
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                Button(onClick = { launcher.launch("image/*") }) {
                    Text(text = "Selecionar Imagem")
                }

                selectedImageUri?.let { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = "Imagem Selecionada",
                        modifier = Modifier
                            .size(200.dp)
                            .padding(8.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nome") }
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Localização") }
                )

                OutlinedTextField(
                    value = value_hour,
                    onValueChange = { value_hour = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Valor por hora") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    )
                )

                OutlinedTextField(
                    value = obs,
                    onValueChange = { obs = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Observações") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                TimeDropdown(
                    label = "Horário de Abertura",
                    selectedTime = openTime,
                    onTimeSelected = { openTime = it }
                )

                TimeDropdown(
                    label = "Horário de Fechamento",
                    selectedTime = closeTime,
                    onTimeSelected = { closeTime = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeDropdown(
    label: String,
    selectedTime: String,
    onTimeSelected: (String) -> Unit,
    hoursRange: IntRange = 14..23
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        OutlinedTextField(
            value = selectedTime,
            onValueChange = { },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            label = { Text(label) },
            trailingIcon = {
                Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
            },
            readOnly = true
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            hoursRange.forEach { hour ->
                DropdownMenuItem(
                    text = {
                        Text(text = "$hour:00")
                    },
                    onClick = {
                        onTimeSelected("$hour:00")
                        expanded = false
                    }
                )
            }
        }
    }
}

fun getFileNameFromUri(context: android.content.Context, uri: Uri): String {
    var name = ""
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            name = it.getString(nameIndex)
        }
    }
    return name
}