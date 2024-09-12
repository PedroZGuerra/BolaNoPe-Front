package com.uri.bolanope

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.uri.bolanope.model.FieldModel
import com.uri.bolanope.model.LoginModel
import com.uri.bolanope.model.TokenModel
import com.uri.bolanope.model.UserModel
import com.uri.bolanope.services.ApiClient
import com.uri.bolanope.services.apiCall

@Composable
fun ReserveField(navController: NavHostController, fieldId: String?) {

    var name by remember { mutableStateOf("") }
    var available by remember { mutableStateOf(true) }
    var open_time by remember { mutableStateOf("") }
    var close_time by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var obs by remember { mutableStateOf("") }
    var value_hour by remember { mutableStateOf("") }

    LaunchedEffect(fieldId) {
        if(fieldId != null){
            getFieldById(fieldId) { field ->
                field?.let {
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
    ){
        Column {
            Text(name)
            Text(available.toString())
            Text(open_time)
            Text(close_time)
            Text(location)
            Text(obs)
            Text(value_hour)
        }
    }
}
fun getFieldById(id: String, callback: (FieldModel?) -> Unit) {
    val call = ApiClient.apiService.getFieldById(id)
    apiCall(call, callback)
}
