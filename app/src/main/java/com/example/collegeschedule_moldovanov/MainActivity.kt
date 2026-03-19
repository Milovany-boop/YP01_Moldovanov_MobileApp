package com.example.collegeschedule_moldovanov

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.example.collegeschedule_moldovanov.data.api.ScheduleApi
import com.example.collegeschedule_moldovanov.data.network.RetrofitInstance
import com.example.collegeschedule_moldovanov.data.repository.ScheduleRepository
import com.example.collegeschedule_moldovanov.ui.AppDestinations
import com.example.collegeschedule_moldovanov.ui.schedule.ScheduleScreen
import com.example.collegeschedule_moldovanov.ui.theme.CollegeSchedule_MoldovanovTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CollegeSchedule_MoldovanovTheme {
                CollegeScheduleApp()
            }
        }
    }
}

@Composable
fun CollegeScheduleApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    // Инициализация Retrofit и репозитория (один раз)
    val retrofit = remember {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5281/") // убедитесь, что порт совпадает с вашим API
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val api = remember { retrofit.create(ScheduleApi::class.java) }
    val repository = remember { ScheduleRepository(api) }

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
                AppDestinations.HOME -> ScheduleScreen(repository)
                AppDestinations.FAVORITES -> Text("Экран избранного (будет реализован позже)")
                AppDestinations.PROFILE -> Text("Экран профиля (будет реализован позже)")
            }
        }
    }
}