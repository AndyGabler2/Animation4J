package com.andronikus.animation4j.statemachine;

public abstract class DeterministicFiniteAutomata<
    TRANSITION_CONTEXT,
    TRANSITION_ROOT,
    STATE_TYPE extends State<STATE_TYPE, TRANSITION_CONTEXT, TRANSITION_ROOT>,
    STATE_RESULT
> {

    private STATE_TYPE initialState;
    private STATE_TYPE activeState;
    private STATE_TYPE realState;

    public DeterministicFiniteAutomata(Object... instantiationParameters) {
        handleInstantiationParameters(instantiationParameters);
        initialState = buildInitialStatesAndTransitions();
        this.activeState = initialState;
        this.realState = initialState;
        initialState.transitionTo();
    }

    protected void handleInstantiationParameters(Object... parameters) {}

    protected STATE_RESULT nextAction(TRANSITION_CONTEXT contextObject, TRANSITION_ROOT root, Object... parameters) {
        final STATE_TYPE nextAnimation = realState.checkTransition(contextObject, root);

        if (nextAnimation != null) {
            realState = nextAnimation;
            nextAnimation.transitionTo();
        }

        if (activeState != realState && activeState.isTransitionFromOkay()) {
            activeState = realState;
        }

        return doWithNextState(activeState, contextObject, root, parameters);
    }

    protected abstract STATE_RESULT doWithNextState(STATE_TYPE state, TRANSITION_CONTEXT contextObject, TRANSITION_ROOT root, Object... parameters);

    /**
     * Build the initial state. This is expected to have, in its transitions, every state that is possible.
     * Note, states never return to their initial state unless a child state makes a cyclic transition. Therefore,
     * the resulting initial state must include every transition and each transition must be deliberate.
     *
     * @return The initial animation state
     */
    protected abstract STATE_TYPE buildInitialStatesAndTransitions();

    /**
     * Check if the object we are attempting to apply a state to is the correct root object. This is appropriate and
     * essential to call since the sprite retrieval function is stateful, so it is important that it is only called
     * when intended.
     *
     * @param object The object that may or may not be the object this automata is controlling
     * @return Whether this automaton controls given object
     */
    public abstract boolean checkIfObjectIsRoot(TRANSITION_ROOT object);
}
