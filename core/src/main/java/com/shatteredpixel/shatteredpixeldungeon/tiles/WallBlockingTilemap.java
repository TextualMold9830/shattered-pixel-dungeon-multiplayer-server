/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.HallsBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.MiningLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.Tilemap;


public class WallBlockingTilemap extends Tilemap {

	public static final int SIZE = 16;

	private static final int CLEARED        = -2;
	private static final int BLOCK_NONE     = -1;
	private static final int BLOCK_RIGHT    = 0;
	private static final int BLOCK_LEFT     = 1;
	private static final int BLOCK_ALL      = 2;
	private static final int BLOCK_BELOW    = 3;

	public WallBlockingTilemap() {
		super(Assets.Environment.WALL_BLOCKING, new TextureFilm( Assets.Environment.WALL_BLOCKING, SIZE, SIZE ) );
		map( new int[Dungeon.level.length()], Dungeon.level.width());
	}

	@Override
	public synchronized void updateMap() {
		for (int cell = 0; cell < data.length; cell++) {
			//force all top/bottom row, and none-discoverable cells to cleared
			if (!Dungeon.level.discoverable[cell]
					|| (cell - mapWidth) <= 0
					|| (cell + mapWidth) >= size){
				data[cell] = CLEARED;
			} else {
				updateMapCell(cell);
			}
		}

		super.updateMap();
	}

	private int curr;
	
	@Override
	public synchronized void updateMapCell(int cell) {
		return;
	}

	private boolean fogHidden(int cell){
		if (!Dungeon.level.visited[cell] && !Dungeon.level.mapped[cell]) {
			return true;
		} else if (wall(cell) && cell + mapWidth < size && !wall(cell + mapWidth) &&
				!Dungeon.level.visited[cell + mapWidth] && !Dungeon.level.mapped[cell + mapWidth]) {
			return true;
		}
		return false;
	}

	private boolean wall(int cell) {
		return false;
	}

	private boolean shelf(int cell) {
		return Dungeon.level.map[cell] == Terrain.BOOKSHELF;
	}

	private boolean door(int cell) {
		return DungeonTileSheet.doorTile(Dungeon.level.map[cell]);
	}
	
	public synchronized void updateArea(int cell, int radius){
		int l = cell%mapWidth - radius;
		int t = cell/mapWidth - radius;
		int r = cell%mapWidth - radius + 1 + 2*radius;
		int b = cell/mapWidth - radius + 1 + 2*radius;
		updateArea(
				Math.max(0, l),
				Math.max(0, t),
				Math.min(mapWidth-1, r - l),
				Math.min(mapHeight-1, b - t)
		);
	}

	public synchronized void updateArea(int x, int y, int w, int h) {
		int cell;
		for (int i = x; i <= x+w; i++){
			for (int j = y; j <= y+h; j++){
				cell = i + j*mapWidth;
				if (cell < data.length && data[cell] != CLEARED)
					updateMapCell(cell);
			}
		}
	}
	
}
