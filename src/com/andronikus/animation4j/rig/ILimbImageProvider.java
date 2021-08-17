package com.andronikus.animation4j.rig;

import java.awt.Image;

public interface ILimbImageProvider<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> {

    boolean canAnimateEntity(ANIMATION_OF_TYPE animatedEntity);

    void provideContext(CONTEXT_OBJECT_TYPE contextProvider, ANIMATION_OF_TYPE animatedEntity);

    Image getImage();
}
