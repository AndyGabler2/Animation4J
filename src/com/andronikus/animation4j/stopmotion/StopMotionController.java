package com.andronikus.animation4j.stopmotion;

import com.andronikus.animation4j.spritesheet.SpriteSheet;

import java.awt.image.BufferedImage;

/**
 * Controller for stop motion animation of some kind of object.
 *
 * @param <ANIMATION_OF_TYPE> Type of object being animated
 * @param <SPRITE_SHEET_TYPE> Sprite sheet to pull animation states from
 * @author Andronikus
 */
public abstract class StopMotionController<CONTEXT_PROVIDER, ANIMATION_OF_TYPE, SPRITE_SHEET_TYPE extends SpriteSheet> {

    private StopMotionState<CONTEXT_PROVIDER, ANIMATION_OF_TYPE, SPRITE_SHEET_TYPE> initialState;
    private StopMotionState<CONTEXT_PROVIDER, ANIMATION_OF_TYPE, SPRITE_SHEET_TYPE> activeState;
    private StopMotionState<CONTEXT_PROVIDER, ANIMATION_OF_TYPE, SPRITE_SHEET_TYPE> realState;

    private final SPRITE_SHEET_TYPE spriteSheet;

    /**
     * Instantiate a controller for stop motion animation of some kind of object.
     *
     * @param spriteSheet The sprite sheet to use in the animations
     */
    public StopMotionController(SPRITE_SHEET_TYPE spriteSheet) {
        this.spriteSheet = spriteSheet;
        initialState = buildInitialStatesAndTransitions();
        this.activeState = initialState;
        this.realState = initialState;
    }

    /**
     * For an animated entity, which is presumed to be the entity this controller is for, and a context provider, get the
     * next sprite that should be rendered in the animation.
     *
     * @param contextProvider Provider of a greater context of the state of the program
     * @param animatedEntity The animated entity
     * @return The sprite to render
     */
    public BufferedImage nextSprite(CONTEXT_PROVIDER contextProvider, ANIMATION_OF_TYPE animatedEntity) {
        final StopMotionState<CONTEXT_PROVIDER, ANIMATION_OF_TYPE, SPRITE_SHEET_TYPE> nextState =
            realState.checkTransition(contextProvider, animatedEntity);

        if (nextState != null) {
            realState = nextState;
            // TODO, technically, this shouldn't be here but meh, just resets a counter
            nextState.transitionTo();
        }

        // If the active state is not the real state, let's see if we can't make it so
        if (activeState != realState && activeState.isTransitionFromOkay()) {
            activeState = realState;
        }

        return activeState.nextSprite();
    }

    /**
     * Package private access to the sprite sheet.
     *
     * @return The sprite sheet
     */
    SPRITE_SHEET_TYPE getSpriteSheet() {
        return spriteSheet;
    }

    /**
     * Build the initial stop motion state. This is expected to have, in its transitions, every state that is possible.
     * Note, states never return to their initial state unless the a child state makes a cyclic transition. Therefore,
     * the resulting initial state must include every transition and each transition must be deliberate.
     *
     * @return The initial animation state
     */
    protected abstract StopMotionState<CONTEXT_PROVIDER, ANIMATION_OF_TYPE, SPRITE_SHEET_TYPE> buildInitialStatesAndTransitions();

    /**
     * Check if the object we are attempting to render and get the next sprite for is the correct sprite to render. This
     * is appropriate and essential to call since the sprite retrieval function is stateful, so it is important that it
     * is only called when intended.
     *
     * @param object The object that may or may not be the object this controller is animating
     * @return Whether or not this controller animates given object
     */
    public abstract boolean checkIfObjectIsAnimatedEntity(ANIMATION_OF_TYPE object);
}
