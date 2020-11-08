package com.playground.time

import com.kizitonwose.time.days
import com.kizitonwose.time.hours
import com.kizitonwose.time.milliseconds
import com.kizitonwose.time.minutes
import com.kizitonwose.time.nanoseconds
import com.kizitonwose.time.seconds
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class MainTest {

    @DisplayName("The lower unit is nanoseconds")
    @Test
    fun testLowerUnitAvailable() {
        assertThat(1.milliseconds).isEqualTo(1000000.nanoseconds)
    }

    @DisplayName("The greater unit is days")
    @Test
    fun testGreaterUnit() {
        assertThat(24.hours).isEqualTo(1.days)
    }

    @DisplayName("Different units can be compared")
    @Test
    fun testComparingDifferentUnits() {
        assertThat(2.minutes > 120.seconds).isFalse()
        assertThat(24.hours <= 86400.seconds).isTrue()
    }

    @DisplayName("Different units can be added and subtracted")
    @Test
    fun testSubtractDifferentUnits() {
        assertThat(1.minutes - 30.seconds).isEqualTo(30.seconds)
    }

    @DisplayName("An Interval can be converted to another unit")
    @Test
    fun testIntervalConversion() {
        assertThat(1.minutes.inSeconds).isEqualTo(60.seconds)
    }

    @DisplayName("Interval toString method is overridden")
    @Test
    fun testIntervalToStringNotOverridden() {
        assertThat(9.seconds.toString()).contains("9 seconds")
    }

    @DisplayName("A custom time unit can be implemented")
    @Test
    fun testImplementingCustomTimeUnit() {
        assertThat(2.weeks).isEqualTo(14.days)
    }
}
