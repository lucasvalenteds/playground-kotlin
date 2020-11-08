package com.playground.reduks

import com.beyondeye.reduks.Reducer
import com.beyondeye.reduks.ReducerFn
import com.beyondeye.reduks.SimpleStore
import com.beyondeye.reduks.subscribe
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class MainTest {

    private val instructionReducer: Reducer<InstructionState> = ReducerFn { state, action ->
        when (action) {
            is InstructionActions.AddInstruction -> {
                state.instructions.add(action.instruction!!).let { state }
            }
            is InstructionActions.RemoveLastInstruction -> {
                state.copy(state.instructions.dropLast(1).toMutableList())
            }
            else -> state
        }
    }
    private val counterStore = SimpleStore(InstructionState(), instructionReducer)

    @DisplayName("Actions can be dispatched using a reactive api")
    @Test
    fun testReactiveApi() {
        var numberOfInstructionsDispatched = 0

        counterStore.subscribe { numberOfInstructionsDispatched++ }

        counterStore.dispatch(InstructionActions.AddInstruction(Instruction("git add .", Priority.MEDIUM)))
        counterStore.dispatch(InstructionActions.AddInstruction(Instruction("git commit", Priority.HIGH)))
        counterStore.dispatch(InstructionActions.AddInstruction(Instruction("git push origin master", Priority.NONE)))
        counterStore.dispatch(InstructionActions.RemoveLastInstruction())
        counterStore.dispatch(InstructionActions.AddInstruction(Instruction("git push origin other-branch", Priority.LOW)))

        assertThat(numberOfInstructionsDispatched).isEqualTo(5)
        assertThat(counterStore.state.instructions.size).isEqualTo(3)
    }
}
