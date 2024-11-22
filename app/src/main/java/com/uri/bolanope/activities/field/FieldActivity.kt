package com.uri.bolanope.activities.field

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.uri.bolanope.model.FieldModel
import com.uri.bolanope.services.ApiClient
import com.uri.bolanope.services.apiCall
import com.uri.bolanope.utils.SharedPreferencesManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.InputStream
import android.util.Base64
import android.util.Log
import androidx.compose.ui.graphics.asImageBitmap
import com.uri.bolanope.activities.team.getCommentsByTeamId
import com.uri.bolanope.components.TopBar
import com.uri.bolanope.model.AllRatingModel
import com.uri.bolanope.model.CommentModel
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
@Composable
fun Field(navController: NavHostController, fieldId: String?) {

    val activityMode = if (fieldId.isNullOrEmpty()) "CREATE" else "UPDATE"
    val topBarTitle = if (fieldId.isNullOrEmpty()) "Criar Quadra" else "Editar Quadra"

    val context = LocalContext.current
    val token = SharedPreferencesManager.getToken(context)

    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var valueHour by remember { mutableStateOf("") }
    var obs by remember { mutableStateOf("") }
    var openTime by remember { mutableStateOf("14:00") }
    var closeTime by remember { mutableStateOf("14:00") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var base64String by remember { mutableStateOf("") }

    LaunchedEffect(fieldId) {
        if (activityMode == "UPDATE" && fieldId != null) {
            getFieldByIdFieldActivity(fieldId) { field ->
                field?.let {
                    name = it.name
                    location = it.location
                    valueHour = it.value_hour
                    obs = it.obs.toString()
                    openTime = it.open_time
                    closeTime = it.close_time
                    base64String = it.image.toString()
                }
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
        }
    }

    Scaffold(
        topBar = { TopBar(topBarTitle) },
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

                if (selectedImageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(selectedImageUri),
                        contentDescription = "Imagem Selecionada",
                        modifier = Modifier
                            .size(200.dp)
                            .padding(8.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    val bitmap = base64ToBitmap(base64String)
                    bitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "Imagem Carregada",
                            modifier = Modifier
                                .size(200.dp)
                                .padding(8.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
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
                    value = valueHour,
                    onValueChange = { valueHour = it },
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

                if (activityMode == "CREATE") {
                    Button(onClick = {
                        val openTimeDate = SimpleDateFormat("HH:mm").parse(openTime)
                        val closeTimeDate = SimpleDateFormat("HH:mm").parse(closeTime)

                        if (name.isEmpty() || location.isEmpty() || valueHour.isEmpty() || selectedImageUri == null || obs.isEmpty()) {
                            Toast.makeText(context, "Por favor, preencha todos os campos obrigatórios", Toast.LENGTH_LONG).show()
                            return@Button
                        }

                        if (openTimeDate != null) {
                            if (openTimeDate.before(closeTimeDate)) {
                                val fieldModel = FieldModel(
                                    _id = null,
                                    name = name,
                                    location = location,
                                    value_hour = valueHour,
                                    obs = obs,
                                    open_time = openTime,
                                    close_time = closeTime,
                                    available = true,
                                    image = null
                                )

                                if (token != null) {
                                    createFieldWithImage(context, fieldModel, selectedImageUri, token) { field ->
                                        if (field != null) {
                                            Toast.makeText(context, "Quadra criada com sucesso", Toast.LENGTH_LONG).show()
                                            navController.navigate("fields")
                                        } else {
                                            Toast.makeText(context, "Falha ao criar quadra", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Horário de abertura não pode ser maior ou igual ao de fechamento", Toast.LENGTH_LONG).show()
                            }
                        }
                    }) {
                        Text("Criar")
                    }
                }

                if (activityMode == "UPDATE") {
                    Button(onClick = {
                        val openTimeDate = SimpleDateFormat("HH:mm").parse(openTime)
                        val closeTimeDate = SimpleDateFormat("HH:mm").parse(closeTime)

                        if (name.isEmpty() || location.isEmpty() || valueHour.isEmpty() || obs.isEmpty()) {
                            Toast.makeText(context, "Por favor, preencha todos os campos obrigatórios", Toast.LENGTH_LONG).show()
                            return@Button
                        }

                        if (openTimeDate != null) {
                            if (openTimeDate.before(closeTimeDate)) {
                                val fieldModel = FieldModel(
                                    _id = null,
                                    name = name,
                                    location = location,
                                    value_hour = valueHour,
                                    obs = obs,
                                    open_time = openTime,
                                    close_time = closeTime,
                                    available = true,
                                    image = null
                                )

                                if (token != null && fieldId != null) {
                                    updateFieldWithImage(context, fieldId, fieldModel, selectedImageUri, token) { field ->
                                        if (field != null) {
                                            Toast.makeText(context, "Quadra editada com sucesso", Toast.LENGTH_LONG).show()
                                            navController.navigate("fields")
                                        } else {
                                            Toast.makeText(context, "Falha ao editar quadra", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Horário de abertura não pode ser maior ou igual ao de fechamento", Toast.LENGTH_LONG).show()
                            }
                        }
                    }) {
                        Text("Editar")
                    }
                }

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

fun getFieldByIdFieldActivity(fieldId: String, callback: (FieldModel?) -> Unit){
    val call = ApiClient.apiService.getFieldById(fieldId)
    apiCall(call, callback)
}

fun updateFieldWithImage(
    context: Context,
    fieldId: String,
    fieldModel: FieldModel,
    imageUri: Uri?,
    token: String,
    callback: (FieldModel?) -> Unit
) {
    val contentResolver: ContentResolver = context.contentResolver

    val imagePart = imageUri?.let {
        val inputStream: InputStream? = contentResolver.openInputStream(it)
        val file = File(context.cacheDir, getFileName(contentResolver, it) ?: "image_temp.jpg")

        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        MultipartBody.Part.createFormData("file_url", file.name, requestFile)
    }

    val namePart = fieldModel.name.toRequestBody("text/plain".toMediaTypeOrNull())
    val locationPart = fieldModel.location.toRequestBody("text/plain".toMediaTypeOrNull())
    val valueHourPart = fieldModel.value_hour.toRequestBody("text/plain".toMediaTypeOrNull())
    val obsPart = fieldModel.obs?.toRequestBody("text/plain".toMediaTypeOrNull())
    val openTimePart = fieldModel.open_time.toRequestBody("text/plain".toMediaTypeOrNull())
    val closeTimePart = fieldModel.close_time.toRequestBody("text/plain".toMediaTypeOrNull())
    val availablePart = fieldModel.available.toString().toRequestBody("text/plain".toMediaTypeOrNull())

    val call = ApiClient.apiService.putFieldWithImage(
        fieldId,
        namePart,
        locationPart,
        valueHourPart,
        obsPart,
        openTimePart,
        closeTimePart,
        availablePart,
        imagePart,
        "Bearer $token"
    )

    apiCall(call, callback)
}

fun createFieldWithImage(
    context: Context,
    fieldModel: FieldModel,
    imageUri: Uri?,
    token: String,
    callback: (FieldModel?) -> Unit
) {
    val contentResolver: ContentResolver = context.contentResolver

    val imagePart = imageUri?.let {
        val inputStream: InputStream? = contentResolver.openInputStream(it)
        val file = File(context.cacheDir, getFileName(contentResolver, it) ?: "image_temp.jpg")

        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        MultipartBody.Part.createFormData("file_url", file.name, requestFile)
    }

    val namePart = fieldModel.name.toRequestBody("text/plain".toMediaTypeOrNull())
    val locationPart = fieldModel.location.toRequestBody("text/plain".toMediaTypeOrNull())
    val valueHourPart = fieldModel.value_hour.toRequestBody("text/plain".toMediaTypeOrNull())
    val obsPart = fieldModel.obs?.toRequestBody("text/plain".toMediaTypeOrNull())
    val openTimePart = fieldModel.open_time.toRequestBody("text/plain".toMediaTypeOrNull())
    val closeTimePart = fieldModel.close_time.toRequestBody("text/plain".toMediaTypeOrNull())
    val availablePart = fieldModel.available.toString().toRequestBody("text/plain".toMediaTypeOrNull())

    val call = ApiClient.apiService.postFieldWithImage(
        namePart,
        locationPart,
        valueHourPart,
        obsPart,
        openTimePart,
        closeTimePart,
        availablePart,
        imagePart,
        "Bearer $token"
    )

    apiCall(call, callback)
}

fun getFileName(contentResolver: ContentResolver, uri: Uri): String? {
    var name: String? = null
    val cursor = contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0) {
                name = it.getString(nameIndex)
            }
        }
    }
    return name
}

fun base64ToBitmap(base64String: String): Bitmap? {
    val decodedString = Base64.decode(base64String, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
}