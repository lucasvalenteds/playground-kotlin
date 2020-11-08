package com.playground.dotenv

import io.github.cdimascio.dotenv.DotEnvException
import io.github.cdimascio.dotenv.dotenv
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class MainTest {

    @DisplayName("All keys are parsed as String")
    @Test
    fun testKeysAreParsedAsString() {
        val env = dotenv { ignoreIfMalformed = true }

        assertThat(env["API_KEY"]).isEqualTo("6162b657e40fb2181d5ba554835553b1")
        assertThat(env["MEETING_DATE"]).isEqualTo("2018-01-04T19:57:37.121")
        assertThat(env["PRETTY_PRINT"]).isEqualTo("true")
        assertThat(env["FOUR"]).isEqualTo("4")
    }

    @DisplayName("Keys without values are empty strings")
    @Test
    fun testEmptyKeysAreStrings() {
        val env = dotenv { ignoreIfMalformed = true }

        assertThat(env["UNDEFINED"]).isEqualTo("")
    }

    @DisplayName("Keys can contain dots")
    @Test
    fun testKeysCanContainDots() {
        val env = dotenv { ignoreIfMalformed = true }

        assertThat(env["LOG.LEVEL"]).isEqualTo("none")
    }

    @DisplayName("The file must be named env")
    @Test
    fun testFileMustBeNamedEnv() {
        assertThatThrownBy { dotenv { directory = ".env.prod" } }
            .isExactlyInstanceOf(DotEnvException::class.java)
    }

    @DisplayName("Throwing exception for keys without equal symbol is optional")
    @Test
    fun testThrowingExceptionsAreOptional() {
        val envThatNotThrows = dotenv { ignoreIfMalformed = true }
        assertThat(envThatNotThrows["MALFORMED"]).isNull()

        assertThatThrownBy { dotenv { ignoreIfMalformed = false } }
            .isExactlyInstanceOf(DotEnvException::class.java)
    }
}
