package com.playground.mockk

import io.mockk.Called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class MainTest {

    interface Joystick {
        fun up(): String
        fun down(): String
        fun left(): String
        fun right(): String
    }

    interface Action {
        suspend fun doIt(): Boolean
    }

    object PlayStation : Joystick {
        override fun up() = "Triangle"
        override fun down() = "X"
        override fun left() = "Square"
        override fun right() = "Circle"
    }

    class Keyboard : Joystick, Action {
        override fun up() = "W"
        override fun down() = "A"
        override fun left() = "S"
        override fun right() = "D"

        override suspend fun doIt() = delay(400).let { false }
    }

    @Test
    @DisplayName("It can mock final classes")
    fun testMockFinal() {
        mockkObject(PlayStation) {
            assertThat(PlayStation.right()).isEqualTo("Circle")

            every { PlayStation.right() } returns "X"

            assertThat(PlayStation.right()).isEqualTo("X")
        }
    }

    @Test
    @DisplayName("It provides Mockito-like matchers")
    fun testMatchers() {
        val xboxController = mockk<Joystick> {
            every { up() } returns "Y"
            every { down() } returns "A"
            every { left() } returns "X"
            every { right() } returns "B"
        }

        xboxController.up()
        xboxController.down()
        xboxController.left()

        verify(exactly = 1) { xboxController.up() }
        verify(atLeast = 1) { xboxController.down() }
        verify(atMost = 3) { xboxController.left() }
        verify(inverse = true) { xboxController.right() }
        verify { mockk<Joystick>() wasNot Called }
    }

    @Disabled("Caused by: java.lang.ClassNotFoundException: kotlinx.coroutines.experimental.BuildersKt")
    @Test
    @DisplayName("Coroutines can be spied works out of the box")
    fun testCoroutines() {
        val keyboard = spyk(Keyboard())

        coEvery { keyboard.doIt() } returns true

        assertThat(runBlocking { keyboard.doIt() }).isTrue()

        coVerify { keyboard.doIt() }
    }
}
