package org.togethermath.task.adapter.persistence

import com.fasterxml.jackson.databind.JsonNode
import org.togethermath.task.domain.model.TaskStep

data class TaskStepDocument(
    val id: String? = null,
    val name: String? = null,
    val description: JsonNode? = null,
    val help: JsonNode? = null
) {
    fun toDomain(): TaskStep = TaskStep(id, name, description, help)

    companion object {
        fun fromDomain(taskStep: TaskStep): TaskStepDocument =
            TaskStepDocument(taskStep.id, taskStep.name, taskStep.description, taskStep.help)
    }
}
