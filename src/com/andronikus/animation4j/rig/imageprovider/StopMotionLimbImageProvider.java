package com.andronikus.animation4j.rig.imageprovider;

import com.andronikus.animation4j.rig.ILimbImageProvider;
import com.andronikus.animation4j.stopmotion.StopMotionController;

import java.awt.Image;

public class StopMotionLimbImageProvider<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> implements ILimbImageProvider<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> {

    private final StopMotionController<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE, ?> stopMotionController;
    private CONTEXT_OBJECT_TYPE nextContentProvider = null;
    private ANIMATION_OF_TYPE nextAnimatedEntity = null;

    public StopMotionLimbImageProvider(StopMotionController<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE, ?> stopMotionController) {
        this.stopMotionController = stopMotionController;
    }

    @Override
    public boolean canAnimateEntity(ANIMATION_OF_TYPE animatedEntity) {
        return stopMotionController.checkIfObjectIsRoot(animatedEntity);
    }

    @Override
    public void provideContext(CONTEXT_OBJECT_TYPE contextProvider, ANIMATION_OF_TYPE animatedEntity) {
        nextContentProvider = contextProvider;
        nextAnimatedEntity = animatedEntity;
    }

    @Override
    public Image getImage() {
        return stopMotionController.nextSprite(nextContentProvider, nextAnimatedEntity);
    }
}
