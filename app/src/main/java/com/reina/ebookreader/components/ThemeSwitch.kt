package com.reina.ebookreader.components

import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private const val TAG = "ThemeSwitch"

/**
 * 主题切换按钮
 * 
 * 简化的主题切换组件，点击图标直接切换深色/浅色模式
 * 
 * @param isDarkTheme 当前是否为深色主题
 * @param onToggleDarkMode 切换深色模式的回调
 */
@Composable
fun ThemeSwitch(
    isDarkTheme: Boolean,
    followSystem: Boolean,
    onToggleDarkMode: () -> Unit,
    onToggleFollowSystem: () -> Unit
) {
    Log.d(TAG, "ThemeSwitch: isDarkTheme=$isDarkTheme, followSystem=$followSystem")
    
    // 简化为单个图标按钮，点击直接切换主题
    IconButton(
        onClick = { 
            Log.d(TAG, "ThemeSwitch: 点击切换主题按钮")
            onToggleDarkMode() 
        }
    ) {
        Icon(
            imageVector = if (isDarkTheme) Icons.Outlined.DarkMode else Icons.Outlined.LightMode,
            contentDescription = if (isDarkTheme) "切换到浅色模式" else "切换到深色模式"
        )
    }
} 
