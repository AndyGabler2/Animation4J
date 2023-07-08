package com.andronikus.animation4j.statemachine;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    private boolean interruptible = true;

    /*
     * Ignored if interruptible is "true", but in cases where this animation cannot be interrupted, this is a hook
     * for transitions off of this state that are interruptible.
     */
    private Boolean transitionInterruptible = null;

    private List<Transition> transitions = new ArrayList<>();

    /**
     * Check if a transition is to happen to a subsequent state.
     *
     * @param contextProvider Object providing greater context
     * @param rootEntity Object that is used to be deciding transition factor
     * @return The state, null if no transition
     */
    STATE_TYPE checkTransition(
        TRANSITION_CONTEXT contextProvider,
        TRANSITION_ROOT rootEntity
    ) {
        final Transition transitionToNextState = transitions.stream()
            .filter(transition -> transition.transitionFunction.apply(contextProvider, rootEntity))
            .findFirst().orElse(null);

        if (transitionToNextState == null) {
            return null;
        }

        if (!interruptible && transitionToNextState.overridesInterruptibleFlag) {
            transitionInterruptible = true;
        }

        return transitionToNextState.toState;
    }

    /**
     * Set whether the state is interruptable and can be transitioned from without finishing one cycle.
     *
     * @param interruptable Interruptible flag
     * @return Self
     */
    public STATE_TYPE withInterruptibleFlag(boolean interruptable) {
        this.interruptible = interruptable;
        return (STATE_TYPE) this;
    }

    /**
     * Create a state with a function call that is used to transition to it.
     *
     * @param transitionCheck Condition for when to transition to the state
     * @return The newly created state
     */
    public STATE_TYPE createTransitionState(BiFunction<TRANSITION_CONTEXT, TRANSITION_ROOT, Boolean> transitionCheck) {
        return createTransitionState(transitionCheck, false);
    }

    /**
     * Create a state with a function call that is used to transition to it.
     *
     * @param transitionCheck Condition for when to transition to the state
     * @param interruptible If this transition interrupts the animation. Only used if the state is not interruptible
     * @return The newly created state
     */
    public STATE_TYPE createTransitionState(
        BiFunction<TRANSITION_CONTEXT, TRANSITION_ROOT, Boolean> transitionCheck,
        boolean interruptible
    ) {
        return createTransition(transitionCheck, interruptible, createBlankState());
    }

    /**
     * Can the stop motion animation leave this state and move to the one it should be transitioned to?
     *
     * @return True if transition can happen
     */
    public boolean isTransitionFromOkay() {
        return interruptible || (transitionInterruptible != null && transitionInterruptible) || atleastOneCycleFinished();
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
        return createTransition(transitionCheck, false, state);
    }

    /**
     * Create a transition between this state and another state.
     *
     * @param transitionCheck Condition for when to transition to the state
     * @param interruptible If this transition interrupts the animation. Only used if the state is not interruptible
     * @param state The state to transition to when condition is met
     * @return The state that a transition was added for
     */
    public STATE_TYPE createTransition(
        BiFunction<TRANSITION_CONTEXT, TRANSITION_ROOT, Boolean> transitionCheck,
        boolean interruptible,
        STATE_TYPE state
    ) {
        Objects.requireNonNull(state, "Transition to state must be null.");
        final Transition transition = new Transition();
        transition.toState = state;
        transition.transitionFunction = transitionCheck;
        transition.overridesInterruptibleFlag = interruptible;
        this.transitions.add(transition);
        return state;
    }

    /**
     * Transition to this state. This should reset instance specific variables that need to be reset when the state is reused.
     *
     * Package private actions to take.
     */
    void prepareToBeActive() {
        // Reset the flag for if the transition off of this state is interruptable
        transitionInterruptible = null;

        transitionTo();
    }

    /**
     * Transition to this state. This should reset instance specific variables that need to be reset when the state is reused.
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

    /**
     * A transition from this state to a new one.
     */
    private class Transition {
        STATE_TYPE toState;
        BiFunction<TRANSITION_CONTEXT, TRANSITION_ROOT, Boolean> transitionFunction;
        boolean overridesInterruptibleFlag;
    }
}
