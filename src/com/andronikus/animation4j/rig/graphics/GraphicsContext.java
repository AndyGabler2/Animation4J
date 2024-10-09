package com.andronikus.animation4j.rig.graphics;

import java.awt.Graphics2D;
import javax.swing.JComponent;

/**
 * Information carrier about the graphics.
 *
 * @author Andronikus
 */
public class GraphicsContext {

    private Graphics2D graphics2d;
    private JComponent observer;

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
    public JComponent getObserver() {
        return observer;
    }

    /**
     * Set the image observer.
     *
     * @param observer The observer
     */
    public void setObserver(JComponent observer) {
        this.observer = observer;
    }

    /**
     * Get the height of the component this is being rendered on.
     *
     * @return The height
     */
    public int getComponentHeight() {
        return observer.getHeight();
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
