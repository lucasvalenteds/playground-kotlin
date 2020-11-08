package com.playground.kotlin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class TuplesTest {

    @DisplayName("Pair wraps two values into one")
    @Test
    fun testPair() {
        val (age, thirtyFive) = Pair("age", 35)
        assertThat(age).isEqualTo("age")
        assertThat(thirtyFive).isEqualTo(35)

        val pair = "Never gonna" to "give you up"
        assertThat(pair.first).isEqualTo("Never gonna")
        assertThat(pair.second).isEqualTo("give you up")

        assertThat("message" to "Hello World").isEqualTo(Pair("message", "Hello World"))
    }

    @DisplayName("Triple wraps three values into one")
    @Test
    fun testTriple() {
        val letters = Triple("a", "b", "c")
        assertThat(letters.first).isEqualTo("a")
        assertThat(letters.second).isEqualTo("b")
        assertThat(letters.third).isEqualTo("c")

        val (one, two, three) = Triple("one", 2, 3.0)
        assertThat(one).isEqualTo("one")
        assertThat(two).isEqualTo(2)
        assertThat(three).isEqualTo(3.0)
    }
}
