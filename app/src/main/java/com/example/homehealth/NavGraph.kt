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
import com.example.homehealth.screens.chat.ChatScreen
import com.example.homehealth.screens.profile.ProfileScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.remember
import com.example.homehealth.viewmodels.AuthViewModel
import com.example.homehealth.screens.admin.adminGraph
import com.example.homehealth.screens.appointment.AppointmentDetailsScreen
import com.example.homehealth.screens.appointment.BrowseCaretakerScreen
import com.example.homehealth.screens.profile.EditProfileScreen
import com.example.homehealth.viewmodels.IndexViewModel
import com.example.homehealth.viewmodels.ProfileViewModel


@Composable
fun NavGraph(
    navController: NavHostController
){
    NavHost(
        navController = navController,
        startDestination = "auth_graph"
    ){
        navigation(startDestination = "login_screen", route = "auth_graph") {
            composable("register_screen"){
                RegisterScreen(navController)
            }

            composable("login_screen") { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(navController.graph.id)
                }
                val authViewModel: AuthViewModel = viewModel(parentEntry)

                LoginScreen(navController, authViewModel)
            }
        }

        // Public (Patient) landing
        composable("index_screen") { backStackEntry ->
            val rootEntry = remember(backStackEntry) {
                navController.getBackStackEntry(navController.graph.id)
            }
            val indexViewModel: IndexViewModel = viewModel(rootEntry)
            val authViewModel: AuthViewModel = viewModel(rootEntry)

            IndexScreen(navController, indexViewModel, authViewModel)
        }

        // Profile Screen
        composable("profile_screen") { backStackEntry ->
            val rootEntry = remember(backStackEntry) {
                navController.getBackStackEntry(navController.graph.id)
            }
            val profileViewModel: ProfileViewModel = viewModel(rootEntry)
            val authViewModel: AuthViewModel = viewModel(rootEntry)

            ProfileScreen(navController, profileViewModel, authViewModel)
        }

        composable("edit_profile_screen") { backStackEntry ->
            val rootEntry = remember(backStackEntry) {
                navController.getBackStackEntry(navController.graph.id)
            }
            val profileViewModel: ProfileViewModel = viewModel(rootEntry)
            val authViewModel: AuthViewModel = viewModel(rootEntry)

            EditProfileScreen(navController, profileViewModel, authViewModel)
        }

        // Browsing made by patient/public
        composable("browse_caretaker_screen/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")!!
            BrowseCaretakerScreen(navController, userId)
        }

        composable("appointment_details_screen/{appointmentId}/{userId}") { backStackEntry ->
            val appointmentId = backStackEntry.arguments?.getString("appointmentId")!!
            val userId = backStackEntry.arguments?.getString("userId")!!
            AppointmentDetailsScreen(navController, appointmentId, userId)
        }

        // Scheduling made by patient/public
        composable("schedule_screen/{userId}/{caretakerId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")!!
            val caretakerId = backStackEntry.arguments?.getString("caretakerId")!!
            ScheduleScreen(navController, userId, caretakerId)
        }

        // Chat
        navigation(startDestination = "chatlist_screen/{userId}", route = "chat_graph"){
            composable("chatlist_screen/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId")!!
                ChatListScreen(navController, userId)
            }

            composable("chat_screen/{userId}/{chatId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId")!!
                val chatId = backStackEntry.arguments?.getString("chatId")!!
                ChatScreen(navController, userId, chatId)
            }
        }

        // ADMIN GRAPH
        adminGraph(navController)
    }
}