package com.uri.bolanope

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.uri.bolanope.ui.theme.BolaNoPeTheme
import com.uri.bolanope.utils.SharedPreferencesManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BolaNoPeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "welcome" ) {
                        composable("welcome") {
                            Welcome(navController)
                        }
                        composable("login") {
                            Login(navController)
                        }
                        composable("home") {
                            HomePage(navController)
                        }
                        composable("user") {
                            UserProfile(navController, null)
                        }
                        composable("reserveField") {
                            ReserveField(navController, null)
                        }
                        composable(
                            "reserveField/{id}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.StringType }
                            )

                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id")
                            ReserveField(navController, id)
                        }
                        composable(
                            route = "user/{id}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id")
                            UserProfile(navController, id)
                        }
                        composable("fields") {
                            Fields(navController)
                        }
                        composable("field") {
                            Field(navController, null)
                        }
                        composable(
                            route = "field/{id}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id")
                            Field(navController, id)
                        }
                        composable(
                            route = "fieldHistory/{id}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id")
                            FieldHistory(navController, id)
                        }
                        composable("homeAdmin") {
                            HomeAdmin(navController)
                        }

                        composable("exploreTeams") {
                            ExploreTeams(navController)
                        }
                    }
                }
            }
        }
    }
}

