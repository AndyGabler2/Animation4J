package com.andronikus.animation4j.statemachine;

import com.andronikus.animation4j.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * State in a state machine.
 *
 * @param <STATE_TYPE> The type of state
 * @param <TRANSITION_CONTEXT> Type of object providing context to the state
 * @param <TRANSITION_ROOT> Type of object that is used for the state's application and transitions
 * @author Andronikus
 */
public abstract class State<
    STATE_TYPE extends State<STATE_TYPE, TRANSITION_CONTEXT, TRANSITION_ROOT>,
    TRANSITION_CONTEXT, TRANSITION_ROOT
> {

    // Whether this state can be interrupted by the transition to another state or if its animation must play out
    private boolean interruptable = true;

    private List<Pair<BiFunction<TRANSITION_CONTEXT, TRANSITION_ROOT, Boolean>, STATE_TYPE>> transitions = new ArrayList<>();

    /**
     * Check if a transition is to happen to a subsequent state.
     *
     * @param contextProvider Object providing greater context
     * @param rootEntity Object that is used to be deciding transition factor
     * @return The state, null if no transition
     */
    public STATE_TYPE checkTransition(
        TRANSITION_CONTEXT contextProvider,
        TRANSITION_ROOT rootEntity
    ) {
        final Pair<
            BiFunction<TRANSITION_CONTEXT, TRANSITION_ROOT, Boolean>,
            STATE_TYPE
        > nextState = transitions.stream()
            .filter(transition -> transition.getFirst().apply(contextProvider, rootEntity))
            .findFirst().orElse(null);

        if (nextState == null) {
            return null;
        }

        return nextState.getSecond();
    }

    /**
     * Set whether the state is interruptable and can be transitioned from without finishing one cycle.
     *
     * @param interruptable Interruptable flag
     * @return Self
     */
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

    /**
     * Has the state finished at least one cycle of actions?
     *
     * @return True, if it has finished at least one cycle of actions
     */
    protected abstract boolean atleastOneCycleFinished();

    /**
     * Create a blank state to be transitioned to.
     *
     * @return The new state
     */
    protected abstract STATE_TYPE createBlankState();
}
