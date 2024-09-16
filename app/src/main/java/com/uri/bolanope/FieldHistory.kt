package com.uri.bolanope

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.uri.bolanope.model.FieldModel
import com.uri.bolanope.model.ReserveModel
import com.uri.bolanope.services.ApiClient
import com.uri.bolanope.services.apiCall
import com.uri.bolanope.ui.theme.Green80
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun FieldHistory(navController: NavHostController, fieldId: String?) {
    val context = LocalContext.current

    val reserves = remember { mutableStateOf<List<ReserveModel>?>(null) }

    val today = LocalDate.now()
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    LaunchedEffect(Unit) {
        if (fieldId != null) {
            getFieldHistory(fieldId) { result ->
                reserves.value = result
            }
        }
    }

    Scaffold(
        topBar = { TopBar("Histórico") },
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
                reserves.value?.let { reserveList ->
                    reserveList.forEach { reserve ->
                        val reserveDayParsed = LocalDate.parse(reserve.reserve_day, dateFormatter)

                        val cardColor = if (reserveDayParsed.isBefore(today)) {
                            Color.LightGray
                        } else {
                            Green80
                        }
                        val textCardColor = if (reserveDayParsed.isBefore(today)) {
                            Color.Black
                        } else {
                            Color.White
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            colors = CardColors(
                                contentColor = textCardColor,
                                containerColor = cardColor,
                                disabledContentColor = Color.Gray,
                                disabledContainerColor =Color.Gray
                            )
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
                                        Text(
                                            text = reserve.start_hour
                                        )
                                        Text(
                                            text = reserve.end_hour
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

fun getFieldHistory(fieldId: String, callback: (List<ReserveModel>?) -> Unit) {
    val call = ApiClient.apiService.getFieldHistory(fieldId)
    apiCall(call, callback)
}
