package com.example.volkswagendemo.core.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.volkswagendemo.ui.screen.BatteryScreen
import com.example.volkswagendemo.ui.screen.HomeScreen
import com.example.volkswagendemo.ui.screen.InventoryScreen
import com.example.volkswagendemo.ui.screen.SearchScreen
import com.example.volkswagendemo.ui.screen.SettingsScreen
import com.example.volkswagendemo.viewmodel.BatteryViewModel
import com.example.volkswagendemo.viewmodel.HomeViewModel
import com.example.volkswagendemo.viewmodel.InventoryViewModel
import com.example.volkswagendemo.viewmodel.LocationViewModel
import com.example.volkswagendemo.viewmodel.SearchViewModel
import com.example.volkswagendemo.viewmodel.SettingsViewModel

@Composable
fun NavigationWrapper() {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Home) {

        composable<Home> {
            val homeViewModel = hiltViewModel<HomeViewModel>()
            val locationViewModel = hiltViewModel<LocationViewModel>()
            HomeScreen(
                homeViewModel,
                locationViewModel,
                { navController.navigate(Inventory) },
                { navController.navigate(Search) },
                { navController.navigate(Battery) },
                { navController.navigate(Settings) }
            )
        }

        composable<Inventory> {
            val inventoryViewModel = hiltViewModel<InventoryViewModel>()
            InventoryScreen(inventoryViewModel) {
                navController.navigate(Home) {
                    popUpTo<Home> { inclusive = true }
                }
            }
        }

        composable<Search> {
            val searchViewModel = hiltViewModel<SearchViewModel>()
            SearchScreen(searchViewModel) {
                navController.navigate(Home) {
                    popUpTo<Home> { inclusive = true }
                }
            }
        }

        composable<Battery> {
            val batteryViewModel = hiltViewModel<BatteryViewModel>()
            BatteryScreen(batteryViewModel) {
                navController.navigate(Home) {
                    popUpTo<Home> { inclusive = true }
                }
            }
        }

        composable<Settings> {
            val settingsViewModel = hiltViewModel<SettingsViewModel>()
            SettingsScreen(settingsViewModel) {
                navController.navigate(Home) {
                    popUpTo<Home> { inclusive = true }
                }
            }
        }
    }
}