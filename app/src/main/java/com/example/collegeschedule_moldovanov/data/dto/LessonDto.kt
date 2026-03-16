package com.example.collegeschedule_moldovanov.data.dto
data class LessonDto(
    val lessonNumber: Int,
    val time: String,
    val groupParts: Map<LessonGroupPart, LessonPartDto>
)