package com.playground.exposed.dsl

import org.jetbrains.exposed.dao.UUIDTable

enum class Weekends {
    YES, NO
}

object Emails : UUIDTable() {
    val address = varchar("address", 255)
    val weekends = enumeration("weekends", Weekends::class).default(Weekends.NO)
}
