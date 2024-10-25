package com.uri.bolanope.activities.user

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController

@Composable
fun ChangePasswordActivity(navController: NavHostController, userId: String?) {
    var password by remember { mutableStateOf("") }

}
