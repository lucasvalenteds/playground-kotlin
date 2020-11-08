package com.playground.kotlin

import org.assertj.core.api.Assertions.assertThat
import java.io.File
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class InputOutputTest {

    private val file = File("src/test/resources/example.md")

    @DisplayName("It has a function to simulate try-with-resources")
    @Test
    fun testUseFunction() {
        val firstLine = file.bufferedReader().useLines { it.iterator().next() }

        assertThat(firstLine).isNotEmpty()
        assertThat(firstLine).startsWith("#")
    }

    @DisplayName("It has functions to consume the content of a file")
    @Test
    fun testReadFunction() {
        assertThat(file.readLines()).isNotEmpty()
        assertThat(file.readText()).isNotEmpty()
        assertThat(file.readBytes()).isNotEmpty()
    }
}
