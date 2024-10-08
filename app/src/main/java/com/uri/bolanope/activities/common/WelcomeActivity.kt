package com.uri.bolanope.activities.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.uri.bolanope.R
import com.uri.bolanope.utils.SharedPreferencesManager

@Composable
fun Welcome(navController: NavHostController) {
    val context = LocalContext.current
    val userId = SharedPreferencesManager.getUserId(context)
    val userRole = SharedPreferencesManager.getUserRole(context)

    // agora so precisa mudar essa var pra skippar a tela de welcome
    val skipLogin = true

    if (!userId.isNullOrEmpty() && skipLogin) {
        if(userRole == "admin") {
            navController.navigate("homeAdmin")
        }else {

            navController.navigate("home")
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {

            Image(
                painter = painterResource(id = R.drawable.ic_logo_bolanope),
                contentDescription = "Logo Bola no Pé",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 200.dp)
                    .size(150.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Button(onClick = { navController.navigate("login") }) {
                    Text("Entrar")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Não possui conta? Clique aqui e cadastre-se",
                    style = TextStyle(fontSize = 16.sp),
                    modifier = Modifier
                        .clickable { navController.navigate("user") }
                        .padding(8.dp)
                )
            }
        }
    }
}