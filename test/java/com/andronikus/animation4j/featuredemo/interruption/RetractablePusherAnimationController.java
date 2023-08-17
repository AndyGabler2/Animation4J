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
         * Nuetral -> Extending (interruptible = false) (v)
         * Nuetral -> Breaking (interruptible = true) (v)

         * Extending -> Extended (interruptible = false) (v)
         * Extending -> Breaking (interruptible = true) (v)

         * Extended -> Breaking (interruptible = true) (v)
         * Extended -> Retracting (interruptible = true) (v)

         * Retracting -> Neutral (interruptible = false) (v)
         * Retracting -> Breaking (interruptible = true) (v)

         * Breaking -> Broken (interruptible = false) (v)
         * Breaking -> Neutral (interruptible = true) (v)

         * Broken -> Neutral (interruptible = true)
         */
        final Animation<Object, RetractablePusher> neutralState = createAnimation()
            .withInterruptibleFlag(false);

        final Animation<Object, RetractablePusher> extendingState = neutralState.createTransitionState((obj, pusher) -> pusher.isExtending())
            .withInterruptibleFlag(false);

        final Animation<Object, RetractablePusher> breakingState = neutralState.createTransitionState((obj, pusher) -> pusher.isExtending(), true)
            .withInterruptibleFlag(false);

        extendingState.createTransition((obj, pusher) -> pusher.isBroken(), true, breakingState);
        breakingState.createTransition((obj, pusher) -> !pusher.isBroken(), true, neutralState);

        final Animation<Object, RetractablePusher> extendedState = extendingState.createTransitionState((obj, pusher) -> true);

        final Animation<Object, RetractablePusher> retractingState = extendedState.createTransitionState((obj, pusher) -> !pusher.isExtending())
            .withInterruptibleFlag(false);
        extendedState.createTransition((obj, pusher) -> pusher.isBroken(), breakingState);

        retractingState.createTransition((obj, pusher) -> true, neutralState);
        retractingState.createTransition((obj, pusher) -> pusher.isBroken(), true, breakingState);

        final Animation<Object, RetractablePusher> brokenState = breakingState.createTransitionState((obj, pusher) -> true);

        brokenState.createTransition((obj, pusher) -> !pusher.isBroken(), neutralState);

        return neutralState.finishAnimating();
    }

    @Override
    public boolean checkIfObjectIsRoot(RetractablePusher object) {
        return true;
    }
}
