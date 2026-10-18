package org.togethermath.task.adapter.persistence

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@DataMongoTest
@Testcontainers
class MongoTaskRepositoryIntegrationTest {

    @Autowired
    private lateinit var mongoTaskRepository: MongoTaskRepository

    companion object {
        @Container
        val mongoDBContainer = MongoDBContainer("mongo:7.0")

        @JvmStatic
        @DynamicPropertySource
        fun setProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl)
        }
    }

    @BeforeEach
    fun setUp() {
        mongoTaskRepository.deleteAll()
    }

    @Test
    fun `should save and find a task`() {
        // Given
        val taskStep = TaskStepDocument(name = "Step 1", description = "Desc 1", help = "Help 1")
        val task = TaskDocument(description = "Test Task", steps = listOf(taskStep))

        // When
        val savedTask = mongoTaskRepository.save(task)
        val foundTask = mongoTaskRepository.findById(savedTask.id!!)

        // Then
        assertTrue(foundTask.isPresent)
        assertEquals("Test Task", foundTask.get().description)
        assertEquals(1, foundTask.get().steps.size)
        assertEquals("Step 1", foundTask.get().steps[0].name)
    }

    @Test
    fun `should delete a task`() {
        // Given
        val task = TaskDocument(description = "To be deleted")
        val savedTask = mongoTaskRepository.save(task)

        // When
        mongoTaskRepository.deleteById(savedTask.id!!)

        // Then
        val foundTask = mongoTaskRepository.findById(savedTask.id!!)
        assertTrue(foundTask.isEmpty)
    }

    @Test
    fun `should find all tasks`() {
        // Given
        mongoTaskRepository.save(TaskDocument(description = "Task 1"))
        mongoTaskRepository.save(TaskDocument(description = "Task 2"))

        // When
        val tasks = mongoTaskRepository.findAll()

        // Then
        assertEquals(2, tasks.size)
    }
}
