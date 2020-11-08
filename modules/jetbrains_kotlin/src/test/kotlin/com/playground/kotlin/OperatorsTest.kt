package com.playground.kotlin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class OperatorsTest {

    private enum class Brand {
        FENDER, IBANEZ
    }

    private data class Guitar(private val properties: Map<String, Any?>) {
        val brand: Brand by properties
        val color: String by properties
        val price: Double by properties
    }

    private open class Animal

    private class Dog : Animal()

    @Nested
    inner class KeywordsTest {

        @DisplayName("Casting can be done by the operators as and as?")
        @Test
        fun testAs() {
            assertThat(Dog()).isInstanceOf(Dog::class.java)
            assertThat(Dog() as Animal).isInstanceOf(Animal::class.java)
        }

        @DisplayName("In is used to check elements in iteration and ranges")
        @Test
        fun testIn() {
            assertThat(5 in 1..10).isTrue()
            assertThat(10 !in 1..5).isTrue()
        }

        @Suppress("USELESS_IS_CHECK")
        @DisplayName("Is means instanceOf in Java")
        @Test
        fun testIs() {
            assertThat(10 is Int).isTrue()
            assertThat("some string" is String).isTrue()
        }

        @DisplayName("When is a switch/case replacement")
        @Test
        fun testWhen() {
            val brand = Brand.FENDER

            val complexIfResult = when (brand) {
                Brand.IBANEZ -> false
                Brand.FENDER -> true
            }

            assertThat(complexIfResult).isTrue()
        }
    }

    @Nested
    inner class IdentifiersTest {

        @DisplayName("By can be use to implement Lazy Loading")
        @Test
        fun testByLazyLoading() {
            val message by lazy {
                Thread.sleep(500)
                "Hello World!"
            }

            assertThat(message).isEqualTo("Hello World!")
        }

        @DisplayName("By can be used to pass arguments to a class (Delegation Properties)")
        @Test
        fun testDelegationProperty() {
            val stratocaster = Guitar(
                mapOf(
                    "brand" to Brand.FENDER,
                    "color" to "red",
                    "price" to 4500.0
                )
            )

            assertThat(stratocaster.brand).isEqualTo(Brand.FENDER)
            assertThat(stratocaster.color).isEqualTo("red")
            assertThat(stratocaster.price).isEqualTo(4500.0)
        }
    }
}
