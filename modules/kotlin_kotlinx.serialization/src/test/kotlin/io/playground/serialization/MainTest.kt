package com.playground.serialization

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class MainTest {

    private val john = Person("John Smith", SSN(123L), Position.Research)
    private val mary = Person("Mary Jane", SSN(456L), Position.Development)
    private val regularEmployees = listOf(john, mary)

    private val amos = Person("Amos Echo")
    private val dani = Person("Dani Lock")
    private val otherEmployees = mapOf(
        amos to LeaveOfAbsence("Retirement", LocalDateTime.of(LocalDate.of(2019, 10, 5), LocalTime.of(8, 35, 10))),
        dani to LeaveOfAbsence("Vacation", LocalDateTime.of(LocalDate.of(2019, 10, 5), LocalTime.of(8, 35, 10)))
    )

    private val defaultJson: Json = Json {
    }

    @DisplayName("It can convert class to JSON string")
    @Test
    fun testSerialization() {
        val unquotedJson = Json {   }
        assertThat(unquotedJson.encodeToString(Person.serializer(), john))
            .isEqualTo("{\"name\":\"John Smith\",\"ssn\":123,\"position\":\"Research\"}")

        assertThat(defaultJson.encodeToString(Person.serializer(), mary))
            .isEqualTo("{\"name\":\"Mary Jane\",\"ssn\":456,\"position\":\"Development\"}")
    }

    @DisplayName("It can convert JSON string to class")
    @Test
    fun testDeSerialization() {
        assertThat(defaultJson.decodeFromString(
            Person.serializer(),
            "{\"name\":\"John Smith\",\"ssn\":123,\"position\":\"Research\"}"
        )).isEqualTo(john)

        assertThat(defaultJson.decodeFromString(
            Person.serializer(),
            "{\"name\":\"Mary Jane\",\"ssn\":456,\"position\":\"Development\"}"
        )).isEqualTo(mary)
    }

    @DisplayName("It can serialize optional properties")
    @Test
    fun testOptionalField() {
        val person = defaultJson.decodeFromString<Person>("{\"name\":\"${amos.name}\"}")

        assertThat(person.ssn).isEqualTo(SSN())
        assertThat(person.position).isEqualTo(Position.Unknown)
    }

    @DisplayName("It can serialize and deserialize lists")
    @Test
    fun testList() {
        assertThat(defaultJson.encodeToString(ListSerializer(Person.serializer()), regularEmployees))
            .isEqualTo("[{\"name\":\"John Smith\",\"ssn\":123,\"position\":\"Research\"},{\"name\":\"Mary Jane\",\"ssn\":456,\"position\":\"Development\"}]")

        assertThat(
            defaultJson.decodeFromString(
                ListSerializer(Person.serializer()),
                """[
                    {"name":"John Smith","ssn":123,"position":"Research"},
                    {"name":"Mary Jane","ssn":456,"position":"Development"}
                ]"""
            )
        ).isEqualTo(regularEmployees)
    }

    @DisplayName("It can serialize and deserialize maps")
    @Test
    fun testMap() {
        val structuredJson = Json {allowStructuredMapKeys = true}

        assertThat(structuredJson.encodeToString(otherEmployees))
            .isEqualTo("[{\"name\":\"Amos Echo\",\"ssn\":0,\"position\":\"Unknown\"},{\"reason\":\"Retirement\",\"timestamp\":\"2019-10-05T08:35:10\"},{\"name\":\"Dani Lock\",\"ssn\":0,\"position\":\"Unknown\"},{\"reason\":\"Vacation\",\"timestamp\":\"2019-10-05T08:35:10\"}]")

        assertThat(structuredJson.decodeFromString<Map<Person, LeaveOfAbsence>>(
            "[{\"name\":\"Amos Echo\",\"ssn\":0,\"position\":\"Unknown\"},{\"reason\":\"Retirement\",\"timestamp\":\"2019-10-05T08:35:10\"},{\"name\":\"Dani Lock\",\"ssn\":0,\"position\":\"Unknown\"},{\"reason\":\"Vacation\",\"timestamp\":\"2019-10-05T08:35:10\"}]"
        )).isEqualTo(otherEmployees)
    }

    @DisplayName("It does not have a built-in date serializer and deserializer")
    @Test
    fun testDates() {
        assertThat(defaultJson.encodeToString(LeaveOfAbsence.serializer(), otherEmployees.getValue(amos)))
            .isEqualTo("{\"reason\":\"Retirement\",\"timestamp\":\"2019-10-05T08:35:10\"}")

        assertThat(defaultJson.decodeFromString<LeaveOfAbsence>("{\"reason\":\"Vacation\",\"timestamp\":\"2019-10-05T08:35:10\"}"))
            .isEqualTo(otherEmployees.getValue(dani))
    }

    @DisplayName("It allows to represent class property as a single value")
    @Test
    fun testCustomMapping() {
        val json = defaultJson.encodeToJsonElement(Person.serializer(), john)

        assertThat(json.jsonObject["ssn"]!!.jsonPrimitive.long).isEqualTo(123L)
        assertThat(json.jsonObject["ssn"]).isNotEqualTo("{value: 123}")
    }
}
