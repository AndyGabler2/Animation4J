package com.andronikus.animation4j.featuredemo.interruption;

import com.andronikus.animation4j.stopmotion.StopMotionController;
import com.andronikus.animation4j.stopmotion.StopMotionState;

public class PusherArmStopMotionController extends StopMotionController<Object, RetractablePusher, PusherArmSpriteSheet> {

    public PusherArmStopMotionController() {
        super(new PusherArmSpriteSheet());
    }

    @Override
    protected StopMotionState<Object, RetractablePusher, PusherArmSpriteSheet> buildInitialStatesAndTransitions() {
        return null;
    }

    @Override
    public boolean checkIfObjectIsRoot(RetractablePusher object) {
        return true;
    }
}
