package com.uri.bolanope.activities.admin

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.Typeface
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.uri.bolanope.R
import com.uri.bolanope.activities.common.CardAdmin
import com.uri.bolanope.activities.tourney.getAllTourneys
import com.uri.bolanope.components.PieChartData
import com.uri.bolanope.components.PieChart
import com.uri.bolanope.components.TopBar
import com.uri.bolanope.model.TeamModel
import com.uri.bolanope.model.TourneyModel
import com.uri.bolanope.model.UserModel

@Composable
fun AdminDashboard(navController: NavHostController) {
    val context = LocalContext.current

    val regularUsers = remember { mutableStateOf<List<UserModel>?>(null) }
    val adminUsers = remember { mutableStateOf<List<UserModel>?>(null) }
    val teacherUsers = remember { mutableStateOf<List<UserModel>?>(null) }
    val tourneys = remember { mutableStateOf<List<TourneyModel>?>(null) }

    val options = hashMapOf(
        "user" to "Usuário Normal",
        "admin" to "Admin",
        "professor" to "Professor"
    )

    LaunchedEffect(options) {
        options.forEach { (key, value) ->
            getAllUsersByRole(key) { result ->
                when (key) {
                    "user" -> regularUsers.value = result
                    "admin" -> adminUsers.value = result
                    "professor" -> teacherUsers.value = result
                }
            }
        }

        getAllTourneys { result ->
            tourneys.value = result
        }
    }


    val pieChartTeamsByTourney = tourneys.value?.map { tourney ->
        PieChartData(tourney.name, tourney.id_teams.size.toFloat())
    } ?: emptyList()

    Scaffold(
        topBar = { TopBar("Dashboard") },
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding(),
    ) { innerPadding ->
        Column {
            Row {
                CardAdmin(navController, icon = Icons.Default.Person, "Usuários por tipo", "users")
            }
            //if (pieChartDataUsers.isNotEmpty()) {
            //        Box(modifier = Modifier.padding(16.dp)) {
            //            PieChart("Usuários por tipo", pieChartDataUsers, context)
            //        }
            //}
            if (pieChartTeamsByTourney.isNotEmpty()) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        PieChart("Times por Torneio", pieChartTeamsByTourney, context)
                    }
            }
        }
    }
}