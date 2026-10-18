package org.togethermath.task.domain.model

data class Task(
    val id: String? = null,
    val description: String,
    val steps: List<TaskStep> = emptyList()
)
