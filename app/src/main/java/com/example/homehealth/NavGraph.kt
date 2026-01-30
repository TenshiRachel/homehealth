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
import com.example.homehealth.screens.CaretakerLandingScreen
import com.example.homehealth.viewmodels.AuthViewModel
import com.example.homehealth.screens.admin.adminGraph
import com.example.homehealth.screens.appointment.AppointmentDetailsScreen
import com.example.homehealth.screens.appointment.BrowseCaretakerScreen
import com.example.homehealth.screens.appointment.CaretakerDetailsScreen
import com.example.homehealth.screens.profile.CaretakerProfileScreen
import com.example.homehealth.screens.profile.EditCaretakerProfileScreen
import com.example.homehealth.screens.profile.EditProfileScreen
import com.example.homehealth.viewmodels.CaretakerViewModel
import com.example.homehealth.viewmodels.ChatListViewModel
import com.example.homehealth.viewmodels.ChatViewModel
import com.example.homehealth.viewmodels.IndexViewModel
import com.example.homehealth.viewmodels.ProfileViewModel
import com.example.homehealth.viewmodels.ScheduleViewModel
import com.example.homehealth.viewmodels.SkillViewModel


@Composable
fun NavGraph(
    navController: NavHostController
){
    NavHost(
        navController = navController,
        startDestination = "auth_graph"
    ){
        navigation(startDestination = "login_screen", route = "auth_graph") {
            composable("register_screen") { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(navController.graph.id)
                }
                val authViewModel: AuthViewModel = viewModel(parentEntry)

                RegisterScreen(navController, authViewModel)
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

        composable("caretaker_profile_screen") { backStackEntry ->
            val rootEntry = remember(backStackEntry) {
                navController.getBackStackEntry(navController.graph.id)
            }
            val caretakerViewModel: CaretakerViewModel = viewModel(rootEntry)
            val authViewModel: AuthViewModel = viewModel(rootEntry)

            CaretakerProfileScreen(navController, caretakerViewModel, authViewModel)
        }

        composable("edit_profile_screen") { backStackEntry ->
            val rootEntry = remember(backStackEntry) {
                navController.getBackStackEntry(navController.graph.id)
            }
            val profileViewModel: ProfileViewModel = viewModel(rootEntry)
            val authViewModel: AuthViewModel = viewModel(rootEntry)

            EditProfileScreen(navController, profileViewModel, authViewModel)
        }

        composable("edit_caretaker_profile_screen") { backStackEntry ->
            val rootEntry = remember(backStackEntry) {
                navController.getBackStackEntry(navController.graph.id)
            }
            val caretakerViewModel : CaretakerViewModel = viewModel(rootEntry)
            val skillViewModel : SkillViewModel = viewModel(rootEntry)

            EditCaretakerProfileScreen(navController, caretakerViewModel, skillViewModel)
        }

        // Browsing made by patient/public
        composable("browse_caretaker_screen") { backStackEntry ->
            val rootEntry = remember(backStackEntry) {
                navController.getBackStackEntry(navController.graph.id)
            }

            val scheduleViewModel: ScheduleViewModel = viewModel(rootEntry)
            val authViewModel: AuthViewModel = viewModel(rootEntry)
            BrowseCaretakerScreen(navController, authViewModel, scheduleViewModel)
        }

        composable("caretaker_details_screen/{caretakerId}") { backStackEntry ->
            val rootEntry = remember(backStackEntry) {
                navController.getBackStackEntry(navController.graph.id)
            }

            val caretakerId = backStackEntry.arguments?.getString("caretakerId")!!
            val caretakerViewModel: CaretakerViewModel = viewModel(rootEntry)
            val authViewModel: AuthViewModel = viewModel(rootEntry)
            CaretakerDetailsScreen(navController, caretakerId, authViewModel, caretakerViewModel)
        }

        composable("appointment_details_screen/{appointmentId}") { backStackEntry ->
            val rootEntry = remember(backStackEntry) {
                navController.getBackStackEntry(navController.graph.id)
            }

            val appointmentId = backStackEntry.arguments?.getString("appointmentId")!!
            val scheduleViewModel: ScheduleViewModel = viewModel(rootEntry)
            val authViewModel: AuthViewModel = viewModel(rootEntry)
            AppointmentDetailsScreen(navController, appointmentId, authViewModel, scheduleViewModel)
        }

        // Scheduling made by patient/public
        composable("schedule_screen/{caretakerId}") { backStackEntry ->
            val rootEntry = remember(backStackEntry) {
                navController.getBackStackEntry(navController.graph.id)
            }

            val caretakerId = backStackEntry.arguments?.getString("caretakerId")!!
            val scheduleViewModel: ScheduleViewModel = viewModel(rootEntry)
            val authViewModel: AuthViewModel = viewModel(rootEntry)
            ScheduleScreen(navController, caretakerId, authViewModel, scheduleViewModel)
        }

        // Caretaker landing
        composable("caretaker_landing_screen") { backStackEntry ->
            val rootEntry = remember(backStackEntry) {
                navController.getBackStackEntry(navController.graph.id)
            }
            val indexViewModel: IndexViewModel = viewModel(rootEntry)
            val authViewModel: AuthViewModel = viewModel(rootEntry)

            CaretakerLandingScreen(navController, indexViewModel, authViewModel)
        }

        // Chat
        navigation(startDestination = "chatlist_screen", route = "chat_graph"){
            composable("chatlist_screen") { backStackEntry ->
                val rootEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(navController.graph.id)
                }
                val chatListViewModel: ChatListViewModel = viewModel(rootEntry)
                val authViewModel: AuthViewModel = viewModel(rootEntry)
                ChatListScreen(navController, chatListViewModel, authViewModel)
            }

            composable("chat_screen/{chatId}") { backStackEntry ->
                val chatId = backStackEntry.arguments?.getString("chatId")!!
                val rootEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(navController.graph.id)
                }
                val chatViewModel: ChatViewModel = viewModel(rootEntry)
                val authViewModel: AuthViewModel = viewModel(rootEntry)
                ChatScreen(navController, chatId, chatViewModel, authViewModel)
            }
        }

        // ADMIN GRAPH
        adminGraph(navController)
    }
}