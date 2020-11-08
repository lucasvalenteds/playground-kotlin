package com.playground.exposed.dsl

import com.playground.exposed.util.BaseTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import java.util.UUID
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class DslTest : BaseTest() {

    private val schemas = arrayOf(Emails)

    @BeforeEach
    fun setUp() = transaction {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(tables = *schemas)
    }

    @AfterEach
    fun tearDown() = transaction {
        SchemaUtils.drop(tables = *schemas)
    }

    @DisplayName("Insert can return the number of inserted row")
    @Test
    fun testInsertWorks(): Unit = transaction {
        val rowId = Emails.insertAndGetId {
            it[address] = "john.smith@mail.com"
            it[weekends] = Weekends.YES
        }

        assertThat(rowId.value).isInstanceOf(UUID::class.java)
    }

    @DisplayName("Insert Throws exception if some value is missing")
    @Test
    fun testInsertErrorHandling(): Unit = transaction {
        assertThatThrownBy { Emails.insert { it[weekends] = Weekends.YES } }
            .isInstanceOf(ExposedSQLException::class.java)
            .hasMessageContaining("NULL not allowed for column \"ADDRESS\"")
    }

    @DisplayName("Insert of multiple values works (batch insert)")
    @Test
    fun testInsertMultipleValues(): Unit = transaction {
        val addresses = listOf(
            "john.smith@mail.com",
            "ann.westewood@mail.com",
            "york.gartner@mail.com"
        )

        val addressesInserted = Emails.batchInsert(addresses) { email ->
            this[Emails.address] = email
        }

        assertThat(addresses.size).isEqualTo(addressesInserted.size)
    }

    @DisplayName("Select all rows is allowed")
    @Test
    fun testSelectAllWorks(): Unit = transaction {
        val addresses = listOf("john.smith", "jane.blake", "edgar.smith").apply {
            Emails.batchInsert(this) { this[Emails.address] = "$it@email.com" }
        }

        val totalItemsReturned = Emails.selectAll()

        assertThat(totalItemsReturned.count()).isEqualTo(addresses.size)
    }

    @DisplayName("Select a single row")
    @Test
    fun testSelectOneWorks(): Unit = transaction {
        val addresses = listOf("rebecca.fried@email.com", "josh.travis@email.com").apply {
            Emails.batchInsert(this) { this[Emails.address] = it }
        }

        val addressesReturned = Emails.selectAll()
            .limit(n = 1, offset = 0)
            .toList()

        assertThat(addressesReturned.first()[Emails.address]).isEqualTo(addresses.first())
    }

    @DisplayName("Select with conditions is allowed")
    @Test
    fun testSelectWithConditions(): Unit = transaction {
        Emails.batchInsert(
            listOf(
                "email3@email.com",
                "email2@example.com",
                "email@example.com"
            )
        ) { this[Emails.address] = it }

        val addressesReturned = Emails
            .slice(listOf(Emails.address))
            .select { Emails.address like "%example.com%" }
            .map { it[Emails.address] }
            .toList()

        assertThat(addressesReturned.size).isEqualTo(2)
        assertThat(addressesReturned).containsAll(listOf("email2@example.com", "email@example.com"))
    }
}
