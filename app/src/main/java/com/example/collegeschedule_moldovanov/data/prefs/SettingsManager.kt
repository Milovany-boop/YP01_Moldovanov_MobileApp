package com.example.collegeschedule_moldovanov.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsManager(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        private val SELECTED_GROUP_KEY = stringPreferencesKey("selected_group")
        private val THEME_KEY = stringPreferencesKey("theme")
        private val FAVORITES_KEY = stringPreferencesKey("favorites")
    }


    val selectedGroupFlow: Flow<String?> = dataStore.data
        .map { preferences -> preferences[SELECTED_GROUP_KEY] }

    suspend fun saveSelectedGroup(groupName: String) {
        dataStore.edit { preferences ->
            preferences[SELECTED_GROUP_KEY] = groupName
        }
    }


    val themeFlow: Flow<String> = dataStore.data
        .map { preferences -> preferences[THEME_KEY] ?: "system" }

    suspend fun saveTheme(theme: String) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme
        }
    }


    val favoritesFlow: Flow<Set<String>> = dataStore.data
        .map { preferences ->
            preferences[FAVORITES_KEY]?.split(",")?.toSet() ?: emptySet()
        }

    suspend fun addToFavorites(group: String) {
        val current = favoritesFlow.first().toMutableSet()
        current.add(group)
        dataStore.edit { preferences ->
            preferences[FAVORITES_KEY] = current.joinToString(",")
        }
    }

    suspend fun removeFromFavorites(group: String) {
        val current = favoritesFlow.first().toMutableSet()
        current.remove(group)
        dataStore.edit { preferences ->
            preferences[FAVORITES_KEY] = current.joinToString(",")
        }
    }

    suspend fun isFavorite(group: String): Boolean {
        return favoritesFlow.first().contains(group)
    }
}