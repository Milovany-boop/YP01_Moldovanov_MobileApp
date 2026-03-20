package com.example.collegeschedule_moldovanov

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.collegeschedule_moldovanov.data.api.ScheduleApi
import com.example.collegeschedule_moldovanov.data.prefs.SettingsManager
import com.example.collegeschedule_moldovanov.data.repository.ScheduleRepository
import com.example.collegeschedule_moldovanov.ui.AppDestinations
import com.example.collegeschedule_moldovanov.ui.schedule.*
import com.example.collegeschedule_moldovanov.ui.theme.CollegeSchedule_MoldovanovTheme
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CollegeScheduleApp()
        }
    }
}

@Composable
fun CollegeScheduleApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    var showGroupSelection by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val settingsManager = remember { SettingsManager(context) }

    val theme by settingsManager.themeFlow.collectAsState(initial = "system")
    val darkTheme = when (theme) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }

    val retrofit = remember {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5281/") // убедитесь, что порт совпадает с вашим API
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val api = remember { retrofit.create(ScheduleApi::class.java) }
    val repository = remember { ScheduleRepository(api) }
    val scope = rememberCoroutineScope()

    CollegeSchedule_MoldovanovTheme(darkTheme = darkTheme) {
        if (showGroupSelection) {
            GroupSelectionScreen(
                repository = repository,
                onGroupSelected = { group ->
                    scope.launch {
                        settingsManager.saveSelectedGroup(group)
                    }
                    showGroupSelection = false
                },
                onBack = { showGroupSelection = false }
            )
        } else {
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        AppDestinations.entries.forEach { destination ->
                            NavigationBarItem(
                                icon = { Icon(destination.icon, contentDescription = destination.title) },
                                label = { Text(destination.title) },
                                selected = currentDestination == destination,
                                onClick = { currentDestination = destination }
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    when (currentDestination) {
                        AppDestinations.HOME -> ScheduleScreen(
                            repository = repository,
                            settingsManager = settingsManager,
                            onOpenGroupSelection = { showGroupSelection = true }
                        )
                        AppDestinations.FAVORITES -> FavoritesScreen(
                            repository = repository,
                            settingsManager = settingsManager,
                            onGroupSelected = { group ->
                                currentDestination = AppDestinations.HOME
                                scope.launch {
                                    settingsManager.saveSelectedGroup(group)
                                }
                            }
                        )
                        AppDestinations.PROFILE -> ProfileScreen(settingsManager = settingsManager)
                    }
                }
            }
        }
    }
}