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

package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.badlogic.gdx.Gdx;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.utils.Random;
import org.json.JSONObject;

public class EmoIcon extends Image {

	protected float maxSize = 2;
	protected float timeScale = 1;
	
	protected boolean growing	= true;
	
	protected CharSprite owner;
	
	public EmoIcon( CharSprite owner ) {
		super();
		
		this.owner = owner;
		GameScene.add( this );
	}
	public JSONObject toJsonObject() {
		JSONObject json = new JSONObject();
		try {
			json.put("max_size", maxSize);
			json.put("time_scale", timeScale);
			json.put("max_size", maxSize);
		} catch (Exception ignored) {
		}
		// todo: part of future, for ShatteredPD
		// Now it's time
		Gdx.app.error("EmoIcon", "Emoicon is not yet implemented");
		return json;
	}
	@Override
	public void update() {
		super.update();
		
		if (visible) {
			if (growing) {
				scale.set( Math.min(scale.x + Game.elapsed * timeScale, maxSize ));
				if (scale.x >= maxSize) {
					growing = false;
				}
			} else {
				scale.set( Math.max(scale.x - Game.elapsed * timeScale, 1f ));
				if (scale.x <= 1) {
					growing = true;
				}
			}
			
			x = owner.x + owner.width() - width / 2;
			y = owner.y - height;
		}
	}
	
	public static class Sleep extends EmoIcon {
		
		public Sleep( CharSprite owner ) {
			
			super( owner );
			
			copy( Icons.get( Icons.SLEEP ) );
			
			maxSize = 1.2f;
			timeScale = 0.5f;
			
			origin.set( width / 2, height / 2 );
			scale.set( Random.Float( 1, maxSize ) );

			x = owner.x + owner.width - width / 2;
			y = owner.y - height;
		}

		@Override
		public JSONObject toJsonObject() {JSONObject jsonObject = new JSONObject();
			jsonObject.put("type", "default");
			jsonObject.put("emotion", "sleep");
			return jsonObject;
		}
	}
	
	public static class Alert extends EmoIcon {
		
		public Alert( CharSprite owner ) {
			
			super( owner );
			
			copy( Icons.get( Icons.ALERT ) );
			
			maxSize = 1.3f;
			timeScale = 2;
			
			origin.set( 2.5f, height - 2.5f );
			scale.set( Random.Float( 1, maxSize ) );

			x = owner.x + owner.width - width / 2;
			y = owner.y - height;
		}

		@Override
		public JSONObject toJsonObject() {JSONObject jsonObject = new JSONObject();
			jsonObject.put("type", "default");
			jsonObject.put("emotion", "alert");
			return jsonObject;
		}
	}
	
	public static class Lost extends EmoIcon {
		
		public Lost( CharSprite owner ){
			super( owner );
			
			copy( Icons.get( Icons.LOST ) );
			
			maxSize = 1.25f;
			timeScale = 1;
			
			origin.set( 2.5f, height - 2.5f );
			scale.set( Random.Float( 1, maxSize ) );
			
			x = owner.x + owner.width - width / 2;
			y = owner.y - height;
		}

		@Override
		public JSONObject toJsonObject() {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("type", "default");
			jsonObject.put("emotion", "lost");
			return jsonObject;
		}

	}

}
