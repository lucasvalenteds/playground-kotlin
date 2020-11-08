package com.playground.time

import com.kizitonwose.time.Interval
import com.kizitonwose.time.TimeUnit

private const val SECONDS_IN_A_WEEK = 604800.0

class Week : TimeUnit {
    override val timeIntervalRatio = SECONDS_IN_A_WEEK
}

val Number.weeks: Interval<Week>
    get() = Interval(this)
