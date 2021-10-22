package com.andronikus.animation4j.rig.imageprovider;

import com.andronikus.animation4j.rig.ILimbImageProvider;
import com.andronikus.animation4j.util.ImagesUtil;

import java.awt.Image;

/**
 * Animation limb image provider that uses a single static image.
 *
 * @param <CONTEXT_OBJECT_TYPE> Type of object providing greater context
 * @param <ANIMATION_OF_TYPE> Type of object being animated
 * @author Andronikus
 */
public class StaticLimbImageProvider<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> implements ILimbImageProvider<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> {

    private final Image image;

    public StaticLimbImageProvider(String imagePath) {
        this(ImagesUtil.getImage(imagePath));
    }

    public StaticLimbImageProvider(Image image) {
        this.image = image;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canAnimateEntity(ANIMATION_OF_TYPE animatedEntity) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void provideContext(CONTEXT_OBJECT_TYPE contextProvider, ANIMATION_OF_TYPE animatedEntity) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public Image getImage() {
        return image;
    }
}
