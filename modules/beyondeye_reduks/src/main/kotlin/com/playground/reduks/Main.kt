package com.playground.reduks

enum class Priority {
    NONE, LOW, MEDIUM, HIGH
}

data class Instruction(
    val content: String,
    val priority: Priority
)

data class InstructionState(val instructions: MutableList<Instruction> = mutableListOf())

sealed class InstructionActions(val instruction: Instruction?) {
    class AddInstruction(task: Instruction) : InstructionActions(task)
    class RemoveLastInstruction : InstructionActions(null)
}
