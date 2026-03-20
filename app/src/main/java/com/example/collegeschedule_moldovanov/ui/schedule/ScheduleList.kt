package com.example.collegeschedule_moldovanov.ui.schedule
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.collegeschedule_moldovanov.data.dto.LessonGroupPart
import com.example.collegeschedule_moldovanov.data.dto.ScheduleByDateDto

@Composable
fun ScheduleList(schedule: List<ScheduleByDateDto>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(schedule) { day ->
            DayHeader(date = day.lessonDate, weekday = day.weekday)
            day.lessons.forEach { lesson ->
                LessonCard(lesson = lesson)
            }
        }
    }
}

@Composable
fun DayHeader(date: String, weekday: String) {
    Text(
        text = "$weekday, $date",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun LessonCard(lesson: com.example.collegeschedule_moldovanov.data.dto.LessonDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = when (lesson.lessonNumber % 3) {
                        0 -> Icons.Default.Computer
                        1 -> Icons.Default.School
                        else -> Icons.Default.MenuBook
                    },
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Пара ${lesson.lessonNumber} (${lesson.time})",
                    style = MaterialTheme.typography.titleSmall
                )
            }

            lesson.groupParts.forEach { (part, detail) ->
                if (detail != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    // Цвет корпуса
                    val buildingColor = when (detail.building) {
                        "Главный корпус" -> Color(0xFF6200EE)
                        "Учебный корпус №2" -> Color(0xFF03DAC5)
                        "Лабораторный корпус" -> Color(0xFFBB86FC)
                        else -> Color.Gray
                    }
                    Row(verticalAlignment = Alignment.Top) {
                        Text(
                            text = if (part == LessonGroupPart.FULL) "Общая" else part.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.width(60.dp)
                        )
                        Column {
                            Text(
                                text = detail.subject,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            )
                            Text(
                                text = "${detail.teacher} (${detail.teacherPosition})",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = buildingColor
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${detail.classroom} (${detail.building})",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = buildingColor
                                )
                            }
                        }
                    }
                } else {
                    Text("Нет информации о занятии")
                }
            }
        }
    }
}