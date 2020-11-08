package com.playground.kotlin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ClassesTest {

    private enum class Brand {
        DODGE, FORD, HONDA
    }

    private data class Car(val brand: Brand, val model: String, val releaseYear: Int)

    private data class Person(val name: String, val car: Car)

    private sealed class RaceStatus {
        object STOPPED : RaceStatus()
        object HAPPENING : RaceStatus()
        object FINISHED : RaceStatus()
    }

    private class Race(val title: String, private val subscriptions: List<Person> = emptyList()) {

        var status: RaceStatus =
            RaceStatus.STOPPED
            private set

        val winner: Person by lazy {
            status = RaceStatus.FINISHED

            subscriptions.first()
        }

        fun hasFinished() = status is RaceStatus.FINISHED

        fun start() {
            status = RaceStatus.HAPPENING
        }
    }

    @Nested
    inner class ClassTest {

        @DisplayName("It does not overrides Object methods automatically")
        @Test
        fun testClassDoesNotOverridesAutomatically() {
            val title = "Indiana IX Drag Race Festival"

            val race = Race(title)

            assertThat(race.toString()).doesNotContain("title")
            assertThat(race.toString()).doesNotContain(title)
        }

        @DisplayName("The parameters can have default values")
        @Test
        fun testClassDefaultValues() {
            val race = Race("New Jersey Truck competition")

            assertThat(race.status).isEqualTo(RaceStatus.STOPPED)
        }

        @DisplayName("New keyword does not exists and parameters can be named")
        @Test
        fun testClassNewAndNamedParameters() {
            val race = Race(title = "III Texas Street Race")

            assertThat(race.title).isEqualTo("III Texas Street Race")
        }
    }

    @Nested
    inner class DataClassTest {

        @DisplayName("The methods Object methods are overridden by default")
        @Test
        fun testDataClassOverridesAutomatically() {
            val mustang = Car(
                brand = Brand.FORD,
                model = "Mustang",
                releaseYear = 1978
            )

            assertThat(mustang).isEqualTo(
                Car(
                    Brand.FORD,
                    "Mustang",
                    1978
                )
            )
            assertThat(mustang).isNotEqualTo(
                Car(
                    Brand.FORD,
                    "Mustang",
                    1990
                )
            )
            assertThat(mustang.toString()).contains("brand=FORD")
            assertThat(mustang.toString()).contains("model=Mustang")
            assertThat(mustang.toString()).contains("releaseYear=1978")
        }
    }

    @Nested
    inner class EnumClassTest {

        @DisplayName("Enum class is used to declare traditional Java Enum")
        @Test
        fun testEnumClass() {
            assertThat(Brand.values()).hasSize(3)
            assertThat(Brand.valueOf("FORD")).isEqualTo(Brand.FORD)
            assertThat(Brand.FORD.name).isEqualTo("FORD")
        }
    }

    @Nested
    inner class ObjectTest {

        @DisplayName("They are singletons and cannot be instantiated")
        @Test
        fun testObjectIsSingleton() {
            val (status1, status2) = RaceStatus.FINISHED to RaceStatus.FINISHED

            assertThat(status1).isEqualTo(status2)
        }
    }

    @Nested
    inner class SealedClassTest {

        @DisplayName("It cannot be instantiated")
        @Test
        fun testSealedClassIsAbstract() {
            setOf(
                RaceStatus.STOPPED,
                RaceStatus.HAPPENING,
                RaceStatus.FINISHED
            )
                    .forEach { assertThat(it).isInstanceOf(RaceStatus::class.java) }
        }

        @DisplayName("It can be used as a null alternative")
        @Test
        fun testSealedClassNullAlternative() {
            val (mustang, charger, civic) = Triple(
                Car(
                    Brand.FORD,
                    "Mustang",
                    1971
                ),
                Car(
                    Brand.DODGE,
                    "Charger",
                    1972
                ),
                Car(
                    Brand.HONDA,
                    "Civic CX",
                    1997
                )
            )

            val (dave, rick, john) = Triple(
                Person("Dave Johnson", mustang),
                Person("Rick Scott", charger),
                Person("John Harris", civic)
            )

            val race = Race("World Cup", listOf(dave, rick, john))

            assertThat(race.status).isEqualTo(RaceStatus.STOPPED)

            race.start().let {
                assertThat(race.status).isEqualTo(RaceStatus.HAPPENING)
            }

            assertThat(race.winner).isEqualTo(dave)
            assertThat(race.hasFinished()).isTrue()
            assertThat(race.status).isEqualTo(RaceStatus.FINISHED)
        }
    }
}
