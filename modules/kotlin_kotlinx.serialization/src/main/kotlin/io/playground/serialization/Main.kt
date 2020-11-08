package com.playground.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime

@Serializable
data class SSN(val value: Long = 0L) {
    @Serializer(forClass = SSN::class)
    companion object SSNMapper : KSerializer<SSN> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("ssn", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): SSN = SSN(value = decoder.decodeLong())

        override fun serialize(encoder: Encoder, value: SSN) = encoder.encodeLong(value.value)
    }
}

enum class Position {
    Development, Research, Unknown;

    @Serializer(forClass = Position::class)
    companion object PositionMapper : KSerializer<Position> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("position", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): Position =
            valueOf(decoder.decodeString().capitalize())

        override fun serialize(encoder: Encoder, value: Position) =
            encoder.encodeString(value.name.toUpperCase())
    }
}

@Serializable
data class Person(
    val name: String,
    val ssn: SSN = SSN(),
    val position: Position = Position.Unknown
)

@Serializable
data class LeaveOfAbsence(
    val reason: String,
    @Serializable(with = LocalDateTimeMapper::class) val timestamp: LocalDateTime
)

@Serializer(forClass = LocalDateTime::class)
object LocalDateTimeMapper : KSerializer<LocalDateTime> {
    override fun serialize(encoder: Encoder, value: LocalDateTime) =
        encoder.encodeString(value.toString())

    override fun deserialize(decoder: Decoder): LocalDateTime =
        LocalDateTime.parse(decoder.decodeString())
}
