package com.andronikus.animation4j.stopmotion;

import com.andronikus.animation4j.spritesheet.SpriteSheet;
import com.andronikus.animation4j.statemachine.DeterministicFiniteAutomata;

import java.awt.image.BufferedImage;

/**
 * Controller for stop motion animation of some kind of object.
 *
 * @param <ANIMATION_OF_TYPE> Type of object being animated
 * @param <SPRITE_SHEET_TYPE> Sprite sheet to pull animation states from
 * @author Andronikus
 */
public abstract class StopMotionController<CONTEXT_PROVIDER, ANIMATION_OF_TYPE, SPRITE_SHEET_TYPE extends SpriteSheet>
    extends DeterministicFiniteAutomata<
        CONTEXT_PROVIDER, ANIMATION_OF_TYPE,
        StopMotionState<CONTEXT_PROVIDER, ANIMATION_OF_TYPE, SPRITE_SHEET_TYPE>,
        BufferedImage
    > {

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
        super();
        this.spriteSheet = spriteSheet;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedImage doWithNextState(
        StopMotionState<CONTEXT_PROVIDER, ANIMATION_OF_TYPE, SPRITE_SHEET_TYPE> state,
        CONTEXT_PROVIDER contextObject,
        ANIMATION_OF_TYPE animatedObject,
        Object... parameters
    ) {
        return state.nextSprite();
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
        return nextAction(contextProvider, animatedEntity);
    }

    /**
     * Package private access to the sprite sheet.
     *
     * @return The sprite sheet
     */
    SPRITE_SHEET_TYPE getSpriteSheet() {
        return spriteSheet;
    }
}
