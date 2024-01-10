package com.andronikus.animation4j.featuredemo.interruption;

import com.andronikus.animation4j.stopmotion.StopMotionController;
import com.andronikus.animation4j.stopmotion.StopMotionState;

public class PusherArmStopMotionController extends StopMotionController<Object, RetractablePusher, PusherArmSpriteSheet> {

    public PusherArmStopMotionController() {
        super(new PusherArmSpriteSheet());
    }

    @Override
    protected StopMotionState<Object, RetractablePusher, PusherArmSpriteSheet> buildInitialStatesAndTransitions() {
        /*
         * The Stop Motion Controllers shall have states:
         *
         * Neutral -> Breaking
         *
         * Breaking -> Broken (interruptible = false)
         * Breaking -> Neutral (interruptible = true)
         *
         * Broken -> Neutral
         */
        final StopMotionState<Object, RetractablePusher, PusherArmSpriteSheet> neutralState = new StopMotionState<>(this)
            .addFrame(1L, (spriteSheet, state) -> spriteSheet.getNeutralSprite())
            .addFrame(null, (spriteSheet, state) -> spriteSheet.getNeutralSprite());

        final StopMotionState<Object, RetractablePusher, PusherArmSpriteSheet> breakingState = neutralState
            .createTransitionState((o, retractablePusher) -> retractablePusher.isBroken());

        breakingState.withInterruptibleFlag(false);

        final StopMotionState<Object, RetractablePusher, PusherArmSpriteSheet> brokenState = breakingState
            .completeCycleState();
        breakingState.createTransition(
            (o, retractablePusher) -> !retractablePusher.isBroken(),
            true,
            neutralState
        );

        brokenState.createTransition((o, retractablePusher) -> !retractablePusher.isBroken(), neutralState);

        brokenState
            .addFrame(1L, (spriteSheet, state) -> spriteSheet.getBrokenSprite())
            .addFrame(null, (spriteSheet, state) -> spriteSheet.getBrokenSprite());

        breakingState
            .addFrame(7L, PusherArmSpriteSheet::getBreakingSprite)
            .addFrame(6L, PusherArmSpriteSheet::getBreakingSprite)
            .addFrame(6L, PusherArmSpriteSheet::getBreakingSprite)
            .addFrame(4L, PusherArmSpriteSheet::getBreakingSprite)
            .addFrame(4L, PusherArmSpriteSheet::getBreakingSprite)
            .addFrame(3L, PusherArmSpriteSheet::getBreakingSprite)
            .addFrame(3L, PusherArmSpriteSheet::getBreakingSprite)
            .addFrame(2L, PusherArmSpriteSheet::getBreakingSprite)
            .addFrame(1L, PusherArmSpriteSheet::getBreakingSprite)
            .addFrame(null, PusherArmSpriteSheet::getBreakingSprite);

        return neutralState;
    }

    @Override
    public boolean checkIfObjectIsRoot(RetractablePusher object) {
        return true;
    }
}
