package com.playground.kotlin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@Suppress("USELESS_IS_CHECK", "RedundantExplicitType", "USELESS_NULLABLE_CHECK")
class VariablesTest {

    private object Constants {
        const val HTTP = "http://"
        const val THE_ANSWER = 42
    }

    @DisplayName("Types can be omitted due to compiler type inference")
    @Test
    fun testTypeInference() {
        val name = "Otto"
        val age = 45
        val isCanadian = true
        val salary = 8540.0

        assertThat(name is String).isTrue()
        assertThat(age is Int)
        assertThat(isCanadian is Boolean)
        assertThat(salary is Double).isTrue()

        val city: String = "Washington, DC"
        val population: Int = 45000
        val isCheapToLive: Boolean = false
        val area: Double = 4560.23

        assertThat(city is String).isTrue()
        assertThat(population is Int).isTrue()
        assertThat(isCheapToLive is Boolean).isTrue()
        assertThat(area is Double).isTrue()
    }

    @DisplayName("Keyword val defines immutable data")
    @Test
    fun testImmutableValues() {
        val name = "John"

        // name = "Mary" <--- Compilation Error

        assertThat(name).isEqualTo("John")
    }

    @DisplayName("Keyword var defines mutable data")
    @Test
    fun testMutableVariables() {
        var sum = 2 + 2
        assertThat(sum).isEqualTo(4)

        sum = 5 + 5
        assertThat(sum).isEqualTo(10)

        // sum = { a:Int, b:Int -> a + b } <--- Compiler Error (Type mismatch)
    }

    @DisplayName("Properties of data classes can de deconstructed")
    @Test
    fun testDeconstruction() {
        data class Person(val name: String, val age: Int)

        val (name, age) = Person("Ursula Kennedy", 39)

        assertThat(name).isEqualTo("Ursula Kennedy")
        assertThat(age).isEqualTo(39)
    }

    @DisplayName("Properties of deconstructed objects can be ignored")
    @Test
    fun testDeconstructingIgnoreProperty() {
        data class Car(val chassis: Int, val model: String, val isUsed: Boolean = true)

        val (chassis, _, isUsed) = Car(chassis = 123, model = "Pontiac Astec")

        assertThat(chassis).isEqualTo(123)
        assertThat(isUsed).isTrue()
    }

    @DisplayName("Keyword const is only allowed at top level or inside an object")
    @Test
    fun testConst() {
        assertThat(Constants.HTTP)
                .isInstanceOf(String::class.java)
                .isEqualTo("http://")

        assertThat(Constants.THE_ANSWER).isEqualTo(42)
    }

    @DisplayName("Type system differs nullable types and non-nullable types")
    @Test
    fun testNull() {
        val name = "Edgar"
        assertThat(name is String).isTrue()
        assertThat(name is String?).isTrue()

        val nullName: String? = null
        assertThat(nullName is String).isFalse()
        assertThat(nullName is String?).isTrue()
    }
}
