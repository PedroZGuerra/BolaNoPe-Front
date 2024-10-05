package com.uri.bolanope.activities.common


import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.uri.bolanope.R
import com.uri.bolanope.activities.field.base64ToBitmap
import com.uri.bolanope.activities.field.getAllFields
import com.uri.bolanope.components.BottomNavigationBar
import com.uri.bolanope.model.FieldModel

@Composable
fun HomePage(navController: NavHostController) {
    val context = LocalContext.current
    val fields = remember { mutableStateOf<List<FieldModel>?>(null) }

    LaunchedEffect(Unit) {
        getAllFields { result ->
            if (result != null) {
                fields.value = result
            } else {
                Toast.makeText(context, "Falha ao carregar os campos.", Toast.LENGTH_LONG).show()
            }
        }
    }

    val pagerState = rememberPagerState(pageCount = {fields.value?.size ?:0})

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_logo_bolanope),
                contentDescription = "Logo Bola no Pé",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .size(150.dp)
            )
            Text(
                text = "Nossas quadras:",
                modifier = Modifier
                    .padding(top = 16.dp, start = 8.dp)
                    .align(Alignment.Start)
            )
            if(fields.value == null){
                Box(
                    Modifier
                        .height(200.dp)
                )
            }
            fields.value?.let { fieldList ->
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) { page ->
                    val field = fields.value!![page]

                    Card(
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        val imageBase64 = field.image.toString()
                        val bitmap = base64ToBitmap(imageBase64)
                        bitmap?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = "Field ${field.name}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable {
                                        navController.navigate("reserveField/${field._id}")
                                    }
                            )

                        }
                    }
                }
            }
            Row (
                modifier = Modifier.fillMaxWidth()
            ){
                Card(
                    onClick = {
                        navController.navigate("exploreTeams")
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(Modifier.height(16.dp))
                        Icon(
                            imageVector = Icons.Filled.Groups,
                            contentDescription = "Times",
                            tint = Color.Black,
                            modifier = Modifier.size(48.dp)
                        )
                        Text("Times")
                        Spacer(Modifier.height(16.dp))
                    }
                }
                Card(
                    onClick = {
                        navController.navigate("exploreTourneys")
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(Modifier.height(16.dp))
                        Icon(
                            imageVector = Icons.Filled.EmojiEvents,
                            contentDescription = "Torneios",
                            tint = Color.Black,
                            modifier = Modifier.size(48.dp)
                        )
                        Text("Torneios")
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }
    }


}
