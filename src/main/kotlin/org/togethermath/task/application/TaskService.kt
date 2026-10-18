package org.togethermath.task.application

import org.springframework.stereotype.Service
import org.togethermath.task.domain.model.Task
import org.togethermath.task.domain.port.TaskRepositoryPort
import java.util.*

@Service
class TaskService(private val repositoryPort: TaskRepositoryPort) {
    fun getAll(): List<Task> = repositoryPort.findAll()

    fun getById(id: String): Optional<Task> = repositoryPort.findById(id)

    fun create(task: Task): Task = repositoryPort.save(task)

    fun update(id: String, task: Task): Optional<Task> {
        return if (repositoryPort.existsById(id)) {
            Optional.of(repositoryPort.save(task.copy(id = id)))
        } else {
            Optional.empty()
        }
    }

    fun delete(id: String): Boolean {
        return if (repositoryPort.existsById(id)) {
            repositoryPort.deleteById(id)
            true
        } else {
            false
        }
    }
}
