package com.andronikus.animation4j.rig;

import java.util.Objects;

/**
 * A link from one {@link AnimationLimb} to the next {@link AnimationLimb}.
 *
 * @param <CONTEXT_OBJECT_TYPE> Type of object providing greater context
 * @param <ANIMATION_OF_TYPE> Type of object being animated
 * @author Andronikus
 */
public class AnimationJoint<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> {

    private double rotation;
    private final AnimationLimb<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> limb;

    public AnimationJoint(AnimationLimb<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> limb) {
        Objects.requireNonNull(limb, "Joint must have non-null limb.");
        this.limb = limb;
    }

    /**
     * Get the limb this joint is going towards.
     *
     * @return The limb
     */
    public AnimationLimb<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> getLimb() {
        return limb;
    }

    /**
     * Get the rotation of the joint.
     *
     * @return The rotation
     */
    public double getRotation() {
        return rotation;
    }

    /**
     * Set the rotation of the joint.
     *
     * @param rotation The joint
     * @return Self
     */
    public AnimationJoint<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> setRotation(double rotation) {
        this.rotation = rotation;
        return this;
    }
}
