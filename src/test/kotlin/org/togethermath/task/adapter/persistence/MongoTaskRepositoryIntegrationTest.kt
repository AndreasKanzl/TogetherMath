package org.togethermath.task.adapter.persistence

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.togethermath.config.MongoConfig

@DataMongoTest
@Testcontainers
@Import(MongoConfig::class, ObjectMapper::class)
class MongoTaskRepositoryIntegrationTest {

    @Autowired
    private lateinit var mongoTaskRepository: MongoTaskRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

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
        val descNode = objectMapper.readTree("{\"text\":\"Test Task\"}")
        val stepDesc = objectMapper.readTree("{\"text\":\"Desc 1\"}")
        val stepHelp = objectMapper.readTree("{\"text\":\"Help 1\"}")
        val taskStep = TaskStepDocument(name = "Step 1", description = stepDesc, help = stepHelp)
        val task = TaskDocument(name = "First title", description = descNode, steps = listOf(taskStep))

        // When
        val savedTask = mongoTaskRepository.save(task)
        val foundTask = mongoTaskRepository.findById(savedTask.id!!)

        // Then
        assertTrue(foundTask.isPresent)
        assertEquals("First title", foundTask.get().name)
        assertEquals(descNode, foundTask.get().description)
        assertEquals(1, foundTask.get().steps.size)
        assertEquals("Step 1", foundTask.get().steps[0].name)
        assertEquals(stepDesc, foundTask.get().steps[0].description)
        assertEquals(stepHelp, foundTask.get().steps[0].help)
    }

    @Test
    fun `should save and find a task with structured object description`() {
        // Given
        val descriptionJson = """
            {
                "time": 1792332125895,
                "blocks": [
                    {"id": "wgRsxl7O1I", "type": "paragraph", "data": {"text": "Test description Test"}}
                ],
                "version": "2.31.0-rc.7"
            }
        """.trimIndent()
        val descriptionNode = objectMapper.readTree(descriptionJson)
        val task = TaskDocument(name = "EditorJS Task", description = descriptionNode)

        // When
        val savedTask = mongoTaskRepository.save(task)
        val foundTask = mongoTaskRepository.findById(savedTask.id!!)

        // Then
        assertTrue(foundTask.isPresent)
        assertEquals("EditorJS Task", foundTask.get().name)
        val retrievedDescription = foundTask.get().description!!
        assertEquals(1792332125895L, retrievedDescription.get("time").asLong())
        assertEquals("2.31.0-rc.7", retrievedDescription.get("version").asText())
        assertEquals("wgRsxl7O1I", retrievedDescription.get("blocks").get(0).get("id").asText())
    }

    @Test
    fun `should delete a task`() {
        // Given
        val task = TaskDocument(description = objectMapper.readTree("{\"text\":\"To be deleted\"}"))
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
        mongoTaskRepository.save(TaskDocument(description = objectMapper.readTree("{\"text\":\"Task 1\"}")))
        mongoTaskRepository.save(TaskDocument(description = objectMapper.readTree("{\"text\":\"Task 2\"}")))

        // When
        val tasks = mongoTaskRepository.findAll()

        // Then
        assertEquals(2, tasks.size)
    }
}
