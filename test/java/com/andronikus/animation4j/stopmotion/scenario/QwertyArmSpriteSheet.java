package com.andronikus.animation4j.stopmotion.scenario;

import com.andronikus.animation4j.spritesheet.SpriteSheet;

import java.awt.image.BufferedImage;

public class QwertyArmSpriteSheet extends SpriteSheet {

    public QwertyArmSpriteSheet() {
        super("armtop.png", 16, 32);
    }

    public BufferedImage getNeutralSprite(int animationState) {
        return getTile(0, animationState);
    }

    public BufferedImage getRedSprite(int animationState) {
        return getTile(1, animationState);
    }
}
