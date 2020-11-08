package com.playground.regexdsl

import com.github.h0tk3y.regexDsl.regex
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class MainTest {

    @Nested
    inner class Usages {

        @DisplayName("It can select the pattern <key>:<value> from Redis entries")
        @Test
        fun testRedisPattern() {
            val redisPattern = regex {
                startOfString()
                oneOrMore { anyOf({ alphaNumeric() }) }
                literally(":")
                oneOrMore { anyOf({ alphaNumeric() }) }
                endOfString()
            }

            assertThat(redisPattern in "name:John").isTrue()
            assertThat(redisPattern in "age:25").isTrue()
            assertThat(redisPattern in "isCanadian:true").isTrue()

            assertThat(redisPattern in "name:").isFalse()
            assertThat(redisPattern in ":312").isFalse()
            assertThat(redisPattern in ":").isFalse()
            assertThat(redisPattern in "The entries are stored as username:password in the database").isFalse()
        }

        @DisplayName("It can find mentions of a username in a tweet content")
        @Test
        fun testTwitterPattern() {
            val mentionPattern = regex {
                literally("@")
                oneOrMore { anyOf({ digit() }, { letter() }) }
                whitespace()
            }

            assertThat(mentionPattern in "Thanks for coming, @dave42")
            assertThat(mentionPattern in "E-mail addresses has @ character").isFalse()
        }

        @DisplayName("It can find CPF numbers in with and without dots in a string")
        @Test
        fun testCpfPattern() {
            val cpfPattern = regex {
                3 times { digit() }; literally(".")
                3 times { digit() }; literally(".")
                3 times { digit() }; literally("-")
                2 times { digit() }
            }

            assertThat(cpfPattern in "000.000.000-00").isTrue()
            assertThat(cpfPattern in "My CPF is 111.111.111-11 and my name is Mary").isTrue()
        }
    }

    @Nested
    inner class API {

        @DisplayName("The compiled pattern can be retrieved as string")
        @Test
        fun testPatternToString() {
            val expression = regex {
                startOfString()
                alphaNumeric()
                endOfString()
            }

            assertThat(expression.pattern).isEqualTo("^[A-Za-z0-9]$")
        }
    }
}
