package org.togethermath.task.adapter.persistence

import org.springframework.stereotype.Component
import org.togethermath.task.domain.model.Task
import org.togethermath.task.domain.port.TaskRepositoryPort
import java.util.*

@Component
class MongoTaskAdapter(private val mongoRepository: MongoTaskRepository) : TaskRepositoryPort {
    override fun findAll(): List<Task> =
        mongoRepository.findAll().map { it.toDomain() }

    override fun findById(id: String): Optional<Task> =
        mongoRepository.findById(id).map { it.toDomain() }

    override fun save(task: Task): Task =
        mongoRepository.save(TaskDocument.fromDomain(task)).toDomain()

    override fun deleteById(id: String) {
        mongoRepository.deleteById(id)
    }

    override fun existsById(id: String): Boolean =
        mongoRepository.existsById(id)
}
