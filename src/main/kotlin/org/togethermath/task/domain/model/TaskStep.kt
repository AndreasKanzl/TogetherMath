package org.togethermath.task.domain.model

data class TaskStep(
    val id: String? = null,
    val name: String,
    val description: String,
    val help: String
)
