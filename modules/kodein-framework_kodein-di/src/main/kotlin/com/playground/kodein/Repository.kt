package com.playground.kodein

interface PersonRepository {
    suspend fun save(person: Person): Boolean
    suspend fun findWhere(predicate: (Person) -> Boolean): List<Person>
}

class PersonRepositoryInMemory(fixtures: List<Person> = listOf()) : PersonRepository {
    private val memory = mutableListOf<Person>()

    init {
        memory.addAll(fixtures)
    }

    override suspend fun save(person: Person): Boolean =
        memory.add(person)

    override suspend fun findWhere(predicate: (Person) -> Boolean): List<Person> =
        memory.filter(predicate)
}

class PersonRepositoryDatabase : PersonRepository {
    override suspend fun save(person: Person): Boolean =
        false

    override suspend fun findWhere(predicate: (Person) -> Boolean): List<Person> =
        emptyList()
}
