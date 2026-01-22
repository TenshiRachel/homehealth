package com.example.homehealth.fragments

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController

@Composable
fun BottomNavBar(navController: NavHostController, userId: String, role: String){
    NavigationBar(containerColor = Color.Cyan) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = navController.currentDestination?.route == "index_screen",
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color.Blue.copy(alpha = 0.5f),
                unselectedIconColor = Color.Black,
                unselectedTextColor = Color.Black,
                indicatorColor = Color.Blue.copy(alpha = 0.5f)
            ),
            onClick = {
                navController.navigate("index_screen") {
                    launchSingleTop = true
                }
            }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.ChatBubble, contentDescription = "Chats") },
            label = { Text("Chats") },
            selected = navController.currentDestination?.route == "chatlist_screen/$userId",
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color.Blue.copy(alpha = 0.5f),
                unselectedIconColor = Color.Black,
                unselectedTextColor = Color.Black,
                indicatorColor = Color.Blue.copy(alpha = 0.5f)
            ),
            onClick = {
                    navController.navigate("chatlist_screen/$userId"){
                    launchSingleTop = true
                }
            }
        )

        if (role == "caretaker"){
            NavigationBarItem(
                icon = { Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = "Requests") },
                label = { Text("Requests") },
                selected = navController.currentDestination?.route == "requests_screen/$userId",
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.Blue.copy(alpha = 0.5f),
                    unselectedIconColor = Color.Black,
                    unselectedTextColor = Color.Black,
                    indicatorColor = Color.Blue.copy(alpha = 0.5f)
                ),
                onClick = {
                    navController.navigate("requests_screen/$userId") {
                        launchSingleTop = true
                    }
                }
            )
        }

        NavigationBarItem(
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = navController.currentDestination?.route == "profile_screen",
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color.Blue.copy(alpha = 0.5f),
                unselectedIconColor = Color.Black,
                unselectedTextColor = Color.Black,
                indicatorColor = Color.Blue.copy(alpha = 0.5f)
            ),
            onClick = {
                navController.navigate("profile_screen") {
                    launchSingleTop = true
                }
            }
        )
    }
}