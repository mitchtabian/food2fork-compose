package com.codingwithmitch.food2forkcompose.presentation

import android.app.Application
import androidx.compose.runtime.dispatch.AndroidUiDispatcher.Companion.Main
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class BaseApplication : Application(){

    @Inject
    lateinit var dataStore: DataStore<Preferences>

    private val preferencesScope = CoroutineScope(Main)

    private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme_key")

    val isDark = mutableStateOf(false)

    override fun onCreate() {
        super.onCreate()
        dataStore.data.onEach { preferences ->
            preferences[DARK_THEME_KEY]?.let { isDarkTheme ->
                isDark.value = isDarkTheme
            }
        }.launchIn(preferencesScope)
    }

    fun toggleTheme(){
        preferencesScope.launch {
            dataStore.edit { preferences ->
                val current = preferences[DARK_THEME_KEY]?: false
                preferences[DARK_THEME_KEY] = !current
            }
        }
    }

}














