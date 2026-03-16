package com.example.collegeschedule_moldovanov

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
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
    // Состояние текущей вкладки (сохраняется при повороте)
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    // Инициализация Retrofit и репозитория (один раз за время жизни композиции)
    val retrofit = remember {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5281/") // убедитесь, что порт совпадает с вашим API
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val api = remember { retrofit.create(ScheduleApi::class.java) }
    val repository = remember { ScheduleRepository(api) }

    // Адаптивный навигационный контейнер
    NavigationSuiteScaffold(
        navigationSuiteItems = {
            // Создаём пункты меню для каждого значения enum
            AppDestinations.entries.forEach { destination ->
                item(
                    icon = { androidx.compose.material3.Icon(destination.icon, contentDescription = destination.title) },
                    label = { Text(destination.title) },
                    selected = currentDestination == destination,
                    onClick = { currentDestination = destination }
                )
            }
        }
    ) {
        // Содержимое выбранной вкладки
        when (currentDestination) {
            AppDestinations.HOME -> ScheduleScreen(repository)
            AppDestinations.FAVORITES -> Text("Экран избранного (будет реализован позже)")
            AppDestinations.PROFILE -> Text("Экран профиля (будет реализован позже)")
        }
    }
}