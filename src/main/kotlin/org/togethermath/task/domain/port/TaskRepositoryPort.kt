package org.togethermath.task.domain.port

import org.togethermath.task.domain.model.Task
import java.util.*

interface TaskRepositoryPort {
    fun findAll(): List<Task>
    fun findById(id: String): Optional<Task>
    fun save(task: Task): Task
    fun deleteById(id: String)
    fun existsById(id: String): Boolean
}
