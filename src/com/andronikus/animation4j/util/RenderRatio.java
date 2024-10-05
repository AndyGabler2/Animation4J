package com.andronikus.animation4j.util;

/**
 * Ratio to the intended render sizes. The use case is if sprites or animations were designed with a certain screen
 * resolution, that they would be able to scale up or down to other resolutions.
 *
 * @author Andronikus
 */
public class RenderRatio {

    public static final int DEFAULT_RESOLUTION_WIDTH = 800;
    public static final int DEFAULT_RESOLUTION_HEIGHT = 433;

    private final int defaultResolutionWidth;
    private final int defaultResolutionHeight;
    private double widthScale = 1.0;
    private double heightScale = 1.0;

    /**
     * Instantiate ratio to the intended render sizes.
     *
     * @param aDefaultResolutionWidth The width of the resolution something was intended to be at
     * @param aDefaultResolutionHeight The height of the resolution something was intende to be at
     * @param actualResolutionWidth The width of the actual resolution
     * @param actualResolutionHeight The height of the actual resolution
     */
    public RenderRatio(int aDefaultResolutionWidth, int aDefaultResolutionHeight, int actualResolutionWidth, int actualResolutionHeight) {
        this(aDefaultResolutionWidth, aDefaultResolutionHeight);
        calculate(actualResolutionWidth, actualResolutionHeight);
    }

    private RenderRatio(int aDefaultResolutionWidth, int aDefaultResolutionHeight) {
        this.defaultResolutionWidth = aDefaultResolutionWidth;
        this.defaultResolutionHeight = aDefaultResolutionHeight;
    }

    /**
     * Calculate the render ratio.
     *
     * @param resolutionWidth The width of the screen
     * @param resolutionHeight The height of the screen
     */
    public void calculate(int resolutionWidth, int resolutionHeight) {
        widthScale = ((double) resolutionWidth) / (double) defaultResolutionWidth;
        heightScale = ((double) resolutionHeight) / (double) defaultResolutionHeight;
    }

    /**
     * Scale a horizontal value.
     *
     * @param horizontal Horizontal value
     * @return Scaled horizontal value
     */
    public int scaleHorizontal(int horizontal) {
        return (int) scaleHorizontal((double) horizontal);
    }

    /**
     * Scale a vertical value.
     *
     * @param vertical Vertical value
     * @return Scaled vertical value
     */
    public int scaleVertical(int vertical) {
        return (int) scaleVertical((double) vertical);
    }

    /**
     * Scale a horizontal value.
     *
     * @param horizontal Horizontal value
     * @return Scaled horizontal value
     */
    public long scaleHorizontal(long horizontal) {
        return (long) scaleHorizontal((double) horizontal);
    }

    /**
     * Scale a vertical value.
     *
     * @param vertical Vertical value
     * @return Scaled vertical value
     */
    public long scaleVertical(long vertical) {
        return (long) scaleVertical((double) vertical);
    }

    /**
     * Scale a horizontal value.
     *
     * @param horizontal Horizontal value
     * @return Scaled horizontal value
     */
    public double scaleHorizontal(double horizontal) {
        return horizontal * widthScale;
    }

    /**
     * Scale a vertical value.
     *
     * @param vertical Vertical value
     * @return Scaled vertical value
     */
    public double scaleVertical(double vertical) {
        return vertical * heightScale;
    }

    /**
     * Deep copy.
     *
     * @return The copy
     */
    public RenderRatio copy() {
        final RenderRatio copy = new RenderRatio(defaultResolutionWidth, defaultResolutionHeight);
        copy.widthScale = this.widthScale;
        copy.heightScale = this.heightScale;
        return copy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof RenderRatio)) {
            return false;
        }

        final RenderRatio inputRatio = (RenderRatio) object;
        return inputRatio.widthScale == widthScale && inputRatio.heightScale == heightScale;
    }

    /**
     * Get the width scale of the render ratio.
     *
     * @return Width scale
     */
    public double getWidthScale() {
        return widthScale;
    }

    /**
     * Get the height scale of the render ratio.
     *
     * @return Height scale
     */
    public double getHeightScale() {
        return heightScale;
    }
}
