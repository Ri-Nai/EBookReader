package com.reina.ebookreader.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.reina.ebookreader.data.ThemePreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

private const val TAG = "ThemeViewModel"

class ThemeViewModel(application: Application) : AndroidViewModel(application) {
    
    private val themePreferences = ThemePreferences(application.applicationContext)
    
    // 是否使用深色主题
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()
    
    // 是否跟随系统设置
    private val _followSystem = MutableStateFlow(true)
    val followSystem: StateFlow<Boolean> = _followSystem.asStateFlow()
    
    init {
        // 加载保存的主题设置
        viewModelScope.launch {
            combine(
                themePreferences.isDarkMode,
                themePreferences.followSystem
            ) { isDark, followSys ->
                Pair(isDark, followSys)
            }.collect { (isDark, followSys) ->
                Log.d(TAG, "初始化主题设置: isDark=$isDark, followSys=$followSys")
                _isDarkTheme.value = isDark
                _followSystem.value = followSys
            }
        }
    }
    
    // 切换深色/浅色主题
    fun toggleDarkMode() {
        viewModelScope.launch {
            val newValue = !_isDarkTheme.value
            Log.d(TAG, "切换深色模式: $newValue")
            _isDarkTheme.value = newValue
            themePreferences.setDarkMode(newValue)
            
            // 如果用户手动切换主题，则不再跟随系统
            if (_followSystem.value) {
                Log.d(TAG, "不再跟随系统设置")
                _followSystem.value = false
                themePreferences.setFollowSystem(false)
            }
        }
    }
    
    // 切换是否跟随系统设置
    fun toggleFollowSystem() {
        viewModelScope.launch {
            val newValue = !_followSystem.value
            Log.d(TAG, "切换跟随系统设置: $newValue")
            _followSystem.value = newValue
            themePreferences.setFollowSystem(newValue)
        }
    }
    
    // 获取当前应该使用的主题模式
    fun shouldUseDarkTheme(isSystemInDarkMode: Boolean): Boolean {
        val result = if (_followSystem.value) {
            isSystemInDarkMode
        } else {
            _isDarkTheme.value
        }
        Log.d(TAG, "计算主题模式: followSystem=${_followSystem.value}, isSystemInDarkMode=$isSystemInDarkMode, isDarkTheme=${_isDarkTheme.value}, 结果=$result")
        return result
    }
} 
