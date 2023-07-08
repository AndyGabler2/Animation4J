package com.andronikus.animation4j.featuredemo.interruption;

import com.andronikus.animation4j.animation.Animation;
import com.andronikus.animation4j.animation.AnimationController;

public class RetractablePusherAnimationController extends AnimationController<Object, RetractablePusher> {

    public RetractablePusherAnimationController(RetractablePusher pusher) {
        super(new RetractablePusherRig(pusher));
    }

    @Override
    protected Animation<Object, RetractablePusher> buildInitialStatesAndTransitions() {
        /*
         * Nuetral -> Extending (interruptible = false)
         * Nuetral -> Breaking (interruptible = true)

         * Extending -> Extended (interruptible = false)
         * Extending -> Breaking (interruptible = true)

         * Extended -> Breaking (interruptible = true)
         * Extended -> Retracting (interruptible = true)

         * Retracting -> Nuetral (interruptible = false)
         * Retracting -> Breaking (interruptible = true)

         * Breaking -> Broken (interruptible = false)
         * Breaking -> Nuetral (interruptible = true)

         * Broken -> Nuetral (interruptible = true)
         */
        final Animation<Object, RetractablePusher> neutralState = createAnimation();

        return neutralState.finishAnimating();
    }

    @Override
    public boolean checkIfObjectIsRoot(RetractablePusher object) {
        return true;
    }
}
