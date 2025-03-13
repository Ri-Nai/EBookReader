package com.reina.ebookreader.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val TAG = "ThemePreferences"

// 为应用创建一个单例的 DataStore 实例
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ThemePreferences(private val context: Context) {
    
    // 定义偏好键
    companion object {
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        val FOLLOW_SYSTEM = booleanPreferencesKey("follow_system")
    }
    
    // 获取当前的深色模式状态
    val isDarkMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            val value = preferences[IS_DARK_MODE] ?: false
            Log.d(TAG, "读取深色模式设置: $value")
            value
        }
    
    // 获取是否跟随系统设置
    val followSystem: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            val value = preferences[FOLLOW_SYSTEM] ?: true
            Log.d(TAG, "读取跟随系统设置: $value")
            value
        }
    
    // 设置深色模式
    suspend fun setDarkMode(isDark: Boolean) {
        Log.d(TAG, "设置深色模式: $isDark")
        context.dataStore.edit { preferences ->
            preferences[IS_DARK_MODE] = isDark
        }
    }
    
    // 设置是否跟随系统
    suspend fun setFollowSystem(follow: Boolean) {
        Log.d(TAG, "设置跟随系统: $follow")
        context.dataStore.edit { preferences ->
            preferences[FOLLOW_SYSTEM] = follow
        }
    }
} 
