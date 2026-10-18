package org.togethermath.task.domain.model

import com.fasterxml.jackson.databind.JsonNode

data class TaskStep(
    val id: String? = null,
    val name: String? = null,
    val description: JsonNode? = null,
    val help: JsonNode? = null
)
