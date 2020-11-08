package com.playground.kodein

import kotlin.random.Random
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.kodein.di.Kodein
import org.kodein.di.generic.allInstances
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class MainTest {

    @DisplayName("Modules are used to group dependencies")
    @Test
    fun testModularization() {
        val persistenceModule = Kodein.Module("persistence") {
            bind<PersonRepository>() with singleton { PersonRepositoryInMemory() }
        }
        val serviceModule = Kodein.Module("service") {
            bind<PersonService>() with singleton { PersonServiceDefault(instance()) }
        }
        val dependencies = Kodein {
            importOnce(persistenceModule)
            importOnce(serviceModule)
        }

        val instances by dependencies.allInstances<Any>()

        assertThat(instances).hasSize(2)
    }

    @DisplayName("A provider always return a new instance")
    @Test
    fun testProvider() {
        val dependencies = Kodein {
            bind<Int>() with provider { Random.nextInt() }
        }

        val firstNumber by dependencies.instance<Int>()
        val secondNumber by dependencies.instance<Int>()

        assertThat(firstNumber).isNotEqualTo(secondNumber)
    }

    @DisplayName("A singleton always returns the same instance")
    @Test
    fun testSingleton() {
        val dependencies = Kodein {
            bind<Int>() with singleton { Random.nextInt() }
        }

        val instanceA by dependencies.instance<Int>()
        val instanceB by dependencies.instance<Int>()

        assertThat(instanceA).isEqualTo(instanceB)
    }

    @DisplayName("Dependencies with same type can be named to avoid conflicts")
    @Test
    fun testDependencyTagging() {
        val dependencies = Kodein {
            bind<PersonRepository>(tag = "test") with provider { PersonRepositoryInMemory() }
            bind<PersonRepository>(tag = "prod") with singleton { PersonRepositoryDatabase() }
        }

        val inMemory by dependencies.instance<PersonRepository>(tag = "test")
        val database by dependencies.instance<PersonRepository>(tag = "prod")

        assertThat(inMemory).isInstanceOf(PersonRepositoryInMemory::class.java)
        assertThat(database).isInstanceOf(PersonRepositoryDatabase::class.java)
    }

    @DisplayName("Dependency retrieval works via delegation")
    @Test
    fun testDependencyRetrieval() {
        val dependencies = Kodein {
            bind<PersonRepository>() with singleton { PersonRepositoryInMemory() }
            bind<PersonService>() with singleton { PersonServiceDefault(instance()) }
        }

        val repository by dependencies.instance<PersonRepository>()
        val service by dependencies.instance<PersonService>()

        assertThat(repository).isInstanceOf(PersonRepositoryInMemory::class.java)
        assertThat(service).isInstanceOf(PersonServiceDefault::class.java)
    }

    @DisplayName("Classes can be dependency injection aware")
    @Test
    fun testDependencyAware() = runBlocking {
        val john = Person(Name("John Smith"), Age(35))
        val mary = Person(Name("Mary Jane"), Age(30))
        val dependencies = Kodein {
            bind<List<Person>>() with singleton { listOf(john, mary) }
            bind<PersonRepository>() with singleton { PersonRepositoryInMemory(instance()) }
            bind<PersonService>() with singleton { PersonServiceAware(kodein) }
        }

        val service by dependencies.instance<PersonService>()

        assertThat(service).isInstanceOf(PersonServiceAware::class.java)
        assertThat(service.getPeopleByName(john.name)).containsOnly(john)
        assertThat(service.getPeopleByAge(mary.age)).containsOnly(mary)
    }
}
