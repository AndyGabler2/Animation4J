package com.andronikus.animation4j.animation;

/**
 * A goal-post in an animation. Used to calculate, given the current state of the animation, what its next state is
 * based on where the state needs to be.
 *
 * @author Andronikus
 */
public class KeyFrame {
    private double jointRotation;
    private boolean reflectX;
    private boolean reflectY;
    private Long duration; // Null means end frame. Cyclic frame if no null.
    private boolean snapTo;

    /**
     * Get rotation of the joint.
     *
     * @return Joint rotation
     */
    public double getJointRotation() {
        return jointRotation;
    }

    /**
     * Set rotation of the joint.
     *
     * @param jointRotation Joint rotation
     */
    public void setJointRotation(double jointRotation) {
        this.jointRotation = jointRotation;
    }

    /**
     * Whether the X is reflected upon reaching this keyframe.
     *
     * @return Reflect X
     */
    public boolean isReflectX() {
        return reflectX;
    }

    /**
     * Whether the X is reflected upon reaching this keyframe.
     *
     * @param reflectX Reflect X
     */
    public void setReflectX(boolean reflectX) {
        this.reflectX = reflectX;
    }

    /**
     * Whether the Y is reflected upon reaching this keyframe.
     *
     * @return Reflect Y
     */
    public boolean isReflectY() {
        return reflectY;
    }

    /**
     * Whether the Y is reflected upon reaching this keyframe.
     *
     * @param reflectY Reflect Y
     */
    public void setReflectY(boolean reflectY) {
        this.reflectY = reflectY;
    }

    /**
     * Get duration of the keyframe.
     *
     * @return The duration
     */
    public Long getDuration() {
        return duration;
    }

    /**
     * Set duration of the keyframe.
     *
     * @param duration The duration.
     */
    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public boolean isSnapTo() {
        return snapTo;
    }

    public void setSnapTo(boolean snapTo) {
        this.snapTo = snapTo;
    }
}
