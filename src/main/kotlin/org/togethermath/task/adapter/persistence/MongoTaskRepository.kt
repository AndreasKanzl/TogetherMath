package org.togethermath.task.adapter.persistence

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface MongoTaskRepository : MongoRepository<TaskDocument, String>
