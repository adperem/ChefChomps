// ThemeManager.kt
package com.example.chefchomps.ui

import ChefChompsTema
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf

val LocalDarkTheme = compositionLocalOf { mutableStateOf(false) }

object ThemeManager {
    private const val PREFS_NAME = "ChefChompsPrefs"
    private const val KEY_DARK_THEME = "dark_theme"

    fun initialize(context: Context) {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedDarkTheme = sharedPref.getBoolean(KEY_DARK_THEME, false)
        isDarkTheme.value = savedDarkTheme
    }

    fun setDarkTheme(enabled: Boolean, context: Context) {
        isDarkTheme.value = enabled
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_DARK_THEME, enabled)
            .apply()
    }

    val isDarkTheme = mutableStateOf(false)
}

@Composable
fun ChefChompsAppTheme(content: @Composable () -> Unit) {
    val darkTheme = ThemeManager.isDarkTheme.value
    CompositionLocalProvider(LocalDarkTheme provides ThemeManager.isDarkTheme) {
        ChefChompsTema(darkTheme = darkTheme) {
            content()
        }
    }
}