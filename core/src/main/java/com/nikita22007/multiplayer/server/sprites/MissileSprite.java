/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 * Copyright (C) 2021-2023 Nikita Shaposhnikov
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
package com.nikita22007.multiplayer.server.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.network.SendData;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;
import org.json.JSONException;
import org.json.JSONObject;

import static com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet.*;


public class MissileSprite {

	private static final float SPEED = 240f;

	public MissileSprite() {
	}

	public static void reset(int from, int to, Item item, Callback listener) {
		if (item == null) {
			reset(from, to, Assets.Sprites.ITEMS, ItemSpriteSheet.ARTIFACT_CAPE, null, listener);
			GLog.n("Missile sprite of NULL item");
		} else {
			reset(from, to, item.spriteSheet(), item.image(), item.glowing(), listener);
		}
	}

	public static void reset(int from, int to, String spriteSheet, int image, ItemSprite.Glowing glowing, Callback listener) {
		float angularSpeed, angle; //degrees

		PointF start = DungeonTilemap.tileToWorld(from);
		PointF dest = DungeonTilemap.tileToWorld(to);

		PointF d = PointF.diff(dest, start);

		if (image == DART || image == INCENDIARY_DART || image == JAVELIN) {
			//no rotation while fly, use angle correction for sprite
			angularSpeed = 0;
			angle = 135 - (float) (Math.atan2(d.x, d.y) / 3.1415926 * 180);

		} else {
			//rotation in flight, SHURIKEN and BOOMERANG rotate twice faster
			angularSpeed = image == SHURIKEN || image == BOOMERANG ? 1440 : 720;
			angle = 0; //is not important
		}

		JSONObject action = new JSONObject();
		try {
			action.put("action_type", "missile_sprite_visual");
			action.put("from", from);
			action.put("to", to);
			action.put("speed", SPEED);
			action.put("angular_speed", angularSpeed);
			action.put("angle", angle);

			action.put("item_sprite_sheet", spriteSheet);
			action.put("item_image", image);
			if (glowing != null){
				action.put("item_glowing", glowing.toJsonObject());
			} else {
				action.put("item_glowing", JSONObject.NULL);
			}
		} catch (JSONException ignore) {
		}
		SendData.sendCustomActionForAll(action);
		if (listener != null) {
			listener.call();
		}
	}
}
