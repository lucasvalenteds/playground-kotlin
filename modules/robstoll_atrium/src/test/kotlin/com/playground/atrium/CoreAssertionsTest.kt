package com.playground.atrium

import ch.tutteli.atrium.api.cc.en_GB.and
import ch.tutteli.atrium.api.cc.en_GB.atLeast
import ch.tutteli.atrium.api.cc.en_GB.contains
import ch.tutteli.atrium.api.cc.en_GB.containsNot
import ch.tutteli.atrium.api.cc.en_GB.containsStrictly
import ch.tutteli.atrium.api.cc.en_GB.hasSize
import ch.tutteli.atrium.api.cc.en_GB.inAnyOrder
import ch.tutteli.atrium.api.cc.en_GB.inOrder
import ch.tutteli.atrium.api.cc.en_GB.isGreaterThan
import ch.tutteli.atrium.api.cc.en_GB.only
import ch.tutteli.atrium.api.cc.en_GB.property
import ch.tutteli.atrium.api.cc.en_GB.toBe
import ch.tutteli.atrium.api.cc.en_GB.toThrow
import ch.tutteli.atrium.api.cc.en_GB.value
import ch.tutteli.atrium.api.cc.en_GB.values
import ch.tutteli.atrium.creating.Assert
import ch.tutteli.atrium.reporting.RawString
import ch.tutteli.atrium.reporting.translating.StringBasedTranslatable
import ch.tutteli.atrium.reporting.translating.Untranslatable
import ch.tutteli.atrium.verbs.assert
import ch.tutteli.atrium.verbs.expect
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CoreAssertionsTest {

    data class Person(val name: String, val age: Int, val isCanadian: Boolean)

    @Nested
    inner class BuiltInAssertionsTest {

        @DisplayName("The simplest assertion is assert(_).toBe(_)")
        @Test
        fun testBasicStructure() {
            assert(2 * 10).toBe(20)
            assert("Hello" + " " + "World" + "!").toBe("Hello World!")
        }

        @DisplayName("Assertions can be composed")
        @Test
        fun testComposition() {
            assert("Hello World") {
                contains("Hello")
            } and {
                contains("World")
            }
        }

        @DisplayName("Provides assertions for Iterable instances")
        @Test
        fun testIterable() {
            assert((5..7)).contains.inAnyOrder.atLeast(times = 1).values(7, 6)
            assert((5..7)).contains.inOrder.only.values(5, 6, 7)
            assert((5..7)).containsNot.values(3, 4)

            assert((1..10).toList()) {
                hasSize(10)
            } and {
                contains(2, 4, 6, 8)
                containsNot(15)
                containsStrictly(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
            }
        }

        @DisplayName("Allows to assert class properties")
        @Test
        fun testClassProperties() {
            val john =
                Person(name = "John Smith", age = 45, isCanadian = true)

            assert(john) {
                property(john::name).contains.atLeast(2).value("h")
                property(john::age).isGreaterThan(25)
                property(john::isCanadian).toBe(true)
            }
        }

        @DisplayName("Can handle exceptions")
        @Test
        fun testExceptions() {
            expect { assert("Code").contains.atLeast(1).value("a") }
                .toThrow<AssertionError> {}
        }
    }

    private infix fun Assert<String>.hasLength(expectedLength: Int) = createAndAddAssertion(
        description = Untranslatable("has length exactly"),
        expected = expectedLength,
        test = { subject.length == expectedLength })

    private enum class DescriptionHas(override val value: String) : StringBasedTranslatable {
        HAS("has"),
        HAS_NOT("hasn't")
    }

    private val vowels = listOf("a", "e", "i", "o", "u")

    private fun Assert<String>.hasVowel() = createAndAddAssertion(
        description = DescriptionHas.HAS,
        expected = RawString.create("vowel"),
        test = { vowels.any { subject.contains(it) } })

    private fun Assert<String>.hasNoneVowel() = createAndAddAssertion(
        description = DescriptionHas.HAS_NOT,
        expected = RawString.create("vowel"),
        test = { vowels.none { subject.contains(it) } })

    @Nested
    inner class CustomAssertionsTest {

        @DisplayName("Allows to implement custom assertions")
        @Test
        fun testCustomAssertion() {
            assert("Code").hasLength(4)
            assert("Code") hasLength 4

            assert("Hola").hasVowel()
            assert("Ghrr").hasNoneVowel()
        }
    }
}
