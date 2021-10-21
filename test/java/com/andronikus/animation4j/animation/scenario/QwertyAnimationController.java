package com.andronikus.animation4j.animation.scenario;

import com.andronikus.animation4j.animation.Animation;
import com.andronikus.animation4j.animation.AnimationController;
import com.andronikus.animation4j.rig.scenario.QwertyAnimationRig;
import com.andronikus.animation4j.stopmotion.scenario.QwertyState;

public class QwertyAnimationController extends AnimationController<Object, QwertyState> {

    public QwertyAnimationController(QwertyState state) {
        super(new QwertyAnimationRig(state));
    }

    @Override
    protected Animation<Object, QwertyState> buildInitialStatesAndTransitions() {
        final Animation<Object, QwertyState> idleAnimation = createAnimation()
            .withRig(getRig())
            .withInterruptableFlag(false)
            .keyFrameBuilder()
                .withJoint((short)3)
                .withDuration(30L)
                .withJointRotation(0)
            .buildKeyFrame()
            .keyFrameBuilder()
                .withJoint((short)3)
                .withDuration(null)
                .withJointRotation(-Math.PI / 2)
            .buildKeyFrame();

        idleAnimation.createTransitionState((o, qwertyState) -> true)
            .withRig(getRig())
            .withInterruptableFlag(true)
            .keyFrameBuilder()
                .withJoint((short)4)
                .withDuration(20L)
                .withJointRotation(0)
            .buildKeyFrame()
            .keyFrameBuilder()
                .withJoint((short)4)
                .withDuration(20L)
                .withJointRotation(Math.PI / -2)
            .buildKeyFrame()
            .finishAnimating();
        return idleAnimation.finishAnimating();
    }

    @Override
    public boolean checkIfObjectIsRoot(QwertyState object) {
        return true;
    }
}
