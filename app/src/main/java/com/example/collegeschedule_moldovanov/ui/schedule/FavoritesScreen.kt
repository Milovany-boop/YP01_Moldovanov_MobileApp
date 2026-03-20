package com.example.collegeschedule_moldovanov.ui.schedule

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.collegeschedule_moldovanov.data.prefs.SettingsManager
import com.example.collegeschedule_moldovanov.data.repository.ScheduleRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    repository: ScheduleRepository,
    settingsManager: SettingsManager,
    onGroupSelected: (String) -> Unit
) {
    var favorites by remember { mutableStateOf<Set<String>>(emptySet()) }
    val scope = rememberCoroutineScope()

    // Подписываемся на изменения избранного
    LaunchedEffect(Unit) {
        settingsManager.favoritesFlow.collect { favs ->
            favorites = favs
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Избранное") }) }
    ) { paddingValues ->
        if (favorites.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Нет избранных групп")
            }
        } else {
            LazyColumn(modifier = Modifier.padding(paddingValues)) {
                items(favorites.toList()) { group ->
                    ListItem(
                        headlineContent = { Text(group) },
                        trailingContent = {
                            IconButton(onClick = {
                                scope.launch {
                                    settingsManager.removeFromFavorites(group)
                                }
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Удалить")
                            }
                        },
                        modifier = Modifier.clickable { onGroupSelected(group) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}