package com.playground.hamkrest

import com.natpryce.hamkrest.Matcher
import com.natpryce.hamkrest.and
import com.natpryce.hamkrest.anyElement
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.endsWith
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.greaterThan
import com.natpryce.hamkrest.has
import com.natpryce.hamkrest.hasElement
import com.natpryce.hamkrest.hasSize
import com.natpryce.hamkrest.isEmpty
import com.natpryce.hamkrest.isIn
import com.natpryce.hamkrest.or
import com.natpryce.hamkrest.startsWith
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class MainTest {

    private data class Person(val name: String, val age: Int) {
        fun isAnAdult() = this.age >= 21
    }

    @DisplayName("Objects can be compared according to a criteria")
    @Test
    fun testLogic() {
        val john = Person("John", 22)

        assertThat(john.name, String::isNotEmpty)
        assertThat(john.name, has(String::length, equalTo(4)))
        assertThat(john.name, startsWith("J") and endsWith("n"))

        assertThat(john.age, isIn(18..25))
        assertThat(john.age, greaterThan(20))
    }

    @DisplayName("It can check size and items of an Iterable")
    @Test
    fun testList() {
        val dave = Person("Dave", 28)
        val amy = Person("Amy", 32)
        val ellen = Person("Ellen", 16)

        val guests = listOf(dave, amy, ellen)

        assertThat(guests, !isEmpty)
        assertThat(guests, hasSize(equalTo(3)))
        assertThat(guests, hasElement(dave) or hasElement(amy))
        assertThat(guests, anyElement(equalTo(ellen)))
    }

    @DisplayName("A custom matcher can be implemented")
    @Test
    fun testCustomMatcher() {
        val eva = Person("Eva", 47)
        val michael = Person("Michael", 12)

        val matchesRequiredAge = Matcher(Person::isAnAdult)

        assertThat(eva, Person::isAnAdult)
        assertThat(michael, !matchesRequiredAge)
    }
}
