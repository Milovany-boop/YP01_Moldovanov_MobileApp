package com.example.collegeschedule_moldovanov.data.repository
import com.example.collegeschedule_moldovanov.data.api.ScheduleApi
import com.example.collegeschedule_moldovanov.data.dto.ScheduleByDateDto

class ScheduleRepository(private val api: ScheduleApi) {

    suspend fun loadSchedule(group: String, start: String, end: String): List<ScheduleByDateDto> {
        return api.getSchedule(group, start, end)
    }
    suspend fun loadGroups(): List<String> {
        return api.getGroups()
    }
}