package com.andronikus.animation4j.rig.scenario;

import com.andronikus.animation4j.rig.AnimationLimb;
import com.andronikus.animation4j.rig.AnimationRig;
import com.andronikus.animation4j.stopmotion.scenario.QwertyArmStopMotionController;
import com.andronikus.animation4j.stopmotion.scenario.QwertyHeadStopMotionController;
import com.andronikus.animation4j.stopmotion.scenario.QwertyState;
import com.andronikus.animation4j.util.ImagesUtil;

import java.util.Collections;
import java.util.List;

public class QwertyAnimationRig extends AnimationRig<Object, QwertyState> {

    public QwertyAnimationRig(QwertyState state) {
        super(state);
    }

    @Override
    protected List<AnimationLimb<Object, QwertyState>> buildLimbs(QwertyState state) {
        final QwertyLimb torso = (QwertyLimb) new QwertyLimb()
            .setWidth(32)
            .setHeight(64)
            .setImage(ImagesUtil.getImage("torso.png"))
            .finishRigging();

        final AnimationLimb<Object, QwertyState> neck = torso.registerJoint((short) 1, Math.PI / 2, 34, true)
            .getLimb()
            .setWidth(16)
            .setHeight(16)
            .setImage(ImagesUtil.getImage("neck.png"))
            .finishRigging();

        neck.registerJoint((short)2, Math.PI / 2, 18, false)
            .getLimb()
            .setHeight(32)
            .setWidth(48)
            .setStopMotionController(new QwertyHeadStopMotionController())
            .finishRigging();

        final AnimationLimb<Object, QwertyState> leftArmTop = torso.registerJoint((short)3, Math.PI * 19 / 32, 32, false)
            .getLimb()
            .setWidth(32)
            .setHeight(64)
            .setStopMotionController(new QwertyArmStopMotionController())
            .finishRigging();

        leftArmTop.registerJoint((short)4, -Math.PI / 32 * 19, 35, false)
            .getLimb()
            .setWidth(16)
            .setHeight(52)
            .setImage(ImagesUtil.getImage("armbottom.png"))
            .finishRigging();

        final AnimationLimb<Object, QwertyState> rightArmTop = torso.registerJoint((short)5, Math.PI * 13 / 32, 32, false)
            .getLimb()
            .setWidth(32)
            .setHeight(64)
            .setStopMotionController(new QwertyArmStopMotionController())
            .setReflectX(true)
            .finishRigging();

        rightArmTop.registerJoint((short)6, -Math.PI / 32 * 13, 35, false)
            .getLimb()
            .setWidth(16)
            .setHeight(52)
            .setImage(ImagesUtil.getImage("armbottom.png"))
            .finishRigging();

        final AnimationLimb<Object, QwertyState> leftLegTop = torso.registerJoint((short)7, Math.PI * 23 / 16, 32, true)
            .getLimb()
            .setWidth(16)
            .setHeight(64)
            .setImage(ImagesUtil.getImage("legtop.png"))
            .setReflectX(true)
            .finishRigging();

        leftLegTop.registerJoint((short)8, Math.PI / -2, 32, false)
            .getLimb()
            .setReflectX(true)
            .setWidth(16)
            .setHeight(52)
            .setImage(ImagesUtil.getImage("legbottom.png"))
            .finishRigging();

        final AnimationLimb<Object, QwertyState> rightLegTop = torso.registerJoint((short)9, Math.PI * 25 / 16, 32, true)
            .getLimb()
            .setWidth(16)
            .setHeight(64)
            .setImage(ImagesUtil.getImage("legtop.png"))
            .setReflectX(true)
            .finishRigging();

        rightLegTop.registerJoint((short)10, Math.PI / -2, 32, false)
            .getLimb()
            .setWidth(16)
            .setHeight(52)
            .setImage(ImagesUtil.getImage("legbottom.png"))
            .finishRigging();

        return Collections.singletonList(torso);
    }

    @Override
    public boolean checkIfObjectIsAnimatedEntity(QwertyState object) {
        return true;
    }

    private class QwertyLimb extends AnimationLimb<Object, QwertyState> {}
}
