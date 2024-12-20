package com.uri.bolanope

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.internal.composableLambda
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
//import com.google.android.libraries.places.api.Places
import com.uri.bolanope.activities.admin.AdminDashboard
import com.uri.bolanope.activities.admin.MostReservedTimes
import com.uri.bolanope.activities.admin.MostReservedTimesFieldList
import com.uri.bolanope.activities.admin.StudentsByTeacher
import com.uri.bolanope.activities.admin.TeamsByTourney
import com.uri.bolanope.activities.admin.UsersActivity
import com.uri.bolanope.activities.common.HomeAdmin
import com.uri.bolanope.activities.common.HomePage
import com.uri.bolanope.activities.common.Welcome
import com.uri.bolanope.activities.field.Field
import com.uri.bolanope.activities.field.FieldHistory
import com.uri.bolanope.activities.field.FieldMap
import com.uri.bolanope.activities.field.Fields
import com.uri.bolanope.activities.field.ReserveField
import com.uri.bolanope.activities.teacher.CreateTeacherActivity
import com.uri.bolanope.activities.teacher.RegisterStudentActivity
import com.uri.bolanope.activities.teacher.TeacherActivity
import com.uri.bolanope.activities.team.CreateTeam
import com.uri.bolanope.activities.team.EditTeam
import com.uri.bolanope.activities.team.ExploreTeams
import com.uri.bolanope.activities.team.MyTeams
import com.uri.bolanope.activities.team.Team
import com.uri.bolanope.activities.team.TeamRequests
import com.uri.bolanope.activities.tourney.CreateTourney
import com.uri.bolanope.activities.tourney.EditTourney
import com.uri.bolanope.activities.tourney.ExploreTourneys
import com.uri.bolanope.activities.tourney.Tourney
import com.uri.bolanope.activities.user.ChangePasswordActivity
import com.uri.bolanope.activities.user.Login
import com.uri.bolanope.activities.user.NotificationPage
import com.uri.bolanope.activities.user.UserProfile
import com.uri.bolanope.services.NotificationWorker
import com.uri.bolanope.ui.theme.BolaNoPeTheme
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Places.initialize(this, "AIzaSyA0sAr05_vxaA0wSY7A6kSMYEINuR1lkqI")
        val context = this

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    context,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            }
        }

        val notificationWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(15, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(this).enqueue(notificationWorkRequest)

        enableEdgeToEdge()
        setContent {
            BolaNoPeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "welcome") {
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

                        composable(
                            route = "changePassword/{id}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id")
                            ChangePasswordActivity(navController, id)
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

                        composable("myTeams/{isLeader}") {
                        }
                        composable(
                            route = "myTeams/{isLeader}",
                            arguments = listOf(
                                navArgument("isLeader") { type = NavType.BoolType }
                            )
                        ) { backStackEntry ->
                            val isLeader = backStackEntry.arguments?.getBoolean("isLeader")
                            MyTeams(navController, isLeader!!)
                        }

                        composable(
                            route = "team/{id}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id")
                            Team(navController, id)
                        }

                        composable("createTeam") {
                            CreateTeam(navController)
                        }

                        composable(
                            route = "editTeam/{id}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id")
                            EditTeam(navController, id)
                        }

                        composable(
                            route = "teamRequests/{id}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id")
                            TeamRequests(navController, id!!)
                        }

                        composable("exploreTourneys") {
                            ExploreTourneys(navController)
                        }

                        composable("createTourney") {
                            CreateTourney(navController)
                        }

                        composable(
                            route = "tourney/{id}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id")
                            Tourney(id!!, navController)
                        }
                        composable(
                            route = "editTourney/{id}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id")
                            EditTourney(navController, id!!)
                        }
                        composable(
                            route = "teacher/{id}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id")
                            TeacherActivity(navController, id!!)
                        }
                        composable("createTeacher") {
                            CreateTeacherActivity(navController)
                        }
                        composable("adminDashboard") {
                            AdminDashboard(navController)
                        }
                        composable("users") {
                            UsersActivity(navController)
                        }
                        composable("notifications") {
                            NotificationPage(navController)
                        }

                        composable (
                            route = "registerStudent/{id}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.StringType }
                            )
                            ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id")
                            RegisterStudentActivity(navController, id!!)
                        }

                        composable("teamsByTourney") {
                            TeamsByTourney(navController)
                        }
                        composable (
                            route = "most-reserved-times/{id}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id")
                            MostReservedTimes(navController, id!!)
                        }
                        composable("most-reserved-times-fields-list") {
                            MostReservedTimesFieldList(navController)
                        }
                        composable("studentsByTeacher") {
                            StudentsByTeacher(navController)
                        }
                        composable (
                            route = "fieldMap/{location}/{name}",
                            arguments = listOf(
                                navArgument("location") { type = NavType.StringType },
                                navArgument("name") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val location = backStackEntry.arguments?.getString("location")
                            val name = backStackEntry.arguments?.getString("name")
                            FieldMap(navController, location!!, name!!)
                        }
                    }
                }
            }
        }
    }
}

