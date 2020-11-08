package com.playground.kodein

import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

interface PersonService {
    suspend fun getPeopleByName(name: Name): List<Person>
    suspend fun getPeopleByAge(age: Age): List<Person>
}

class PersonServiceDefault(private val repository: PersonRepository) : PersonService {
    override suspend fun getPeopleByName(name: Name): List<Person> =
        repository.findWhere { it.name == name }

    override suspend fun getPeopleByAge(age: Age): List<Person> =
        repository.findWhere { it.age == age }
}

class PersonServiceAware(override val kodein: Kodein) : PersonService, KodeinAware {

    private val repository: PersonRepository by instance()

    override suspend fun getPeopleByName(name: Name): List<Person> =
        repository.findWhere { it.name == name }

    override suspend fun getPeopleByAge(age: Age): List<Person> =
        repository.findWhere { it.age == age }
}
