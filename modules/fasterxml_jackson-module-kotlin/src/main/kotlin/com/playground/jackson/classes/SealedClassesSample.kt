package com.playground.jackson.classes

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer

@JsonSerialize(using = ResultSerializer::class)
@JsonDeserialize(using = ResultDeserializer::class)
sealed class Result {
    object Success : Result()
    object Failure : Result()
    object Unknown : Result()

    override fun toString() = this.javaClass.simpleName.toLowerCase()
}

class ResultSerializer : StdSerializer<Result>(Result::class.java) {

    override fun serialize(result: Result, generator: JsonGenerator, provider: SerializerProvider) {
        generator.writeString(result.toString())
    }
}

class ResultDeserializer : StdDeserializer<Result>(Result::class.java) {

    override fun deserialize(parser: JsonParser, context: DeserializationContext): Result {
        val resultName = parser.readValueAs(String::class.java).toLowerCase()

        return when (resultName) {
            Result.Success.toString() -> Result.Success
            Result.Failure.toString() -> Result.Failure
            else -> Result.Unknown
        }
    }
}

data class Message(
    val result: Result = Result.Unknown,
    val content: String = ""
)
