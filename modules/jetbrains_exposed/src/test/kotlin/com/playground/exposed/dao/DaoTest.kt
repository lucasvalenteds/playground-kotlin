package com.playground.exposed.dao

import com.playground.exposed.util.BaseTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class DaoTest : BaseTest() {

    private val schemas = arrayOf(Authors, Books, BookAuthors)

    @BeforeEach
    fun setUp() = transaction {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(tables = *schemas)
        insertFixtures()
    }

    @AfterEach
    fun tearDown() = transaction {
        SchemaUtils.drop(tables = *schemas)
    }

    @DisplayName("Join with multiple tables works")
    @Test
    fun testJoin() {
        transaction {
            val authors = Books.innerJoin(BookAuthors).innerJoin(Authors)
                .slice(Authors.name, Authors.id)
                .select(Books.title like "%Cross%")
                .let(Author.Companion::wrapRows)
                .map(Author::name)

            assertThat(authors.count()).isEqualTo(2)
            assertThat(authors).containsAll(listOf("Stephen King", "Neil Gaiman"))
        }
    }

    @DisplayName("Where clauses can be used (eq, limit, order by, like)")
    @Test
    fun testSelectionClauses() {
        transaction {
            val titles = (Books innerJoin BookAuthors innerJoin Authors)
                .select { (Books.isbn13 like "9781%") and (Authors.name eq "Neil Gaiman") }
                .orderBy(Books.id to SortOrder.DESC)
                .limit(1)
                .let(Book.Companion::wrapRows)
                .map(Book::title)

            assertThat(titles.size).isEqualTo(1)
            assertThat(titles).contains("Christine")
        }
    }

    private fun insertFixtures(): Unit = transaction {
        val stephen = Author.new { name = "Stephen King" }
        val neil = Author.new { name = "Neil Gaiman" }

        Book.new { title = "American Gods"; isbn13 = "9780062080233" }.apply {
            authors = SizedCollection(listOf(stephen))
        }
        Book.new { title = "The Ocean at the End of the Lane"; isbn13 = "9780062255662" }.apply {
            authors = SizedCollection(listOf(stephen))
        }
        Book.new { title = "Under the Dome"; isbn13 = "9781476735474" }.apply {
            authors = SizedCollection(listOf(neil))
        }
        Book.new { title = "Christine"; isbn13 = "9781501144189" }.apply {
            authors = SizedCollection(listOf(neil))
        }
        Book.new { title = "Crossover"; isbn13 = "0001234567890" }.apply {
            authors = SizedCollection(listOf(stephen, neil))
        }
    }
}
