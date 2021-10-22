package com.andronikus.animation4j.rig;

import java.awt.Image;

/**
 * Interface used to provide images for an animation rig.
 *
 * @param <CONTEXT_OBJECT_TYPE> The type of object used to provide context
 * @param <ANIMATION_OF_TYPE> The type of object being animated
 * @author Andronikus
 */
public interface ILimbImageProvider<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> {

    /**
     * Can this image provider be used in the animation pipeline of the animated entity?
     *
     * @param animatedEntity The animated entity
     * @return If entity can be animated
     */
    boolean canAnimateEntity(ANIMATION_OF_TYPE animatedEntity);

    /**
     * Provide context to the image provider.
     *
     * @param contextProvider The context provider
     * @param animatedEntity The animated entity
     */
    void provideContext(CONTEXT_OBJECT_TYPE contextProvider, ANIMATION_OF_TYPE animatedEntity);

    /**
     * Get the next image.
     *
     * @return The image
     */
    Image getImage();
}
