package com.andronikus.animation4j.stopmotion;

import com.andronikus.animation4j.spritesheet.SpriteSheet;
import com.andronikus.animation4j.statemachine.State;
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
public class StopMotionState<CONTEXT_PROVIDER, ANIMATION_OF_TYPE, SPRITE_SHEET_TYPE extends SpriteSheet> extends State<
    StopMotionState<CONTEXT_PROVIDER, ANIMATION_OF_TYPE, SPRITE_SHEET_TYPE>,
    CONTEXT_PROVIDER,
    ANIMATION_OF_TYPE
> {

    private final StopMotionController<CONTEXT_PROVIDER, ANIMATION_OF_TYPE, SPRITE_SHEET_TYPE> controller;

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

    /**
     * Instantiate a state in a stop motion animation.
     *
     * @param controller The animation controller
     */
    public StopMotionState(StopMotionController<CONTEXT_PROVIDER, ANIMATION_OF_TYPE, SPRITE_SHEET_TYPE> controller) {
        super();
        // TODO Hook this in
        this.controller = controller;
    }

    /**
     * Transition to this state.
     */
    @Override
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

    @Override
    protected boolean atleastOneCycleFinished() {
        return ticksOnState > frameResetTickCount;
    }

    @Override
    protected StopMotionState<CONTEXT_PROVIDER, ANIMATION_OF_TYPE, SPRITE_SHEET_TYPE> createBlankState() {
        return new StopMotionState<>(controller);
    }
}
