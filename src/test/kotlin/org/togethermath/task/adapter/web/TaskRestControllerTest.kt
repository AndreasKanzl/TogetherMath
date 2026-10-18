package org.togethermath.task.adapter.web

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.togethermath.config.SecurityConfig
import org.togethermath.task.application.TaskService
import org.togethermath.task.domain.model.Task
import org.togethermath.task.domain.model.TaskStep
import java.util.*

@WebMvcTest(TaskRestController::class)
@Import(SecurityConfig::class)
class TaskRestControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var taskService: TaskService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `should get all tasks`() {
        val desc1 = objectMapper.readTree("{\"text\":\"Task 1\"}")
        val desc2 = objectMapper.readTree("{\"text\":\"Task 2\"}")
        val tasks = listOf(Task(id = "1", description = desc1), Task(id = "2", description = desc2))
        `when`(taskService.getAll()).thenReturn(tasks)

        mockMvc.perform(get("/api/tasks"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value("1"))
            .andExpect(jsonPath("$[0].description.text").value("Task 1"))
            .andExpect(jsonPath("$[1].id").value("2"))
            .andExpect(jsonPath("$[1].description.text").value("Task 2"))
    }

    @Test
    fun `should get task by id`() {
        val desc = objectMapper.readTree("{\"text\":\"Task 1\"}")
        val task = Task(id = "1", description = desc)
        `when`(taskService.getById("1")).thenReturn(Optional.of(task))

        mockMvc.perform(get("/api/tasks/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.description.text").value("Task 1"))
    }

    @Test
    fun `should return 404 when task not found`() {
        `when`(taskService.getById("1")).thenReturn(Optional.empty())

        mockMvc.perform(get("/api/tasks/1"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should create task`() {
        val desc = objectMapper.readTree("{\"text\":\"New Task\"}")
        val task = Task(name = "First title", description = desc)
        val createdTask = Task("1", "First title", desc)
        `when`(taskService.create(task)).thenReturn(createdTask)

        mockMvc.perform(
            post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.name").value("First title"))
            .andExpect(jsonPath("$.description.text").value("New Task"))
    }

    @Test
    fun `should create task with structured description object`() {
        val payload = """
            {
                "steps": [],
                "description": {
                    "time": 1792332125895,
                    "blocks": [
                        {"id": "wgRsxl7O1I", "type": "paragraph", "data": {"text": "Test description Test"}},
                        {"id": "xKfMAyDaGQ", "type": "math", "data": {"type": "cdx-math-success", "latex": "7\\sum_{n\\mathop{=}0}^{\\infty}6\\times+\\lambda"}}
                    ],
                    "version": "2.31.0-rc.7"
                },
                "name": "First title"
            }
        """.trimIndent()

        val expectedDescription = objectMapper.readTree("""
            {
                "time": 1792332125895,
                "blocks": [
                    {"id": "wgRsxl7O1I", "type": "paragraph", "data": {"text": "Test description Test"}},
                    {"id": "xKfMAyDaGQ", "type": "math", "data": {"type": "cdx-math-success", "latex": "7\\sum_{n\\mathop{=}0}^{\\infty}6\\times+\\lambda"}}
                ],
                "version": "2.31.0-rc.7"
            }
        """.trimIndent())
        val expectedTask = Task(
            name = "First title",
            description = expectedDescription,
            steps = emptyList()
        )
        val createdTask = Task(
            id = "generated-id",
            name = "First title",
            description = expectedDescription,
            steps = emptyList()
        )
        `when`(taskService.create(expectedTask)).thenReturn(createdTask)

        mockMvc.perform(
            post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value("generated-id"))
            .andExpect(jsonPath("$.name").value("First title"))
            .andExpect(jsonPath("$.description.time").value(1792332125895L))
            .andExpect(jsonPath("$.description.blocks[0].id").value("wgRsxl7O1I"))
            .andExpect(jsonPath("$.description.blocks[1].data.latex").value("7\\sum_{n\\mathop{=}0}^{\\infty}6\\times+\\lambda"))
    }

    @Test
    fun `should update task`() {
        val desc = objectMapper.readTree("{\"text\":\"Updated Task\"}")
        val task = Task(name = "Updated Title", description = desc)
        val updatedTask = Task(id = "1", name = "Updated Title", description = desc)
        `when`(taskService.update("1", task)).thenReturn(Optional.of(updatedTask))

        mockMvc.perform(
            put("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.name").value("Updated Title"))
            .andExpect(jsonPath("$.description.text").value("Updated Task"))
    }

    @Test
    fun `should return 404 when updating non-existent task`() {
        val desc = objectMapper.readTree("{\"text\":\"Updated Task\"}")
        val task = Task(description = desc)
        `when`(taskService.update("1", task)).thenReturn(Optional.empty())

        mockMvc.perform(
            put("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should allow cors preflight and include cors headers`() {
        mockMvc.perform(
            options("/api/tasks")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "GET")
        )
            .andExpect(status().isOk)
            .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"))

        `when`(taskService.getAll()).thenReturn(emptyList())

        mockMvc.perform(
            get("/api/tasks")
                .header("Origin", "http://localhost:3000")
        )
            .andExpect(status().isOk)
            .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"))
    }

    @Test
    fun `should delete task`() {
        `when`(taskService.delete("1")).thenReturn(true)

        mockMvc.perform(delete("/api/tasks/1"))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `should return 404 when deleting non-existent task`() {
        `when`(taskService.delete("1")).thenReturn(false)

        mockMvc.perform(delete("/api/tasks/1"))
            .andExpect(status().isNotFound)
    }
}
