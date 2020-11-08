package com.playground.preconditions

import com.github.spoptchev.kotlin.preconditions.and
import com.github.spoptchev.kotlin.preconditions.check
import com.github.spoptchev.kotlin.preconditions.matcher.contains
import com.github.spoptchev.kotlin.preconditions.matcher.endsWith
import com.github.spoptchev.kotlin.preconditions.matcher.hasKey
import com.github.spoptchev.kotlin.preconditions.matcher.hasLength
import com.github.spoptchev.kotlin.preconditions.matcher.hasSize
import com.github.spoptchev.kotlin.preconditions.matcher.hasValue
import com.github.spoptchev.kotlin.preconditions.matcher.includes
import com.github.spoptchev.kotlin.preconditions.matcher.isBetween
import com.github.spoptchev.kotlin.preconditions.matcher.isEqualTo
import com.github.spoptchev.kotlin.preconditions.matcher.isGreaterThan
import com.github.spoptchev.kotlin.preconditions.matcher.isGreaterThanOrEqualTo
import com.github.spoptchev.kotlin.preconditions.matcher.isLessThan
import com.github.spoptchev.kotlin.preconditions.matcher.matches
import com.github.spoptchev.kotlin.preconditions.matcher.startsWith
import com.github.spoptchev.kotlin.preconditions.not
import com.github.spoptchev.kotlin.preconditions.or
import com.github.spoptchev.kotlin.preconditions.require
import com.github.spoptchev.kotlin.preconditions.requireThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.fail

class MainTest {

    @DisplayName("Unmatched validations via checkThat throws IllegalStateException")
    @Test
    fun testCheckThat() {
        assertThrows<IllegalStateException> {
            check("Tomorrow will gonna be a good day") {
                startsWith("Tomorrow") and endsWith("good")
            }
        }
    }

    @DisplayName("Unmatched validations via requireThat throws IllegalArgumentException")
    @Test
    fun testRequireThat() {
        assertThrows<IllegalArgumentException> {
            requireThat("2e6479c00b88382dc20080469c3d8238") {
                hasLength(32) and matches("[a-Z][0-9]") and includes("c00b")
            }
        }
    }

    @DisplayName("It can validate a List")
    @Test
    fun testList() {
        catchableTest {
            requireThat((0..10).toList()) {
                hasSize(5) or (contains(6) and contains(8))
            }
        }
    }

    @DisplayName("It can validate a Map")
    @Test
    fun testMap() {
        val volunteers = listOf("Mary", "Alice", "Judith")
                .mapIndexed { index, name -> index to name }
                .toMap()

        catchableTest {
            requireThat(volunteers) {
                hasKey(0) and not(hasValue("Dave"))
            }
        }
    }

    @DisplayName("It can validate an Int")
    @Test
    fun testInt() {
        catchableTest {
            require(8) { isGreaterThan(2) and isLessThan(9) }
            require(7) { isEqualTo(7) }
            require(3) { isBetween((0..5)) }
            require(2) { isGreaterThanOrEqualTo(2) }
        }
    }

    private fun catchableTest(testBody: () -> Unit) {
        try {
            testBody()
        } catch (exception: IllegalArgumentException) {
            fail("An exception should not be raised: ${exception.message}")
        }
    }
}
