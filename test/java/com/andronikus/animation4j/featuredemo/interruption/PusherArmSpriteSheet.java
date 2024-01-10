package com.andronikus.animation4j.featuredemo.interruption;

import com.andronikus.animation4j.spritesheet.SpriteSheet;
import java.awt.image.BufferedImage;

public class PusherArmSpriteSheet extends SpriteSheet {

    public PusherArmSpriteSheet() {
        super("retractable-pusher/arm-spritesheet.png", 319, 320);
    }

    public BufferedImage getNeutralSprite() {
        return getTile(0, 0);
    }

    public BufferedImage getBreakingSprite(int state) {
        return getTile(1, state);
    }

    public BufferedImage getBrokenSprite() {
        return getTile(0, 1);
    }
}
