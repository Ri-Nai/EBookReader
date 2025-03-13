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
import android.net.Uri

@Composable
fun AppNavHost(
    navController: NavHostController,
    innerPadding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = AppDestinations.HOME_ROUTE,
        modifier = Modifier.padding(innerPadding)
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
    }
}

// 导航路由
object AppDestinations {
    const val HOME_ROUTE = "home"
    const val BOOK_DETAIL_ROUTE = "bookDetail/{bookId}"
    
    fun bookDetailRoute(bookId: String): String {
        // 确保ID不包含特殊字符，可能会影响URL解析
        return "bookDetail/${Uri.encode(bookId)}"
    }
}
