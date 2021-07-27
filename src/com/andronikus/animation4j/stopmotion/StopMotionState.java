package com.andronikus.animation4j.stopmotion;

import com.andronikus.animation4j.spritesheet.SpriteSheet;
import com.andronikus.animation4j.util.Pair;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * State in a stop motion animation. The animation has a set of states. These states are just descriptions about the
 * condition of the of animated object. Within each state, are the frames that the state cycles through and transitions
 * to other states.
 *
 * @param <ANIMATION_OF_TYPE> The type of object being animated
 * @param <SPRITE_SHEET_TYPE> The type of sprite sheet being used to pull frames from
 * @author Andronikus
 */
public class StopMotionState<CONTEXT_PROVIDER, ANIMATION_OF_TYPE, SPRITE_SHEET_TYPE extends SpriteSheet> {

    private final StopMotionController<CONTEXT_PROVIDER, ANIMATION_OF_TYPE, SPRITE_SHEET_TYPE> controller;

    // List of pairings of transition functions and states to transition to
    private final List<Pair<BiFunction<CONTEXT_PROVIDER, ANIMATION_OF_TYPE, Boolean>, StopMotionState<CONTEXT_PROVIDER, ANIMATION_OF_TYPE, SPRITE_SHEET_TYPE>>> transitions = new ArrayList<>();

    // Counter of render ticks on this animation state
    private long ticksOnState = 0;

    /*
     * List of frame states.
     *
     * First pair parameter is the amount of ticks spent on this state.
     * Second pair parameter is the function call to the sprite sheet
     */
    private final List<Pair<Long, BiFunction<SPRITE_SHEET_TYPE, Integer, BufferedImage>>> animationFrames = new ArrayList<>();

    // Ticks until state is reset, maintained by additions to the frames
    private long frameResetTickCount = 0;

    // Whether or not a frame has been added to the state that declares this as the last frame to be rendered
    private boolean finalFrameAdded = false;

    // Whether or not this state can be interrupted by the transition to another state or if its animation must play out
    private boolean interruptable = true;

    /**
     * Instantiate a state in a stop motion animation.
     *
     * @param controller The animation controller
     */
    public StopMotionState(StopMotionController<CONTEXT_PROVIDER, ANIMATION_OF_TYPE, SPRITE_SHEET_TYPE> controller) {
        this.controller = controller;
    }

    /**
     * Check if a transition to another state is appropriate given the game state and animated entity.
     *
     * @param contextProvider Provider of a greater context of the state of the program
     * @param animatedEntity The object being animated
     * @return The state to transition to, null if current state remains appropriate
     */
    public StopMotionState<CONTEXT_PROVIDER, ANIMATION_OF_TYPE, SPRITE_SHEET_TYPE> checkTransition(CONTEXT_PROVIDER contextProvider, ANIMATION_OF_TYPE animatedEntity) {
        final Pair<BiFunction<CONTEXT_PROVIDER, ANIMATION_OF_TYPE, Boolean>, StopMotionState<CONTEXT_PROVIDER, ANIMATION_OF_TYPE, SPRITE_SHEET_TYPE>> nextState =
            transitions.stream().filter(transition -> transition.getFirst().apply(contextProvider, animatedEntity)).findFirst().orElse(null);

        if (nextState == null) {
            return null;
        }

        return nextState.getSecond();
    }

    /**
     * Transition to this state.
     */
    public void transitionTo() {
        ticksOnState = 0;
    }

    /**
     * Get the next sprite in this state. This is a stateful method.
     *
     * @return The next sprite
     */
    public BufferedImage nextSprite() {
        long frameIndex = ticksOnState % frameResetTickCount;

        if (finalFrameAdded) {
            frameIndex = ticksOnState;
        }

        long tickCounter = 0;

        Pair<Long, BiFunction<SPRITE_SHEET_TYPE, Integer, BufferedImage>> activeFrame = null;
        int frameNumber = -1;
        for (Pair<Long, BiFunction<SPRITE_SHEET_TYPE, Integer, BufferedImage>> animationFrame : animationFrames) {
            if (frameIndex >= tickCounter) {
                activeFrame = animationFrame;
                frameNumber++;
            }

            if (animationFrame.getFirst() != null) {
                tickCounter += animationFrame.getFirst();
            }
        }

        ticksOnState++;
        if (activeFrame == null) {
            return null;
        }

        return activeFrame.getSecond().apply(controller.getSpriteSheet(), frameNumber);
    }

    /**
     * Add a frame to the animation state.
     *
     * @param tickCount How many ticks this state will last.
     * @param spriteCallBack Function that takes a sprite sheet and an integer and gives a Sprite
     * @return Self so that this can be called in a builder-like fashion
     */
    public StopMotionState<CONTEXT_PROVIDER, ANIMATION_OF_TYPE, SPRITE_SHEET_TYPE> addFrame(Long tickCount, BiFunction<SPRITE_SHEET_TYPE, Integer, BufferedImage> spriteCallBack) {
        if (finalFrameAdded) {
            throw new IllegalStateException("Stop Motion state already has a final frame. Cannot add another frame.");
        }

        this.animationFrames.add(new Pair<>(tickCount, spriteCallBack));
        if (tickCount != null) {
            frameResetTickCount += tickCount;
        } else {
            finalFrameAdded = true;
        }

        return this;
    }

    public StopMotionState<CONTEXT_PROVIDER, ANIMATION_OF_TYPE, SPRITE_SHEET_TYPE> withInterruptableFlag(boolean interruptable) {
        this.interruptable = interruptable;
        return this;
    }

    /**
     * Create a state with a function call that is used to transition to it.
     *
     * @param transitionCheck Condition for when to transition to the state
     * @return The newly created state
     */
    public StopMotionState<CONTEXT_PROVIDER, ANIMATION_OF_TYPE, SPRITE_SHEET_TYPE> createTransitionState(BiFunction<CONTEXT_PROVIDER, ANIMATION_OF_TYPE, Boolean> transitionCheck) {
        return createTransition(transitionCheck, new StopMotionState<>(controller));
    }

    /**
     * Can the stop motion animation leave this state and move to the one it should be transitioned to?
     *
     * @return True if transition can happen
     */
    public boolean isTransitionFromOkay() {
        return interruptable || ticksOnState > frameResetTickCount;
    }

    /**
     * Create a transition between this state and another state.
     *
     * @param transitionCheck Condition for when to transition to the state
     * @param state The state to transition to when condition is met
     * @return The state that a transition was added for
     */
    public StopMotionState<CONTEXT_PROVIDER, ANIMATION_OF_TYPE, SPRITE_SHEET_TYPE> createTransition(
        BiFunction<CONTEXT_PROVIDER, ANIMATION_OF_TYPE, Boolean> transitionCheck,
        StopMotionState<CONTEXT_PROVIDER, ANIMATION_OF_TYPE, SPRITE_SHEET_TYPE> state
    ) {
        this.transitions.add(new Pair<>(transitionCheck, state));
        return state;
    }
}
