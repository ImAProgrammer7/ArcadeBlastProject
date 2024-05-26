package com.example.arcadeblastproject;

import android.graphics.Canvas;
import android.graphics.Rect;

public abstract class Tile {

    protected final Rect mapLocationRect;

    public Tile(Rect mapLocationRect) {
        this.mapLocationRect = mapLocationRect;
    }

    public enum TileType {
        GRASS_TILE,
        TREE_TILE,
        GROUND_TILE
    }

    public static Tile getTile(int idxTileType, SpriteSheet spriteSheet, Rect mapLocationRect){
        switch (TileType.values()[idxTileType]){
            case GROUND_TILE:
                return new GroundTile(spriteSheet, mapLocationRect);

            case GRASS_TILE:
                return new GrassTile(spriteSheet, mapLocationRect);

            case TREE_TILE:
                return new TreeTile(spriteSheet, mapLocationRect);

            default:
                return null;

        }
    }

    public abstract void draw(Canvas canvas);
}
