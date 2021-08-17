package com.andronikus.animation4j.rig.imageprovider;

import com.andronikus.animation4j.rig.ILimbImageProvider;
import com.andronikus.animation4j.util.ImagesUtil;

import java.awt.Image;

public class StaticLimbImageProvider<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> implements ILimbImageProvider<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> {

    private final Image image;

    public StaticLimbImageProvider(String imagePath) {
        this(ImagesUtil.getImage(imagePath));
    }

    public StaticLimbImageProvider(Image image) {
        this.image = image;
    }

    @Override
    public boolean canAnimateEntity(ANIMATION_OF_TYPE animatedEntity) {
        return true;
    }

    @Override
    public void provideContext(CONTEXT_OBJECT_TYPE contextProvider, ANIMATION_OF_TYPE animatedEntity) {}

    @Override
    public Image getImage() {
        return image;
    }
}
