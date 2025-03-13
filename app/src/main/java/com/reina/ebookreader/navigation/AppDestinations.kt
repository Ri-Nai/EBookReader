package com.reina.ebookreader.navigation

import android.net.Uri

// 导航路由
object AppDestinations {
    const val HOME_ROUTE = "home"
    const val BOOK_DETAIL_ROUTE = "bookDetail/{bookId}"
    const val SETTINGS_ROUTE = "settings"
    
    fun bookDetailRoute(bookId: String): String {
        // 确保ID不包含特殊字符，可能会影响URL解析
        return "bookDetail/${Uri.encode(bookId)}"
    }
} 
