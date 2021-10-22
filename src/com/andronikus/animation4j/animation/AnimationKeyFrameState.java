package com.andronikus.animation4j.animation;

/**
 * Object to hold the state of the current key frame.
 *
 * @author Andronikus
 */
public class AnimationKeyFrameState {
    private long tickCounter;
    private KeyFrame activeFrame;
    private KeyFrame nextFrame;
    private int frameIndex;
    private boolean renderedFirstFrame = false;

    /**
     * Get the tick counter.
     *
     * @return Tick counter
     */
    public long getTickCounter() {
        return tickCounter;
    }

    /**
     * Set the tick counter.
     *
     * @param tickCounter Tick counter
     */
    public void setTickCounter(long tickCounter) {
        this.tickCounter = tickCounter;
    }

    /**
     * Get the active frame.
     *
     * @return The active frame
     */
    public KeyFrame getActiveFrame() {
        return activeFrame;
    }

    /**
     * Get the current frame.
     *
     * @param activeFrame The current frame
     */
    public void setActiveFrame(KeyFrame activeFrame) {
        this.activeFrame = activeFrame;
    }

    /**
     * Get the next frame.
     *
     * @return The next frame
     */
    public KeyFrame getNextFrame() {
        return nextFrame;
    }

    /**
     * Set the next frame.
     *
     * @param nextFrame The next frame
     */
    public void setNextFrame(KeyFrame nextFrame) {
        this.nextFrame = nextFrame;
    }

    /**
     * Get the frame index.
     *
     * @return Frame index
     */
    public int getFrameIndex() {
        return frameIndex;
    }

    /**
     * Set the frame index.
     *
     * @param frameIndex Frame index
     */
    public void setFrameIndex(int frameIndex) {
        this.frameIndex = frameIndex;
    }

    /**
     * Get whether the first frame has been rendered.
     *
     * @return First frame rendered?
     */
    public boolean isRenderedFirstFrame() {
        return renderedFirstFrame;
    }

    /**
     * Set whether the first frame has been rendered.
     *
     * @param renderedFirstFrame First frame rendered?
     */
    public void setRenderedFirstFrame(boolean renderedFirstFrame) {
        this.renderedFirstFrame = renderedFirstFrame;
    }
}
