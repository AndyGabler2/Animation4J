package com.andronikus.animation4j.animation;

public class AnimationKeyFrameState {
    private long tickCounter;
    private KeyFrame activeFrame;
    private KeyFrame nextFrame;
    private int frameIndex;
    private boolean renderedFirstFrame = false;

    public long getTickCounter() {
        return tickCounter;
    }

    public void setTickCounter(long tickCounter) {
        this.tickCounter = tickCounter;
    }

    public KeyFrame getActiveFrame() {
        return activeFrame;
    }

    public void setActiveFrame(KeyFrame activeFrame) {
        this.activeFrame = activeFrame;
    }

    public KeyFrame getNextFrame() {
        return nextFrame;
    }

    public void setNextFrame(KeyFrame nextFrame) {
        this.nextFrame = nextFrame;
    }

    public int getFrameIndex() {
        return frameIndex;
    }

    public void setFrameIndex(int frameIndex) {
        this.frameIndex = frameIndex;
    }

    public boolean isRenderedFirstFrame() {
        return renderedFirstFrame;
    }

    public void setRenderedFirstFrame(boolean renderedFirstFrame) {
        this.renderedFirstFrame = renderedFirstFrame;
    }
}
