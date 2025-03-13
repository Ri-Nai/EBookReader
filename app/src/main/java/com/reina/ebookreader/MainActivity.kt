package com.reina.ebookreader

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.reina.ebookreader.components.ThemeSwitch
import com.reina.ebookreader.navigation.AppNavHost
import com.reina.ebookreader.navigation.AppDestinations
import com.reina.ebookreader.ui.theme.EBookReaderTheme
import com.reina.ebookreader.viewmodel.ThemeViewModel
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
            val followSystem by themeViewModel.followSystem.collectAsState()
            val systemInDarkTheme = isSystemInDarkTheme()
            
            Log.d(TAG, "MainActivity: isDarkTheme=$isDarkTheme, followSystem=$followSystem, systemInDarkTheme=$systemInDarkTheme")
            
            // 根据设置决定是否使用深色主题
            val useDarkTheme = themeViewModel.shouldUseDarkTheme(systemInDarkTheme)
            Log.d(TAG, "MainActivity: 最终使用的主题 useDarkTheme=$useDarkTheme")
            
            EBookReaderTheme(darkTheme = useDarkTheme) {
                val navController = rememberNavController()
                
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { Text("电子书阅读器") },
                            actions = {
                                // 设置按钮
                                IconButton(
                                    onClick = { 
                                        Log.d(TAG, "点击设置按钮")
                                        navController.navigate(AppDestinations.SETTINGS_ROUTE) 
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "设置"
                                    )
                                }
                                
                                // 主题切换按钮
                                ThemeSwitch(
                                    isDarkTheme = useDarkTheme,
                                    followSystem = followSystem,
                                    onToggleDarkMode = { 
                                        Log.d(TAG, "MainActivity: 切换深色模式")
                                        themeViewModel.toggleDarkMode() 
                                    },
                                    onToggleFollowSystem = { 
                                        Log.d(TAG, "MainActivity: 切换跟随系统设置")
                                        themeViewModel.toggleFollowSystem() 
                                    }
                                )
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                ) { innerPadding ->
                    // 直接将 innerPadding 传递给 AppNavHost，避免嵌套 padding
                    AppNavHost(
                        navController = navController,
                        innerPadding = innerPadding
                    )
                }
            }
        }
    }
}
