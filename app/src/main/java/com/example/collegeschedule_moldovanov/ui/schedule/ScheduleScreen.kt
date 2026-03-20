package com.example.collegeschedule_moldovanov.ui.schedule

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.collegeschedule_moldovanov.data.dto.ScheduleByDateDto
import com.example.collegeschedule_moldovanov.data.prefs.SettingsManager
import com.example.collegeschedule_moldovanov.data.repository.ScheduleRepository
import com.example.collegeschedule_moldovanov.utils.getWeekDateRange
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    repository: ScheduleRepository,
    settingsManager: SettingsManager,
    onOpenGroupSelection: () -> Unit
) {
    var selectedGroup by remember { mutableStateOf<String?>(null) }
    var schedule by remember { mutableStateOf<List<ScheduleByDateDto>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var isFavorite by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        settingsManager.selectedGroupFlow.collect { group ->
            selectedGroup = group
            if (group != null) {

                isFavorite = settingsManager.isFavorite(group)
                val (start, end) = getWeekDateRange()
                try {
                    schedule = repository.loadSchedule(group, start, end)
                } catch (e: Exception) {
                    error = e.message
                } finally {
                    loading = false
                }
            } else {
                loading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(selectedGroup ?: "Расписание") },
                actions = {
                    if (selectedGroup != null) {
                        IconButton(onClick = {
                            scope.launch {
                                if (isFavorite) {
                                    settingsManager.removeFromFavorites(selectedGroup!!)
                                    isFavorite = false
                                } else {
                                    settingsManager.addToFavorites(selectedGroup!!)
                                    isFavorite = true
                                }
                            }
                        }) {
                            Icon(
                                if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (isFavorite) "Удалить из избранного" else "Добавить в избранное"
                            )
                        }
                    }
                    IconButton(onClick = onOpenGroupSelection) {
                        Icon(Icons.Default.Search, contentDescription = "Выбрать группу")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when {
                selectedGroup == null -> {
                    Text("Группа не выбрана. Нажмите на значок поиска.")
                }
                loading -> CircularProgressIndicator()
                error != null -> Text("Ошибка: $error")
                else -> ScheduleList(schedule)
            }
        }
    }
}