package com.playground.kotlin

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@Suppress("ConstantConditionIf")
class BranchesTest {

    @DisplayName("If-else can be used to assign a value")
    @Test
    fun testIfElseReturn() {
        val hello = if (true) "Hello" else "World"
        val world = if (false) "Hello" else "World"

        assertThat(hello).isEqualTo("Hello")
        assertThat(world).isEqualTo("World")
    }

    @DisplayName("When can be used with a condition")
    @Test
    fun testWhenCondition() {
        val message = "Hello"

        when (message.length == 5) {
            true -> assertThat(message).isEqualTo("Hello")
            false -> fail("The word should be `Hello`")
        }

        when (message.length == 5) {
            true -> assertThat(message).isEqualTo("Hello")
            else -> fail("The word should be `Hello`")
        }
    }

    @DisplayName("When can be used to assign a value")
    @Test
    fun testWhenAssign() {
        val hello = when (true) {
            true -> "Hello"
            else -> "World"
        }

        val world = when (false) {
            true -> "Hello"
            else -> "World"
        }

        assertThat(hello).isEqualTo("Hello")
        assertThat(world).isEqualTo("World")
    }

    @DisplayName("When can retain a value")
    @Test
    fun testWhenRetain() {
        val getMessage = { condition: Boolean ->
            if (condition) "Hello" else "WORLD"
        }

        val hello = when (val result = getMessage(true)) {
            "Hello" -> result.toUpperCase()
            else -> result.toLowerCase()
        }
        val world = when (val result = getMessage(false)) {
            "Hello" -> result.toUpperCase()
            else -> result.toLowerCase()
        }

        assertThat(hello).isEqualTo("HELLO")
        assertThat(world).isEqualTo("world")
    }
}
