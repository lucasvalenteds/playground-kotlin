package com.playground.kotlin

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class SequencesTest {

    @DisplayName("It is lazy populated and can be reused")
    @Test
    fun testReusable() {
        assertThat(emptySequence<String>().count()).isZero()

        val words = sequenceOf("flower", "basket", "garden")
        repeat(3) { assertThat(words.count()).isEqualTo(3) }
    }

    @DisplayName("It can be iterated using an iterator")
    @Test
    fun testIterator() {
        val words = sequenceOf("flower", "basket", "garden")

        val iterator = words.iterator()

        assertThat(iterator.next()).isEqualTo("flower")
        assertThat(iterator.next()).isEqualTo("basket")
        assertThat(iterator.next()).isEqualTo("garden")
        assertThat(iterator.hasNext()).isFalse()
        assertThatThrownBy { iterator.next() }.isInstanceOf(NoSuchElementException::class.java)
    }

    @DisplayName("It cannot be iterated if empty")
    @Test
    fun testIteratorEmpty() {
        assertThatThrownBy { emptySequence<Any>().iterator().next() }
            .isInstanceOf(NoSuchElementException::class.java)
    }

    @DisplayName("It can be used to create a generator based on coroutines")
    @Test
    fun test() = runBlocking {
        val generator = sequence {
            yield("0%")
            yield("25%")
            yield("50%")
            yield("75%")
            yield("100%")
        }

        val iterator = generator.iterator()
        while (iterator.hasNext()) {
            assertThat(iterator.next()).endsWith("%")
        }

        assertThat(iterator.hasNext()).isFalse()
    }
}
