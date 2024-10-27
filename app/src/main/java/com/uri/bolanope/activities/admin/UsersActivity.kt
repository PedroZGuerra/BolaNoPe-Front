package com.uri.bolanope.activities.admin

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.uri.bolanope.components.TopBar
import com.uri.bolanope.model.UserModel
import com.uri.bolanope.services.ApiClient
import com.uri.bolanope.services.apiCall


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun UsersActivity(navController: NavHostController) {
    val users = remember { mutableStateOf<List<UserModel>?>(null) }

    Scaffold(
        topBar = { TopBar("Usuários") },
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding(),
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            RoleDropdown(users)

            users.value?.forEach { user ->
                UserCard(user)
            }
        }
    }
}

@Composable
fun RoleDropdown(users: MutableState<List<UserModel>?>) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("Selecione uma opção") }

    Column(Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .shadow(4.dp)
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text(
                text = selectedOption,
                modifier = Modifier.weight(1f)
            )
            Text(text = "▼")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            val options = hashMapOf(
                "user" to "Usuário Normal",
                "admin" to "Admin",
                "professor" to "Professor"
            )
            options.forEach { (key, value) ->
                DropdownMenuItem(
                    text = { Text(value) },
                    onClick = {
                        selectedOption = value
                        expanded = false
                        getAllUsersByRole(key) { result ->
                            users.value = result
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun UserCard(userModel: UserModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Nome: ${userModel.name}")
            Text("Email: ${userModel.email}")
        }
    }
}

fun getAllUsersByRole(role: String, callback: (List<UserModel>?) -> Unit) {
    val call = ApiClient.apiService.getAllUsersByRole(role)
    apiCall(call, callback)
}