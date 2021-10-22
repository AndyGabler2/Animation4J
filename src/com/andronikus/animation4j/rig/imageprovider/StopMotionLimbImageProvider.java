package com.andronikus.animation4j.rig.imageprovider;

import com.andronikus.animation4j.rig.ILimbImageProvider;
import com.andronikus.animation4j.stopmotion.StopMotionController;

import java.awt.Image;

/**
 * Animation limb image provider that uses a stop motion controller to supply images.
 *
 * @param <CONTEXT_OBJECT_TYPE> Type of object providing greater context
 * @param <ANIMATION_OF_TYPE> Type of object being animated
 * @author Andronikus
 */
public class StopMotionLimbImageProvider<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> implements ILimbImageProvider<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> {

    private final StopMotionController<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE, ?> stopMotionController;
    private CONTEXT_OBJECT_TYPE nextContextProvider = null;
    private ANIMATION_OF_TYPE nextAnimatedEntity = null;

    public StopMotionLimbImageProvider(StopMotionController<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE, ?> stopMotionController) {
        this.stopMotionController = stopMotionController;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canAnimateEntity(ANIMATION_OF_TYPE animatedEntity) {
        return stopMotionController.checkIfObjectIsRoot(animatedEntity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void provideContext(CONTEXT_OBJECT_TYPE contextProvider, ANIMATION_OF_TYPE animatedEntity) {
        nextContextProvider = contextProvider;
        nextAnimatedEntity = animatedEntity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Image getImage() {
        return stopMotionController.nextSprite(nextContextProvider, nextAnimatedEntity);
    }
}
