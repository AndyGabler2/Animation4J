package com.andronikus.animation4j.statemachine;

/**
 * Deterministic finite automata used to transition between states and apply the state appropriately.
 *
 * @param <TRANSITION_CONTEXT> Type of object that provides context to transitions
 * @param <TRANSITION_ROOT> Type of object that will actually be acted on and provides instance-specific transition logic
 * @param <STATE_TYPE> The sub-type of the state in the DFA
 * @param <STATE_RESULT> The type of result returned by applying the state
 * @author Andronikus
 */
public abstract class DeterministicFiniteAutomata<
    TRANSITION_CONTEXT,
    TRANSITION_ROOT,
    STATE_TYPE extends State<STATE_TYPE, TRANSITION_CONTEXT, TRANSITION_ROOT>,
    STATE_RESULT
> {

    private STATE_TYPE initialState;
    private STATE_TYPE activeState;
    private STATE_TYPE realState;

    /**
     * Instantiate a deterministic finite automata.
     *
     * @param instantiationParameters Optional parameters on instantiation
     */
    public DeterministicFiniteAutomata(Object... instantiationParameters) {
        handleInstantiationParameters(instantiationParameters);
        initialState = buildInitialStatesAndTransitions();
        this.activeState = initialState;
        this.realState = initialState;
        initialState.prepareToBeActive();
    }

    /**
     * If handling of instantiation parameters are required before state building, handle these parameters.
     *
     * @param parameters The parameters
     */
    protected void handleInstantiationParameters(Object... parameters) {}

    /**
     * Perform the next action on a state. Method responsible for choosing and applying state.
     *
     * @param contextObject The context object
     * @param root The object that is being applied to whichever state comes up
     * @param parameters Parameters for the action
     * @return The result of the state's action
     */
    protected STATE_RESULT nextAction(TRANSITION_CONTEXT contextObject, TRANSITION_ROOT root, Object... parameters) {
        final STATE_TYPE nextState = realState.checkTransition(contextObject, root);

        if (nextState != null) {
            realState = nextState;
            nextState.prepareToBeActive();
        }

        if (activeState != realState && activeState.isTransitionFromOkay()) {
            activeState = realState;
        }

        return doWithNextState(activeState, contextObject, root, parameters);
    }

    /**
     * Handle the next state in the state machine.
     *
     * @param state The state
     * @param contextObject The object providing context to the state
     * @param root The object being handled by the state
     * @param parameters The parameters to pass to the state
     * @return The result of the state's handling of the root object
     */
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
