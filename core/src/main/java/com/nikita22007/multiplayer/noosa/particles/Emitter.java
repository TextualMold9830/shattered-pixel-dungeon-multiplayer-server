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

package com.nikita22007.multiplayer.noosa.particles;

import com.shatteredpixel.shatteredpixeldungeon.network.SendData;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.glwrap.Blending;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Visual;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Documented;

/**
 * Emitter emits visual particles from random place in its area.
 * <p>
 * Emitter's area is Rect(x, y, width, height).
 * <p>
 * Emitter emits {@link #quantity} count of particles with {@link #interval} delay.
 * If {@code quantity == 0} emitter should be stopped manually */
public class Emitter extends Group {

	public static boolean freezeEmitters = false;

	/**
	 * X position of left-top angle of emitter's area
	 */
	public float x;

	/**
	 * Y position of left-top angle of emitter's area
	 */
	public float y;
	/**
	 * Width of emitter's area
	 */
	public float width;
	/**
	 * Height of emitter's area
	 */
	public float height;
	/**
	 * If target != null, emitter's will use target's position instead of itself
	 */
	protected Visual target;
	/**
	 * If target != null and fillTarget, emitter's will use target's size instead of itself
	 */
	public boolean fillTarget = true;

	protected float interval;
	protected int quantity;

	public boolean on = false;

	private boolean started = false;
	public boolean autoKill = true;

	protected int count;
	protected float time;

	/**
	 * Factory which producing particles
	 */
	protected Factory factory;

	private Integer cell = null;

	private int id = -1;
	private static int lastId = 0;
	protected synchronized final int getNextId() {
		return ++lastId;
	}

	public void pos( float x, float y ) {
		pos( x, y, 0, 0 );
	}

	public void pos( PointF p ) {
		pos( p.x, p.y, 0, 0 );
	}

	public void cell(int pos) {
		cell = pos;
		this.x = 0;
		this.y = 0;
	}
	public void cell(int pos, float width, float height) {
		cell = pos;
		this.x = 0;
		this.y = 0;
		this.width = width;
		this.height = 0;
	}
	public void cell(int pos, int x, int y,  float width, float height) {
		cell = pos;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = 0;
	}
	public void pos( float x, float y, float width, float height ) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		target = null;
		cell = null;
	}

	public void pos( Visual target ) {
		this.target = target;
	}

	public void pos( Visual target, float x, float y, float width, float height ) {
		pos(x, y, width, height);
		pos(target);
	}

	/**
	 * Emits {@code quantity} particles in one time
	 * @param factory factory of particles
	 * @param quantity count of particles
	 */
	public void burst( Factory factory, int quantity ) {
		start( factory, 0, quantity );
	}

	/**
	 * Emits particles each {@code interval} seconds. Should be stopped manually
	 * @param factory factory of particles
	 * @param interval interval between emitting
	 */
	public void pour( Factory factory, float interval ) {
		start( factory, interval, 0 );
	}

	/**
	 * Emits {@code quantity} particles each {@code interval} seconds
	 * if quantity is 0, should be stopped manually
	 * @param factory factory of particles
	 * @param interval interval between emitting
	 * @param quantity count of particles
	 */
	public void start( Factory factory, float interval, int quantity ) {

		this.factory = factory;

		this.interval = interval;
		this.quantity = quantity;

		count = 0;
		time = Random.Float( interval );

		on = true;
		started = true;
		if (quantity == 0) {
			if (id == -1) {
				id = getNextId();
			}
		}
		sendSelf();
	}


	protected boolean isFrozen(){
		return Game.timeTotal > 1 && freezeEmitters;
	}

	@Override
	public void update() {

	}

	@Override
	public void revive() {
		//ensure certain emitter variables default to true
		started = false;
		visible = true;
		fillTarget = true;
		autoKill = true;
		super.revive();
	}

	public void onParentChanged() {
		super.onParentChanged();
		sendSelf();
	}

	protected void sendSelf() {
		if (parent == null) {
			return;
		}
		JSONObject actionObj = new JSONObject();
		try {
			actionObj.put("action_type", "emitter_visual");
			actionObj.put("id", id);
			if (target != null){
				if ((target instanceof CharSprite) && ((CharSprite) target).ch != null && ((CharSprite) target).ch.id() != -1){
						actionObj.put("target_char", ((CharSprite) target).ch.id());
						actionObj.put("shift_x", x);
						actionObj.put("shift_y", y);
				} else {
					actionObj.put("position_x", x + target.x);
					actionObj.put("position_y", x + target.y);
				}
				} else if (cell != null){
					actionObj.put("pos", cell);
					actionObj.put("shift_x", x);
					actionObj.put("shift_y", y);
				} else {
				actionObj.put("position_x", x);
				actionObj.put("position_y", y);
			}

			actionObj.put("fill_target", fillTarget);

			actionObj.put("width", width);
			actionObj.put("height", height);

			actionObj.put("interval", interval);
			actionObj.put("quantity", quantity);

			actionObj.put("factory", factory.toJsonObject());
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
		SendData.sendCustomActionForAll(actionObj);
	}
	abstract public static class Factory {

		abstract public void emit( Emitter emitter, int index, float x, float y );

		public boolean lightMode() {
			return false;
		}

		@NotNull
		public abstract String factoryName();

		@Nullable
		public JSONObject customParams() {
			return new JSONObject();
		}
		public final JSONObject toJsonObject() {
			JSONObject result = customParams();
			try {
				result = result == null? new JSONObject(): result;
				result.put("factory_type", factoryName());
				result.put("light_mode", lightMode());
			} catch (JSONException ignored) {}
			return result;
		}
	}
}
