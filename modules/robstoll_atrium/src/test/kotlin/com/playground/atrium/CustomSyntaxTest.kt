package com.playground.atrium

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class CustomSyntaxTest {

    @DisplayName("A different assert function can be implemented")
    @Test
    fun testAssertion() {
        garantaQue(2 + 2).sejaIgualA(4)
        garantaQue(2 + 2) {
            sejaIgualA(4)
            sejaIgualA(1 + 1 + 1 + 1)
        }
    }

    @DisplayName("A different toThrow function can be implemented")
    @Test
    fun testExceptionHandling() {
        esperaSeQue { garantaQue(2 + 2).sejaIgualA(5) }
            .gereUm<ErroDeExpectativa>()
    }
}
