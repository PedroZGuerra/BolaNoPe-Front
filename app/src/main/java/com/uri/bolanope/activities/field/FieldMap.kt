package com.uri.bolanope.activities.field

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.uri.bolanope.components.TopBar
import com.uri.bolanope.model.GeocodeApiResponseModel
import com.uri.bolanope.services.GoogleMapsApiClient
import com.uri.bolanope.services.apiCall
import com.uri.bolanope.ui.theme.Green80

@Composable
fun FieldMap(navHostController: NavHostController, fieldLocation: String, fieldName: String) {
    val context = LocalContext.current
    var lat by remember { mutableDoubleStateOf(0.0) }
    var lng by remember { mutableDoubleStateOf(0.0) }
    var isMapReady by remember { mutableStateOf(false) }
    var isLocationValid by remember { mutableStateOf(false) }

    LaunchedEffect(isMapReady) {
        getGeocodeLocation(fieldLocation) { location ->
            if (location != null && location.results.isNotEmpty()) {
                lat = location.results.first().geometry.location.lat
                lng = location.results.first().geometry.location.lng
                isMapReady = true
                isLocationValid = true
            }else {
                isLocationValid = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopBar("Mapa")
        },
    ){ innerPadding ->
        if (isMapReady && isLocationValid) {
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(LatLng(lat, lng), 18f)
                Log.d("location", "ReserveField: $position")
            }

            GoogleMap(
                cameraPositionState = cameraPositionState
            ) {
                Marker(
                    state = MarkerState(position = LatLng(lat, lng)),
                    title = fieldName
                )
            }
            Button(
                onClick = {
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "$fieldName: $fieldLocation")
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Compartilhar localização"))
                },
                colors = ButtonDefaults.buttonColors(contentColor = Color.White, containerColor = Green80),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Compartilhar localização", color = Color.White)
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Carregando mapa...",
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

fun getGeocodeLocation(location: String, callback: (GeocodeApiResponseModel?) -> Unit) {
    val call = GoogleMapsApiClient.apiService.getGeocode(address = location)
    apiCall(call, callback)
}
