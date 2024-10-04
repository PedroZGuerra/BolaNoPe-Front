package com.uri.bolanope

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.uri.bolanope.model.AcceptRequestBody
import com.uri.bolanope.model.RequestModel
import com.uri.bolanope.model.TeamModel
import com.uri.bolanope.model.UserModel
import com.uri.bolanope.services.ApiClient
import com.uri.bolanope.services.apiCall
import com.uri.bolanope.ui.theme.Green80
import com.uri.bolanope.utils.SharedPreferencesManager

@Composable
fun TeamRequests(navController: NavHostController, teamId: String) {
    val requests = remember { mutableStateOf<List<RequestModel>?>(null) }
    val context = LocalContext.current
    val userId = SharedPreferencesManager.getUserId(context)
    val token = SharedPreferencesManager.getToken(context)

    LaunchedEffect(Unit) {
        getTeamRequests(teamId, token!!) { result ->
            if (result != null) {
                requests.value = result

            } else {
                Toast.makeText(context, "Falha ao carregar os pedidos.", Toast.LENGTH_LONG).show()
            }
        }
    }

    Scaffold(
        topBar = { TopBar("Pedidos") },
        content = { paddingValues ->
            requests.value?.let { teamList ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                ) {
                    items(teamList) { request ->
                        if (userId != null) {
                            RequestCard(request, context, token!!)
                        }
                    }
                }
            } ?: run {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    )
}

@Composable
fun RequestCard(request: RequestModel, context: Context, token: String) {
    val user = remember { mutableStateOf<UserModel?>(null) }
    getUserById(request.user_id!!){ result ->
        if (result != null){
            user.value = result
        } else{
            Log.d("eita", "deu ruim no pegar o user")
        }
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                user.value?.let {
                    Text(text = it.name, style = MaterialTheme.typography.titleLarge)
                    Text(text = it.email, style = MaterialTheme.typography.bodySmall)
                }
            }
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if(request.status == "pending") {
                    Button(
                        colors = ButtonDefaults.textButtonColors(
                            backgroundColor = Green80,
                        ),
                        onClick = {
                            acceptTeamRequest(request._id!!, token, "approve") { result ->
                                if (result != null) {
                                    Toast.makeText(context, "Pedido aceito com sucesso!", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "Erro ao aceitar pedido.", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    ) {
                        Text(
                            color = Color.White,
                            text = "Aceitar"
                        )
                    }
                    Button(
                        colors = ButtonDefaults.textButtonColors(
                            backgroundColor = Color.Red,
                        ),
                        onClick = {
                            acceptTeamRequest(request._id!!, token, "") { result ->
                                if (result != null) {
                                    Toast.makeText(context, "Pedido recusado.", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "Erro ao recusar pedido.", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    ) {
                        Text(
                            color = Color.White,
                            text = "Recusar"
                        )
                    }

                } else if (request.status == "approved") {
                    Text("Aprovado!")
                } else {
                    Text("Recusado!")
                }
            }
        }
    }
}
fun getTeamRequests(teamId: String, authHeader: String, callback: (List<RequestModel>?) -> Unit) {
    val call = ApiClient.apiService.getTeamRequests(teamId, "Bearer: $authHeader")
    apiCall(call, callback)
}

fun acceptTeamRequest(requestId: String, authHeader: String, action: String, callback: (RequestModel?) -> Unit){
    val actionBody = AcceptRequestBody(
        action
    )
    val call = ApiClient.apiService.acceptTeamRequest(requestId, actionBody, "Bearer: $authHeader")
    apiCall(call, callback)
}