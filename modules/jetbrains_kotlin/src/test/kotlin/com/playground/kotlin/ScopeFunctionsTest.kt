package com.playground.kotlin

import org.assertj.core.api.Assertions.assertThat
import java.util.Properties
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ScopeFunctionsTest {

    @DisplayName("Also function can be used to setup objects")
    @Test
    fun testAlso() {
        val apiProperties = Properties().also {
            it.setProperty("API_KEY", "41913ac3b7f300629b3f6d8766ebc777")
            it.setProperty("API_URL", "https://api.example.com/beta/")
            it.setProperty("API_VERSION", "1")
        }

        assertThat(apiProperties.keys).hasSize(3)
        assertThat(apiProperties.keys).containsAll(listOf("API_KEY", "API_URL", "API_VERSION"))
    }

    @DisplayName("Apply function can be used to setup and return objects")
    @Test
    fun testApply() {
        val properties = Properties().apply {
            put("message", "Hello World")
        }

        assertThat(properties["message"]).isEqualTo("Hello World")
    }

    @DisplayName("Let function can be used to avoid an if not null")
    @Test
    fun testLet() {
        val username: String? = "not null but it could be so better check its nullability"

        username?.let { assertThat(username).isNotNull() }
    }

    @DisplayName("With function can be used to avoid repeat variables name")
    @Test
    fun testWith() {
        val messageBuilder = with(StringBuilder()) {
            append("Hello ")
            append("Idiomatic ")
            append("World!")
        }

        assertThat(messageBuilder.toString()).isEqualTo("Hello Idiomatic World!")
    }
}
