package com.andronikus.animation4j.rig;

import java.awt.Graphics2D;
import java.util.Objects;

public class AnimationJoint<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> {

    private double rotation;
    private final AnimationLimb<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> limb;

    public AnimationJoint(AnimationLimb<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> limb) {
        Objects.requireNonNull(limb, "Joint must have non-null limb.");
        this.limb = limb;
    }

    public AnimationLimb<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> getLimb() {
        return limb;
    }

    public double getRotation() {
        return rotation;
    }

    public AnimationJoint<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> setRotation(double rotation) {
        this.rotation = rotation;
        return this;
    }
}
