package com.playground.atrium

import ch.tutteli.atrium.api.cc.infix.en_UK.contains
import ch.tutteli.atrium.api.cc.infix.en_UK.isGreaterThan
import ch.tutteli.atrium.api.cc.infix.en_UK.toBe
import ch.tutteli.atrium.verbs.assert.assert
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class CoreAssertionsInfixTest {

    @DisplayName("Assertions can be made using infix functions")
    @Test
    fun testInfix() {
        assert("Hello World") contains "Hell"
        assert(2 + 2) toBe 4 isGreaterThan 0
    }
}
