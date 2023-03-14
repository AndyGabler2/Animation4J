package com.andronikus.animation4j.featuredemo.interruption;

import com.andronikus.animation4j.stopmotion.StopMotionController;
import com.andronikus.animation4j.stopmotion.StopMotionState;

public class PusherBaseStopMotionController extends StopMotionController<Object, RetractablePusher, PusherBaseSpriteSheet> {

    public PusherBaseStopMotionController() {
        super(new PusherBaseSpriteSheet());
    }

    @Override
    protected StopMotionState<Object, RetractablePusher, PusherBaseSpriteSheet> buildInitialStatesAndTransitions() {
        /*
        The Stop Motion Controllers shall have states:

Nuetral -> Breaking

Breaking -> Broken (interruptible = false)
Breaking -> Nuetral (interruptible = true)

Broken -> Nuetral
         */

        final StopMotionState<Object, RetractablePusher, PusherBaseSpriteSheet> neutralState = new StopMotionState<>(this)
            .addFrame(1L, (spriteSheet, state) -> spriteSheet.getNeutralSprite())
            .addFrame(null, (spriteSheet, state) -> spriteSheet.getNeutralSprite());

        final StopMotionState<Object, RetractablePusher, PusherBaseSpriteSheet> breakingState = neutralState
            .createTransitionState((o, retractablePusher) -> retractablePusher.isBroken());

        return neutralState;
    }

    @Override
    public boolean checkIfObjectIsRoot(RetractablePusher object) {
        return true;
    }
}
