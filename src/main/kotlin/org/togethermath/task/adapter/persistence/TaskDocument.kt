package org.togethermath.task.adapter.persistence

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.togethermath.task.domain.model.Task

@Document(collection = "tasks")
data class TaskDocument(
    @Id
    val id: String? = null,
    val description: String,
    val steps: List<TaskStepDocument> = emptyList()
) {
    fun toDomain(): Task = Task(
        id = id,
        description = description,
        steps = steps.map { it.toDomain() }
    )

    companion object {
        fun fromDomain(task: Task): TaskDocument = TaskDocument(
            id = task.id,
            description = task.description,
            steps = task.steps.map { TaskStepDocument.fromDomain(it) }
        )
    }
}
