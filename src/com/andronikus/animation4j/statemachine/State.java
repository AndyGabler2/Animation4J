package com.andronikus.animation4j.statemachine;

import com.andronikus.animation4j.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public abstract class State<
    STATE_TYPE extends State<STATE_TYPE, TRANSITION_CONTEXT, TRANSITION_ROOT>,
    TRANSITION_CONTEXT, TRANSITION_ROOT
> {

    // Whether this state can be interrupted by the transition to another state or if its animation must play out
    private boolean interruptable = true;

    private List<Pair<BiFunction<TRANSITION_CONTEXT, TRANSITION_ROOT, Boolean>, STATE_TYPE>> transitions = new ArrayList<>();

    public STATE_TYPE checkTransition(
        TRANSITION_CONTEXT contextProvider,
        TRANSITION_ROOT animatedEntity
    ) {
        final Pair<
            BiFunction<TRANSITION_CONTEXT, TRANSITION_ROOT, Boolean>,
            STATE_TYPE
        > nextState = transitions.stream()
            .filter(transition -> transition.getFirst().apply(contextProvider, animatedEntity))
            .findFirst().orElse(null);

        if (nextState == null) {
            return null;
        }

        return nextState.getSecond();
    }

    public STATE_TYPE withInterruptableFlag(boolean interruptable) {
        this.interruptable = interruptable;
        return (STATE_TYPE) this;
    }

    /**
     * Create a state with a function call that is used to transition to it.
     *
     * @param transitionCheck Condition for when to transition to the state
     * @return The newly created state
     */
    public STATE_TYPE createTransitionState(BiFunction<TRANSITION_CONTEXT, TRANSITION_ROOT, Boolean> transitionCheck) {
        return createTransition(transitionCheck, createBlankState());
    }

    /**
     * Can the stop motion animation leave this state and move to the one it should be transitioned to?
     *
     * @return True if transition can happen
     */
    public boolean isTransitionFromOkay() {
        return interruptable || atleastOneCycleFinished();
    }

    /**
     * Create a transition between this state and another state.
     *
     * @param transitionCheck Condition for when to transition to the state
     * @param state The state to transition to when condition is met
     * @return The state that a transition was added for
     */
    public STATE_TYPE createTransition(
        BiFunction<TRANSITION_CONTEXT, TRANSITION_ROOT, Boolean> transitionCheck,
        STATE_TYPE state
    ) {
        this.transitions.add(new Pair<>(transitionCheck, state));
        return state;
    }

    /**
     * Transition to this state.
     */
    public abstract void transitionTo();

    protected abstract boolean atleastOneCycleFinished();

    protected abstract STATE_TYPE createBlankState();
}
