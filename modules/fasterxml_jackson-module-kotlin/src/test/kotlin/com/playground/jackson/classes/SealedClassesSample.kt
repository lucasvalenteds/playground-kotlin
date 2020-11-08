package com.playground.jackson.classes

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class SealedClassesSample {

    private val mapper = jacksonObjectMapper()

    @DisplayName("A JSON string can be mapped to a sealed class instance")
    @Test
    fun testStringToSealedClass() {
        val json = """{
            "result": "success",
            "content": "Success happened"}
        """

        val (result, content) = mapper.readValue<Message>(json)

        assertThat(result).isEqualTo(Result.Success)
        assertThat(content).isEqualTo("Success happened")
    }

    @DisplayName("A sealed class instance can be mapped to JSON string")
    @Test
    fun testSealedClassToString() {
        val sealedClass = Message(Result.Failure, "Too much work")

        val json = mapper.writeValueAsString(sealedClass)

        assertThat(json).isEqualTo("""{"result":"failure","content":"Too much work"}""")
    }
}
