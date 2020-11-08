package com.playground.jackson.enums

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class EnumSampleTest {

    private val mapper = jacksonObjectMapper().apply {
        enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
        enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
    }

    @DisplayName("A JSON string can be mapped to an object with enums")
    @Test
    fun testStringToObject() {
        val json = """{
            "content": "Buy a blue pen",
            "status": "Doing"
        }"""

        val (content, status) = mapper.readValue<Task>(json)

        assertThat(content).isEqualTo("Buy a blue pen")
        assertThat(status).isEqualTo(Status.Doing)
    }

    @DisplayName("An object with enums can be mapped to JSON string")
    @Test
    fun testObjectToString() {
        val enum = Task(
                content = "Write a Jackson sample in Kotlin",
                status = Status.Done)

        val jsonString = mapper.writeValueAsString(enum)

        assertThat(jsonString).isEqualTo("""{"content":"Write a Jackson sample in Kotlin","status":"Done"}""")
    }

    @DisplayName("Enums can have default values if not informed")
    @Test
    fun testDefaultValues() {
        val json = """{ "content": "Send an e-mail to John" }"""

        val (content, status) = mapper.readValue<Task>(json)

        assertThat(content).isEqualTo("Send an e-mail to John")
        assertThat(status).isEqualTo(Status.Todo) // Defined in the data class
    }

    @DisplayName("Enums can be expressed as lowercase by overriding toString")
    @Test
    fun testOverridingToString() {
        val json = """{
            "content": "Change Enum serialization behavior",
            "status": "todo"
        }"""

        val (content, status) = mapper.readValue<Task>(json)

        assertThat(content).isEqualTo("Change Enum serialization behavior")
        assertThat(status).isEqualTo(Status.Todo)
    }
}
