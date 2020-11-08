package com.playground.jackson.enums

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue

enum class Status {
    @JsonEnumDefaultValue
    Todo,
    Doing,
    Done;

    override fun toString() = name.toLowerCase()
}

data class Task(
    val content: String = "",
    val status: Status = Status.Todo
)
