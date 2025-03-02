/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 * Copyright (C) 2021-2023 Nikita SHaposhnikov
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
package com.nikita22007.multiplayer.server.effects;

import com.shatteredpixel.shatteredpixeldungeon.network.SendData;
import com.watabou.noosa.Visual;
import com.watabou.utils.PointF;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

public final class Flare extends Visual {

	private float duration = 0;

	private boolean lightMode = true;

	private int color;
	private final int nRays;
	private final float radius;
	private final float angle;

	public float angularSpeed;
	private float lifespan;

	@Nullable
	private PointF position = null;
	private int pos = -1;

	public Flare( int nRays, float radius ) {
		super( 0, 0, 0, 0 );

        color = 0;

		this.nRays = nRays;
		this.radius = radius;

		angle = 45;
		angularSpeed = 180;

	}

//	public Flare hardlight(int color ) {
//		super();
//		hardlight(color);
//		this.color = color;
//		return this;
//
//	}

	public Flare color( int color, boolean lightMode ) {
		this.lightMode = lightMode;
		hardlight( color );

		return this;
	}

	public PointF point(PointF pos){
		this.position = pos;
		return position;
	}

	public void show(int pos, float duration ) {
		this.pos = pos;
		this.duration = duration;

		SendThis();
	}

	public void SendThis(){
		try{
			JSONObject actionObj = new JSONObject();
			actionObj.put("action_type", "flare_visual");
			if (position != null) {
				actionObj.put("position_x", position.x);
				actionObj.put("position_y", position.y);
			} else {
				actionObj.put("pos", pos);
			}
			actionObj.put("color", color);
			actionObj.put("duration", duration);
			actionObj.put("light_mode", lightMode);
			actionObj.put("rays", nRays);
			actionObj.put("radius", radius);
			actionObj.put("angle", angle);
			actionObj.put("angular_speed", angularSpeed);
			SendData.sendCustomActionForAll(actionObj);
		}catch (JSONException ignore){}
	}

	@Contract ("_ -> this")
	public Flare setAngularSpeed(float angularSpeed) {
		this.angularSpeed = angularSpeed;
		return this;
	}
	public Flare show(Visual visual, float duration ) {
		point( visual.center() );
		visual.parent.addToBack( this );

		lifespan = this.duration = duration;
		SendThis();
		return this;
	}
}
