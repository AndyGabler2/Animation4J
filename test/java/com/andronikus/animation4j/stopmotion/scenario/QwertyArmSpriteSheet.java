package com.andronikus.animation4j.stopmotion.scenario;

import com.andronikus.animation4j.spritesheet.SpriteSheet;

import java.awt.image.BufferedImage;

public class QwertyArmSpriteSheet extends SpriteSheet {

    public QwertyArmSpriteSheet() {
        super("qwerty/armtop.png", 32, 64);
    }

    public BufferedImage getNeutralSprite(int animationState) {
        return getTile(0, animationState);
    }

    public BufferedImage getRedSprite(int animationState) {
        return getTile(1, animationState);
    }
}
