/* Pixel Dungeon Multiplayer
 * Copyright (C) 2021-2023 Shaposhnikov Nikita
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

package com.nikita22007.multiplayer.noosa.tweeners;

import com.shatteredpixel.shatteredpixeldungeon.network.SendData;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.Visual;
import com.watabou.noosa.tweeners.Tweener;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

public class AlphaTweener extends Tweener {

	@NotNull
	private final JSONObject actionObj;

	public AlphaTweener(CharSprite image, float target_alpha, float interval ) {
		super( image, interval );
		if (image.ch == null) {
			GLog.n("Can't add alpha tweener to unknown character");
			this.actionObj = null;
		} else {
			JSONObject actionObj = new JSONObject();
			try {
				actionObj.put("action_name", "sprite_action");
				actionObj.put("action", "alpha_tweener");
				actionObj.put("actor_id", image.ch.id());
				//actionObj.put("start_alpha", image.alpha());
				actionObj.put("target_alpha", target_alpha);
				actionObj.put("interval", interval);
			} catch (JSONException ignored) {
			}
			this.actionObj = actionObj;
		}
	}

	@Override
	public void onAdd() {
		if (actionObj != null) {
			SendData.sendCustomActionForAll(actionObj);
		}
	}

	@Override
	protected void updateValues(float progress) {

	}
}
