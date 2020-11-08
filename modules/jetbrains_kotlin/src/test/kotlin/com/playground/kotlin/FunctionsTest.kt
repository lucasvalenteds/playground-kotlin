package com.playground.kotlin

import org.assertj.core.api.Assertions.assertThat
import kotlin.reflect.jvm.javaType
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class FunctionsTest {

    private sealed class CallTo {
        data class Yes(val person: Contact) : CallTo()
        object No : CallTo()
    }

    private data class Contact(
        val name: String,
        val phone: String,
        var callTo: CallTo = CallTo.No
    ) {

        infix fun shouldCallTo(contact: Contact): String {
            callTo = CallTo.Yes(contact)
            return contact.phone
        }

        override fun toString() = name
    }

    private data class Counter(val initial: Int)

    private operator fun Counter.unaryMinus() =
        Counter(initial - 5)

    @Nested
    inner class CrossLineTest {

        private inline fun higherOrderFunction(crossinline block: () -> Unit) {
            assertThat(canReturnTwo { block() }).isEqualTo(2)
        }

        private fun canReturnTwo(block: () -> Unit) = block().let { 2 }

        @DisplayName("It makes the lambda impossible to return things")
        @Test
        fun testNotAbleToReturn() {
            assertThat(higherOrderFunction {
                // return 4 <-- `return` not allowed here
            }).isEqualTo(Unit)
        }
    }

    @Nested
    inner class ExtensionTest {

        private fun String.appendHelloWorld() = this + "Hello World"

        @DisplayName("It must be defined outside the class that is been extended")
        @Test
        fun testExtensionFunctionOutsideClass() {
            val predicate = "Meggie told me to say "

            val completeMessage = predicate.appendHelloWorld()

            assertThat(completeMessage).contains("Hello World")
            assertThat(completeMessage).contains(predicate)
        }
    }

    @Nested
    inner class FunTest {

        private fun sumTwo(number: Int) = number.plus(2)

        private val sendMessage = { id: Int, message: String -> "$message (sent from $id)" }

        private fun <T> returnParameter(parameter: T): T = parameter

        @DisplayName("The return type is optional")
        @Test
        fun testOptionalReturnType() {
            assertThat(::sumTwo.returnType.javaType).isEqualTo(Int::class.javaPrimitiveType)
            assertThat(sumTwo(4)).isEqualTo(6)
        }

        @DisplayName("It can be defined using a lambda syntax")
        @Test
        fun testLambdaSyntax() {
            val (id, message) = 13 to "Hello World!"

            assertThat(sendMessage(id, message)).isEqualTo("Hello World! (sent from 13)")
        }

        @DisplayName("It can receive a generic type")
        @Test
        fun testGenericType() {
            val (integer, string) = 12 to "Your welcome"

            assertThat(returnParameter(integer)).isEqualTo(12)
            assertThat(returnParameter(string)).isEqualTo("Your welcome")
        }

        @DisplayName("In a lambda function the return keyword is optional")
        @Test
        fun testReturnKeyword() {
            val yesOrNoFunction = { option: Boolean ->
                if (option) {
                    "Yes"
                } else {
                    "No"
                }
            }

            assertThat(yesOrNoFunction(true)).isEqualTo("Yes")
            assertThat(yesOrNoFunction(false)).isEqualTo("No")
        }
    }

    @Nested
    inner class InfixTest {

        @DisplayName("It can be called without dot and parenthesis")
        @Test
        fun testCallingWithoutParenthesis() {
            val john = Contact(name = "John", phone = "00 98890-3284")
            val mary = Contact(name = "Mary", phone = "00 99609-2345")

            assertThat(john shouldCallTo mary).isEqualTo(mary.phone)
            assertThat(john.shouldCallTo(mary)).isEqualTo(mary.phone)
        }
    }

    @Nested
    inner class InlineTest {

        internal val randomNumbers = 42 to 24

        private inline fun compiledAsCodeBecauseOfItIsInline(context: () -> Boolean): Boolean {
            assertThat(randomNumbers.first).isEqualTo(42)
            return context()
        }

        private fun compiledAsCallbackBecauseItIsNotInline(context: () -> Boolean): Boolean {
            assertThat(randomNumbers.second).isEqualTo(24)
            return context()
        }

        @DisplayName("Replace lambda call with it's implementation when compiled")
        @Test
        fun testImprovesHighOrderFunctions() {
            assertThat(compiledAsCodeBecauseOfItIsInline { true }).isTrue()
            assertThat(compiledAsCodeBecauseOfItIsInline { false }).isFalse()
        }

        @DisplayName("To access private member it must be marked as internal")
        @Test
        fun testAccessToPrivateMembers() {
            assertThat(compiledAsCallbackBecauseItIsNotInline { true }).isTrue()
            assertThat(compiledAsCallbackBecauseItIsNotInline { false }).isFalse()
        }
    }

    @Nested
    inner class NoinlineTest {

        private inline fun randomHOF(callback: () -> Unit, noinline implementation: () -> Unit) {
            callback()
            implementation()
        }

        @DisplayName("It will always be compiled as a callback")
        @Test
        fun testAlwaysCallback() {
            assertThat(randomHOF({}, {})).isEqualTo(Unit)
        }
    }

    @Nested
    inner class OperatorTest {

        @DisplayName("Operators are symbols for common methods like n++ for n.inc()")
        @Test
        fun testOperatorsAreLanguageSymbols() {
            val one = 1
            val numbers = listOf(1, 2, 3)

            assertThat(numbers[2]).isEqualTo(numbers.get(2))
            assertThat(one + one).isEqualTo(one.plus(one))
            assertThat(one - one).isEqualTo(one.minus(one))
        }

        @DisplayName("Minus symbol can be implemented to subtract 5 of a counter")
        @Test
        fun testMinusExample() {
            assertThat(Counter(10)).isEqualTo(
                Counter(
                    10
                )
            )
            assertThat(-Counter(10)).isEqualTo(
                Counter(
                    5
                )
            )
        }

        @DisplayName("Operators .. and until are used to generate ranges of data")
        @Test
        fun testRangeFilter() {
            assertThat(1..10).isInstanceOf(IntRange::class.java)
            assertThat((1..10).toList()).containsAll(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))

            assertThat(2 until 6).isInstanceOf(IntRange::class.java)
            assertThat((2 until 6).toList()).containsAll(listOf(2, 3, 4, 5))
        }

        @DisplayName("The keyword operator is used to override a behavior")
        @Test
        fun testOverrideDefaultOperator() {
            operator fun IntRange.get(range: IntProgression) = toList().slice(range)

            val twoUntilSix = (1..10)[2 until 6] // get operator == List.get() method

            assertThat(twoUntilSix).containsAll(listOf(3, 4, 5, 6))
        }
    }
}
