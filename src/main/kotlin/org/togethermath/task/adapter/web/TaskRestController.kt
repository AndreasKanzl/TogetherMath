package org.togethermath.task.adapter.web

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.togethermath.task.application.TaskService
import org.togethermath.task.domain.model.Task

@RestController
@RequestMapping("/api/tasks")
class TaskRestController(private val taskService: TaskService) {

    @GetMapping
    fun getAll(): List<Task> = taskService.getAll()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: String): Task =
        taskService.getById(id).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody task: Task): Task = taskService.create(task)

    @PutMapping("/{id}")
    fun update(@PathVariable id: String, @RequestBody task: Task): Task =
        taskService.update(id, task).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: String) {
        if (!taskService.delete(id)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
    }
}
