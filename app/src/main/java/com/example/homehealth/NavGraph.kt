package com.example.homehealth

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.homehealth.screens.IndexScreen
import com.example.homehealth.screens.auth.LoginScreen
import com.example.homehealth.screens.appointment.ScheduleScreen
import com.example.homehealth.screens.auth.RegisterScreen
import com.example.homehealth.screens.chat.ChatListScreen
import com.example.homehealth.screens.profile.ProfileScreen


@Composable
fun NavGraph(
    navController: NavHostController
){
    NavHost(navController = navController, startDestination = "auth_graph"){
        navigation(startDestination = "login_screen", route = "auth_graph"){
            composable("register_screen"){ RegisterScreen(navController) }
            composable("login_screen"){ LoginScreen(navController) }
        }

        // Public(Patient) landing
        composable("index_screen/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")!!
            IndexScreen(navController, userId)
        }

        // Profile Screen
        composable("profile_screen/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")!!
            ProfileScreen(navController, userId)
        }

        composable("edit_profile_screen/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")!!
            com.example.homehealth.screens.profile.EditProfileScreen(navController, userId)
        }

        // 🔵 Caregiver landing
        composable("schedule_screen/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")!!
            ScheduleScreen(navController, userId)
        }

        navigation(startDestination = "chatlist_screen/{userId}", route = "chat_graph"){
            composable("chatlist_screen/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId")!!
                ChatListScreen(navController, userId)
            }
        }

//        composable("home_screen/{userId}") { backStackEntry ->
//            val userId = backStackEntry.arguments?.getString("userId") ?: ""
//            IndexScreen(navController, userId)
//        }
    }
}