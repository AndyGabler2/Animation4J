package com.andronikus.animation4j.featuredemo.interruption;

import com.andronikus.animation4j.animation.Animation;
import com.andronikus.animation4j.animation.AnimationController;

public class RetractablePusherAnimationController extends AnimationController<Object, RetractablePusher> {

    private static final double BROKEN_ARM_ROTATION = -Math.PI * 1 / 3;

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

        final Animation<Object, RetractablePusher> extendedState = extendingState.createTransitionState(extendingState.completeCycleTransition());

        final Animation<Object, RetractablePusher> retractingState = extendedState.createTransitionState((obj, pusher) -> !pusher.isExtending())
            .withInterruptibleFlag(false);
        extendedState.createTransition((obj, pusher) -> pusher.isBroken(), breakingState);

        retractingState.createTransition(retractingState.completeCycleTransition(), neutralState);
        retractingState.createTransition((obj, pusher) -> pusher.isBroken(), true, breakingState);

        final Animation<Object, RetractablePusher> brokenState = breakingState.createTransitionState(breakingState.completeCycleTransition());

        brokenState.createTransition((obj, pusher) -> !pusher.isBroken(), neutralState);

        extendingState
            .keyFrameBuilder()
                .withJoint((short) 1)
                .withDuration(15L)
                .withFulcrumDistanceMultiplier(1.0)
            .buildKeyFrame()
            .keyFrameBuilder()
                .withJoint((short) 1)
                .withDuration(null)
                .withFulcrumDistanceMultiplier(7.0)
            .buildKeyFrame()
            .finishAnimating();
        extendedState
            .keyFrameBuilder()
                .withJoint((short) 1)
                .withDuration(15L)
                .withFulcrumDistanceMultiplier(7.0)
            .buildKeyFrame()
            .keyFrameBuilder()
                .withJoint((short) 1)
                .withDuration(null)
                .withFulcrumDistanceMultiplier(7.0)
            .buildKeyFrame()
            .finishAnimating();
        retractingState
            .keyFrameBuilder()
                .withJoint((short) 1)
                .withDuration(15L)
                .withFulcrumDistanceMultiplier(7.0)
            .buildKeyFrame()
            .keyFrameBuilder()
                .withJoint((short) 1)
                .withDuration(null)
                .withFulcrumDistanceMultiplier(1.0)
            .buildKeyFrame()
            .finishAnimating();
        breakingState
            .keyFrameBuilder()
                .withJoint((short) 1)
                .withJointRotation(0)
                .withDuration(15L)
            .buildKeyFrame()
            .keyFrameBuilder()
                .withJoint((short) 1)
                .withJointRotation(BROKEN_ARM_ROTATION)
                .withDuration(null)
            .buildKeyFrame()
            //.withRotationalKeyFrame(5L, 0)
            //.withRotationalKeyFrame(null, Math.PI * 3 / 2)
            .finishAnimating();
        brokenState
            .keyFrameBuilder()
                .withJoint((short) 1)
                .withJointRotation(BROKEN_ARM_ROTATION)
                .withDuration(1L)
            .buildKeyFrame()
            .keyFrameBuilder()
                .withJoint((short) 1)
                .withJointRotation(BROKEN_ARM_ROTATION)
                .withDuration(null)
            .buildKeyFrame()
            //.withRotationalKeyFrame(1L, Math.PI / 2)
            //.withRotationalKeyFrame(null, Math.PI / 2)
            .finishAnimating();
        return neutralState
            .keyFrameBuilder()
                .withJoint((short) 1)
                .withSnapTo(true)
                .withFulcrumDistanceMultiplier(1.0)
                .withJointRotation(0)
                .withDuration(1L)
            .buildKeyFrame()
            .keyFrameBuilder()
                .withJoint((short) 1)
                .withFulcrumDistanceMultiplier(1.0)
                .withJointRotation(0)
                .withDuration(null)
            .buildKeyFrame()
            //.withRotationalKeyFrame(1L, 0)
            //.withRotationalKeyFrame(null, 0)
            .finishAnimating();
    }

    @Override
    public boolean checkIfObjectIsRoot(RetractablePusher object) {
        return true;
    }
}
