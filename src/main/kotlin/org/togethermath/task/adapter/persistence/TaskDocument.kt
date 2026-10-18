package org.togethermath.task.adapter.persistence

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.togethermath.task.domain.model.Task

@Document(collection = "tasks")
data class TaskDocument(
    @Id
    val id: String? = null,
    val name: String? = null,
    val description: JsonNode? = null,
    val steps: List<TaskStepDocument> = emptyList()
) {
    fun toDomain(): Task = Task(
        id = id,
        name = name,
        description = description,
        steps = steps.map { it.toDomain() }
    )

    companion object {
        fun fromDomain(task: Task): TaskDocument = TaskDocument(
            id = task.id,
            name = task.name,
            description = task.description,
            steps = task.steps.map { TaskStepDocument.fromDomain(it) }
        )
    }
}
