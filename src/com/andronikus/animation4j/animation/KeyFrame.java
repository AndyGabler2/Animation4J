package com.andronikus.animation4j.animation;

public class KeyFrame {
    private double jointRotation;
    private boolean reflectX;
    private boolean reflectY;
    private Long duration; // Null means end frame. Cyclic frame.
    private boolean snapTo;

    public double getJointRotation() {
        return jointRotation;
    }

    public void setJointRotation(double jointRotation) {
        this.jointRotation = jointRotation;
    }

    public boolean isReflectX() {
        return reflectX;
    }

    public void setReflectX(boolean reflectX) {
        this.reflectX = reflectX;
    }

    public boolean isReflectY() {
        return reflectY;
    }

    public void setReflectY(boolean reflectY) {
        this.reflectY = reflectY;
    }

    public Long getDuration() {
        return duration;
    }

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
