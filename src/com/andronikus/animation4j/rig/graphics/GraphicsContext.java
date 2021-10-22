package com.andronikus.animation4j.rig.graphics;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

/**
 * Information carrier about the graphics.
 *
 * @author Andronikus
 */
public class GraphicsContext {

    private Graphics2D graphics2d;
    private ImageObserver observer;
    private int componentHeight;

    /**
     * Set the graphics being used.
     *
     * @param graphics2d The graphics
     */
    public void setGraphics2d(Graphics2D graphics2d) {
        this.graphics2d = graphics2d;
    }

    /**
     * Get the image observer.
     *
     * @return The observer
     */
    public ImageObserver getObserver() {
        return observer;
    }

    /**
     * Set the image observer.
     *
     * @param observer The observer
     */
    public void setObserver(ImageObserver observer) {
        this.observer = observer;
    }

    /**
     * Get the height of the component this is being rendered on.
     *
     * @return The height
     */
    public int getComponentHeight() {
        return componentHeight;
    }

    /**
     * Set the height of the component this is being rendered on.
     *
     * @param componentHeight The height
     */
    public void setComponentHeight(int componentHeight) {
        this.componentHeight = componentHeight;
    }

    /**
     * Create a new graphical instance from the current graphics.
     *
     * @return The graphics
     */
    public Graphics2D createGraphicalInstance() {
        return (Graphics2D) graphics2d.create();
    }
}
