package com.reina.ebookreader.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.reina.ebookreader.screens.BookDetailScreen
import com.reina.ebookreader.screens.HomeScreen
import com.reina.ebookreader.screens.SettingsScreen
import android.net.Uri
import androidx.compose.ui.unit.dp

@Composable
fun AppNavHost(
    navController: NavHostController,
    innerPadding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = AppDestinations.HOME_ROUTE,
        modifier = Modifier.padding(
            top = innerPadding.calculateTopPadding(),
            bottom = 0.dp,
            start = 0.dp,
            end = 0.dp
        )
    ) {
        composable(AppDestinations.HOME_ROUTE) {
            HomeScreen(navController = navController)
        }
        composable(
            AppDestinations.BOOK_DETAIL_ROUTE,
            arguments = listOf(navArgument("bookId") { type = NavType.StringType })
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
            BookDetailScreen(
                bookId = bookId,
                onBackClick = { navController.navigateUp() }
            )
        }
        composable(AppDestinations.SETTINGS_ROUTE) {
            SettingsScreen(
                onBackClick = { navController.navigateUp() }
            )
        }
    }
}
