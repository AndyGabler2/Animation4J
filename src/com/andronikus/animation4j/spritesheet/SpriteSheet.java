package com.andronikus.animation4j.spritesheet;

import com.andronikus.animation4j.util.ImagesUtil;

import java.awt.image.BufferedImage;

/**
 * Sprite sheet that uses a big image and divides them into a set of smaller images called sprites.
 *
 * @author Andronikus
 */
public class SpriteSheet {

    private final BufferedImage spriteSheet;
    private final int tileWidth;
    private final int tileHeight;

    /**
     * Instantiate a sprite sheet.
     *
     * @param spriteSheetPath The path to the sprite sheet
     * @param tileSize The size of the tile
     */
    public SpriteSheet(String spriteSheetPath, int tileSize) {
        this(ImagesUtil.getImage(spriteSheetPath), tileSize);
    }

    /**
     * Instantiate a sprite sheet.
     *
     * @param spriteSheet The sprite sheet image
     * @param tileSize The size of the tile
     */
    public SpriteSheet(BufferedImage spriteSheet, int tileSize) {
        this(spriteSheet, tileSize, tileSize);
    }

    /**
     * Instantiate a sprite sheet.
     *
     * @param spriteSheetPath The path to the sprite sheet
     * @param tileWidth The size of the tile
     * @param tileHeight The size of the tile
     */
    public SpriteSheet(String spriteSheetPath, int tileWidth, int tileHeight) {
        this(ImagesUtil.getImage(spriteSheetPath), tileWidth, tileHeight);
    }

    /**
     * Instantiate a sprite sheet.
     *
     * @param spriteSheet The sprite sheet image
     * @param tileWidth The size of the tile
     * @param tileHeight The size of the tile
     */
    public SpriteSheet(BufferedImage spriteSheet, int tileWidth, int tileHeight) {
        this.spriteSheet = spriteSheet;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    /**
     * Get the tile.
     *
     * @param x The X location
     * @param y The Y location
     * @return The sprite
     */
    protected BufferedImage getTile(int x, int y) {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Tile X and Y must be zero or higher.");
        }
        return spriteSheet.getSubimage(x * tileWidth, y * tileHeight, tileWidth, tileHeight);
    }
}
