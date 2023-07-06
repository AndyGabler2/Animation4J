package com.andronikus.animation4j.featuredemo.interruption;

import com.andronikus.animation4j.rig.AnimationLimb;
import com.andronikus.animation4j.rig.AnimationRig;

import java.util.Collections;
import java.util.List;

public class RetractablePusherRig extends AnimationRig<Object, RetractablePusher> {

    public RetractablePusherRig(RetractablePusher animatedObject) {
        super(animatedObject);
    }

    @Override
    protected List<AnimationLimb<Object, RetractablePusher>> buildLimbs(RetractablePusher animatedObject) {
        final PusherLimb baseLimb = (PusherLimb) new PusherLimb()
            .setWidth(239)
            .setHeight(320)
            .setStopMotionController(new PusherBaseStopMotionController())
            .finishRigging();

        baseLimb.registerJoint((short) 1, 0, 319, true)
            .getLimb()
            .setWidth(319)
            .setHeight(320)
            .setStopMotionController(new PusherArmStopMotionController())
            .finishRigging();

        return Collections.singletonList(baseLimb);
    }

    @Override
    public boolean checkIfObjectIsAnimatedEntity(RetractablePusher object) {
        return true;
    }

    private static class PusherLimb extends AnimationLimb<Object, RetractablePusher> {}
}
