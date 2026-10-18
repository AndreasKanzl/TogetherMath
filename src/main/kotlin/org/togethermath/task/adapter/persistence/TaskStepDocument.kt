package org.togethermath.task.adapter.persistence

import org.togethermath.task.domain.model.TaskStep

data class TaskStepDocument(
    val id: String? = null,
    val name: String,
    val description: String,
    val help: String
) {
    fun toDomain(): TaskStep = TaskStep(id, name, description, help)

    companion object {
        fun fromDomain(taskStep: TaskStep): TaskStepDocument =
            TaskStepDocument(taskStep.id, taskStep.name, taskStep.description, taskStep.help)
    }
}
