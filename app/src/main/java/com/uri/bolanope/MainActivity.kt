package com.uri.bolanope

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.uri.bolanope.ui.theme.BolaNoPeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BolaNoPeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Home(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Home(name: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { onClickLogin(context) }) {
            Text("Entrar")
        }

        Button(onClick = { onClickSignUp(context) }) {
            Text("Cadastrar")
        }

        Button(onClick = { onClickUpdate(context) }) {
            Text("Editar")
        }

    }
}

fun onClickLogin(context: Context){
    val intent = Intent(context, LoginActivity::class.java)
    context.startActivity(intent)
}

fun onClickSignUp(context: Context){
    val intent = Intent(context, UserProfileActivity()::class.java).apply {
        putExtra("ACTIVITY_MODE", "CREATE")
    }
    context.startActivity(intent)
}

fun onClickUpdate(context: Context){
    val intent = Intent(context, UserProfileActivity()::class.java).apply {
        putExtra("ACTIVITY_MODE", "UPDATE")
        putExtra("USER_ID", "66da077268f93a0d34cd038a")
    }
    context.startActivity(intent)
}

