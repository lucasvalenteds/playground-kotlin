package com.playground.exposed.dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Table

object Authors : IntIdTable() {
    val name = varchar("name", 50)
}

class Author(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Author>(Authors)

    var name by Authors.name
}

object Books : IntIdTable() {
    val title = varchar("title", 255)
    val isbn13 = varchar("isbn13", 13)
}

class Book(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Book>(Books)

    var title by Books.title
    var isbn13 by Books.isbn13
    var authors by Author.via(BookAuthors)
}

// Many-to-many
object BookAuthors : Table() {
    val book = reference("book", Books).primaryKey(0)
    val author = reference("author", Authors).primaryKey(1)
}
