package com.andronikus.animation4j.rig.graphics;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

public class GraphicsContext {

    private Graphics2D graphics2d;
    private ImageObserver observer;
    private int componentHeight;

    public Graphics2D getGraphics2d() {
        return graphics2d;
    }

    public void setGraphics2d(Graphics2D graphics2d) {
        this.graphics2d = graphics2d;
    }

    public ImageObserver getObserver() {
        return observer;
    }

    public void setObserver(ImageObserver observer) {
        this.observer = observer;
    }

    public int getComponentHeight() {
        return componentHeight;
    }

    public void setComponentHeight(int componentHeight) {
        this.componentHeight = componentHeight;
    }
}
