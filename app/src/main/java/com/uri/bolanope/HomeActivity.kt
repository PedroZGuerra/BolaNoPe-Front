package com.uri.bolanope

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uri.bolanope.ui.theme.BolaNoPeTheme

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val userId = intent.getStringExtra("USER_ID")

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BolaNoPeTheme {
                if (userId != null) {
                    HomePage(userId)
                }
            }
        }
    }
}

@Composable
fun HomePage(userId: String) {
    Scaffold (
        bottomBar = { BottomNavigationBar(userId) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "home show de bola",
                modifier = Modifier.align(Alignment.CenterHorizontally)

            )
        }
    }
}
