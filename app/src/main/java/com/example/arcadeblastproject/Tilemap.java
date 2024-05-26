package com.example.arcadeblastproject;

import static com.example.arcadeblastproject.MapLayout.NUMBER_OF_COLUMN_TILES;
import static com.example.arcadeblastproject.MapLayout.NUMBER_OF_ROW_TILES;
import static com.example.arcadeblastproject.MapLayout.TILE_HEIGHT_PIXEL;
import static com.example.arcadeblastproject.MapLayout.TILE_WIDTH_PIXEL;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Tilemap {

    private final MapLayout mapLayout;
    private Tile[][] tilemap;
    private SpriteSheet spriteSheet;
    private Bitmap mapBitmap;

    public Tilemap(SpriteSheet spriteSheet){
        mapLayout = new MapLayout();
        this.spriteSheet = spriteSheet;
        initializeTilemap();
    }

    private void initializeTilemap() {
        int[][] layout = mapLayout.getLayout();
        tilemap = new Tile[NUMBER_OF_ROW_TILES][NUMBER_OF_COLUMN_TILES];

        for (int iRow = 0; iRow < NUMBER_OF_ROW_TILES; iRow++){
            for (int iColumn = 0; iColumn < NUMBER_OF_COLUMN_TILES; iColumn++){
                tilemap[iRow][iColumn] = Tile.getTile(
                        layout[iRow][iColumn],
                        spriteSheet,
                        getRectByIndex(iRow, iColumn)
                );
            }
        }

        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        mapBitmap = Bitmap.createBitmap(
                NUMBER_OF_COLUMN_TILES * TILE_WIDTH_PIXEL,
                NUMBER_OF_ROW_TILES * TILE_HEIGHT_PIXEL,
                config
        );

        Canvas mapCanvas = new Canvas(mapBitmap);

        for (int iRow = 0; iRow < NUMBER_OF_ROW_TILES; iRow++){
            for (int iColumn = 0; iColumn < NUMBER_OF_COLUMN_TILES; iColumn++){
                tilemap[iRow][iColumn].draw(mapCanvas);
            }
        }

        Tile first = tilemap[0][0];
        Tile last = tilemap[tilemap.length - 1][tilemap[tilemap.length - 1].length - 1];

        SurvivalPlayer.setBounds(first.mapLocationRect.left, first.mapLocationRect.top,
                last.mapLocationRect.right, last.mapLocationRect.bottom);
    }

    private Rect getRectByIndex(int idxRow, int idxColumn) {
        return new Rect(
                idxColumn * TILE_WIDTH_PIXEL,
                idxRow * TILE_HEIGHT_PIXEL,
                (idxColumn + 1) * TILE_WIDTH_PIXEL,
                (idxRow + 1) * TILE_HEIGHT_PIXEL
        );
    }

    public void draw(Canvas canvas, GameDisplay gameDisplay) {
        canvas.drawBitmap(
                mapBitmap,
                gameDisplay.getGameRect(),
                gameDisplay.DISPLAY_RECT,
                null
        );
    }
}
