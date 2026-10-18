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
        val tasks = listOf(Task("1", "Task 1"), Task("2", "Task 2"))
        `when`(taskService.getAll()).thenReturn(tasks)

        mockMvc.perform(get("/api/tasks"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value("1"))
            .andExpect(jsonPath("$[0].description").value("Task 1"))
            .andExpect(jsonPath("$[1].id").value("2"))
            .andExpect(jsonPath("$[1].description").value("Task 2"))
    }

    @Test
    fun `should get task by id`() {
        val task = Task("1", "Task 1")
        `when`(taskService.getById("1")).thenReturn(Optional.of(task))

        mockMvc.perform(get("/api/tasks/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.description").value("Task 1"))
    }

    @Test
    fun `should return 404 when task not found`() {
        `when`(taskService.getById("1")).thenReturn(Optional.empty())

        mockMvc.perform(get("/api/tasks/1"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should create task`() {
        val task = Task(description = "New Task")
        val createdTask = Task("1", "New Task")
        `when`(taskService.create(task)).thenReturn(createdTask)

        mockMvc.perform(
            post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.description").value("New Task"))
    }

    @Test
    fun `should update task`() {
        val task = Task(description = "Updated Task")
        val updatedTask = Task("1", "Updated Task")
        `when`(taskService.update("1", task)).thenReturn(Optional.of(updatedTask))

        mockMvc.perform(
            put("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.description").value("Updated Task"))
    }

    @Test
    fun `should return 404 when updating non-existent task`() {
        val task = Task(description = "Updated Task")
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
