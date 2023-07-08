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
    private final AnimationLimb<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> parent;
    private final int parentJointRegistrationPosition;
    private final AnimationLimb<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> limb;

    public AnimationJoint(
        AnimationLimb<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> parent,
        int jointRegistrationPosition,
        AnimationLimb<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> limb
    ) {
        Objects.requireNonNull(limb, "Joint must have non-null limb.");
        this.parent = parent;
        parentJointRegistrationPosition = jointRegistrationPosition;
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

    /**
     * Get accessor for fields on the limb that may be adjusted by the Animation4J library after it has been finalized.
     *
     * @deprecated Framework use only. Anything set will be overriden.
     * @return Accessor for fields that are modified during rendering after finalized
     */
    @Deprecated
    public StateAccess stateAccessor() {
        if (!parent.isFinalized()) {
            throw new IllegalStateException("Attempted to get accessor for joint state when the joint has not been finalized.");
        }
        // State access should inherently have an instance of "this" with proper fields in scope
        return new StateAccess();
    }

    /**
     * Class with methods that can only be used when the limb has been finalized.
     */
    public class StateAccess {

        /**
         * Set the fulcrum distance multiplier.
         *
         * @param fulcrumDistanceMultiplier The fulcrum distance multiplier
         */
        public void setFulcrumDistanceMultiplier(double fulcrumDistanceMultiplier) {
            parent.stateAccessor().setDistanceFromFulcrumForJoint(parentJointRegistrationPosition, fulcrumDistanceMultiplier);
        }
    }
}
