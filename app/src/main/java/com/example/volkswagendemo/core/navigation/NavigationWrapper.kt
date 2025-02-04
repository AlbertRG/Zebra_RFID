package com.example.volkswagendemo.core.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.volkswagendemo.ui.screen.HomeScreen
import com.example.volkswagendemo.ui.screen.InventoryScreen
import com.example.volkswagendemo.ui.screen.SearchScreen
import com.example.volkswagendemo.viewmodel.HomeViewModel
import com.example.volkswagendemo.viewmodel.InventoryViewModel

@Composable
fun NavigationWrapper() {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Home) {

        composable<Home> {
            val homeViewModel = hiltViewModel<HomeViewModel>()
            HomeScreen(
                homeViewModel,
                { navController.navigate(Inventory) },
                { navController.navigate(Search) })
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
            SearchScreen {
                navController.navigate(Home) {
                    popUpTo<Home> { inclusive = true }
                }
            }
        }

    }

}