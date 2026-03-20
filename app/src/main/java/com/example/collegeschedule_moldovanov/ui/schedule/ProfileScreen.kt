package com.example.collegeschedule_moldovanov.ui.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.collegeschedule_moldovanov.data.prefs.SettingsManager
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(settingsManager: SettingsManager) {
    var currentTheme by remember { mutableStateOf("system") }
    val scope = rememberCoroutineScope()

    // Подписываемся на изменения темы
    LaunchedEffect(Unit) {
        settingsManager.themeFlow.collect { theme ->
            currentTheme = theme
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Настройки", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Тема оформления")

        Row {
            listOf("light", "dark", "system").forEach { theme ->
                val isSelected = currentTheme == theme
                Button(
                    onClick = {
                        scope.launch {
                            settingsManager.saveTheme(theme)
                        }
                    },
                    modifier = Modifier.padding(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text(theme)
                }
            }
        }
    }
}