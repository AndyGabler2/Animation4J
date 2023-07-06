package com.andronikus.animation4j.featuredemo.interruption;

import com.andronikus.animation4j.animation.Animation;
import com.andronikus.animation4j.animation.AnimationController;
import com.andronikus.animation4j.rig.AnimationRig;

public class RetractablePusherAnimationController extends AnimationController<Object, RetractablePusher> {

    public RetractablePusherAnimationController(AnimationRig<Object, RetractablePusher> rig) {
        super(rig);
    }

    @Override
    protected Animation<Object, RetractablePusher> buildInitialStatesAndTransitions() {
        return null;
    }

    @Override
    public boolean checkIfObjectIsRoot(RetractablePusher object) {
        return true;
    }
}
