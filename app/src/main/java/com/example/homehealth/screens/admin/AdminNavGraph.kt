package com.example.homehealth.screens.admin

import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.NavGraphBuilder
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.remember
import com.example.homehealth.screens.admin.skill.CreateSkillScreen
import com.example.homehealth.screens.admin.skill.ManageSkillsScreen
import com.example.homehealth.viewmodels.AdminViewModel
import com.example.homehealth.viewmodels.AuthViewModel
import com.example.homehealth.viewmodels.SkillViewModel

fun NavGraphBuilder.adminGraph(navController: NavHostController) {
    navigation(
        route = "admin_graph",
        startDestination = "admin_dashboard_screen"
    ) {
        composable("admin_dashboard_screen") { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("admin_graph")
            }

            val adminViewModel: AdminViewModel = viewModel(parentEntry)
            val rootEntry = remember(backStackEntry) {
                navController.getBackStackEntry(navController.graph.id)
            }
            val authViewModel: AuthViewModel = viewModel(rootEntry)

            AdminDashboardScreen(
                navController = navController,
                adminViewModel = adminViewModel,
                authViewModel = authViewModel
            )
        }

        // Manage Skills Screen
        composable("create_skill_screen") { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("admin_graph")
            }

            val skillViewModel: SkillViewModel = viewModel(parentEntry)

            CreateSkillScreen(
                navController = navController,
                skillViewModel = skillViewModel
            )
        }

        composable("manage_skills_screen") { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("admin_graph")
            }

            val skillViewModel: SkillViewModel = viewModel(parentEntry)
            ManageSkillsScreen(
                navController = navController,
                skillViewModel = skillViewModel
            )
        }
    }
}