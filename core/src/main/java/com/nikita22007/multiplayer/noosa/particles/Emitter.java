/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015  Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2016 Evan Debenham
 *
 * Pixel Dungeon Multiplayer
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

package com.nikita22007.multiplayer.noosa.particles;

import com.shatteredpixel.shatteredpixeldungeon.network.SendData;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.noosa.Gizmo;
import com.watabou.noosa.Group;
import com.watabou.utils.PointF;
import com.watabou.utils.SparseArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Emitter emits visual particles from random place in its area.
 * <p>
 * Emitter's area is Rect(x, y, width, height).
 * <p>
 * Emitter emits {@link #quantity} count of particles with {@link #interval} delay.
 * If {@code quantity == 0} emitter should be stopped manually */
public class Emitter /*this is temporary ->*/extends Group {
	int id = -1;

	protected boolean lightMode = false;
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
	protected CharSprite target;
	/**
	 * If target != null, emitter's will use target's size instead of itself
	 */
	public boolean fillTarget = true;
	
	protected float interval;
	protected int quantity;

	private boolean on = false;

	/**
	 * Factory which producing particles
	 */
	protected Factory factory;
	private Integer cell = null;
	private PointF shift = new PointF(0, 0);
	public boolean visible = true;
	public static boolean freezeEmitters = false;

	public void cellPos(int cell) {
		cellPos(cell, 0, 0);
	}

	public void cellPos(int cell, float width, float height){
		this.cell = cell;
		this.width = width;
		this.height = height;
	}

	public void cellPosWithShift(int cell, float shiftX, float shiftY) {
		cellPosWithShift(cell, new PointF(shiftX, shiftY));
	}

	public void cellPosWithShift(int cell, PointF shift) {
		cellPosWithShift(cell, shift, 0,0);
	}

	public void cellPosWithShift(int cell, float shiftX,float shiftY, float width, float height) {
		cellPosWithShift(cell, new PointF(shiftX, shiftY), width, height);
	}

	public void cellPosWithShift(int cell, PointF shift, float width, float height) {
		this.cell = cell;
		this.shift = shift;
		this.width = width;
		this.height = height;
		target = null;
	}

	public void pos( PointF p ) {
		pos( p.x, p.y, 0, 0 );
	}
	
	public void pos( float x, float y, float width, float height ) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		target = null;
	}
	public void pos(CharSprite target, float x, float y, float width, float height){
		pos(x, y, width, height);
		this.target = target;
	}
	public void pos( CharSprite target ) {
		this.target = target;
	}

	public void pos( CharSprite target, PointF shift ) {
		this.target = target;
		this.shift = shift;
	}

	/**
	 * Emits {@code quantity} particles in one time
	 * @param factory factory of particles
	 * @param quantity count of particles
	 */
	public void burst( Factory factory, int quantity ) {
		start( factory, 0, quantity );
	}
	//This is temporary
	public void burst(Object factory, int quantity){}

	/**
	 * Emits particles each {@code interval} seconds. Should be stopped manually
	 * @param factory factory of particles
	 * @param interval interval between emitting
	 */
	public void pour( Factory factory, float interval ) {
		start( factory, interval, 0 );
	}
	//This is temporary
	public void pour(Object factory, float interval){

	}

	/**
	 * Emits {@code quantity} particles each {@code interval} seconds
	 * @param factory factory of particles
	 * @param interval interval between emitting
	 */
	static SparseArray<Emitter> infiniteEmitters = new SparseArray<>();
	static int idCounter = 0;
	static void putEmitter(Emitter emitter){
		emitter.id = idCounter;
		infiniteEmitters.put(emitter.id, emitter);
		idCounter++;
	}
	public void start( Factory factory, float interval, int quantity ) {

		if (quantity == 0) {
			putEmitter(this);
		}

		this.factory = factory;
		this.lightMode = factory.lightMode();
		
		this.interval = interval;
		this.quantity = quantity;
		
		on(true);
		sendSelf();
	}
	//TODO: remove all uses of this
	public void update(){}

	//TODO: remove all uses of this
	public void revive(){}
	//TODO: remove all uses of this
	public boolean autoKill = false;
	//TODO: remove all uses of this
	public void killAndErase(){
		if (id != -1){
			JSONObject object = new JSONObject();
			object.put("action_type", "emitter_visual");
			object.put("id", id);
			object.put("kill", true);
			SendData.sendCustomActionForAll(object);
		}
	}
	@Override
	public void kill(){
		killAndErase();
	}

	//TODO: check this
	protected boolean isFrozen(){return false;}

	protected void sendSelf() {
		JSONObject actionObj = new JSONObject();
		try {
			actionObj.put("action_type", "emitter_visual");

			if ((target != null) && (target.ch != null) && (target.ch.id() != -1)) {
				actionObj.put("target_char", target.ch.id());
				actionObj.put("fill_target", fillTarget);
			} else if (cell != null){
				actionObj.put("pos", cell);
			} else {
				actionObj.put("position_x", x);
				actionObj.put("position_y", y);
			}

			actionObj.put("shift_x", shift.x);
			actionObj.put("shift_y", shift.y);

			actionObj.put("width", width);
			actionObj.put("height", height);

			actionObj.put("interval", interval);
			actionObj.put("quantity", quantity);
			if (id > -1){
				actionObj.put("id", id);
			}
			actionObj.put("factory", factory.toJsonObject());

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		SendData.sendCustomActionForAll(actionObj);
	}

	public void pos(float x, float y) {
		pos(x, y, 0,0);
	}

	/**
	 * if {@code on == false}, Emitter stops emitting
	 */
	public boolean on() {
		return on;
	}

	public void 	on(boolean on) {
		this.on = on;
		if (!on){
			killAndErase();
		}
	}

	abstract public static class Factory {

		public boolean lightMode() {
			return false;
		}
		//TODO: check this
		public String factoryName(){
			//return toPath(this);
			return getClass().getName();
		};

		public JSONObject customParams() {
			return new JSONObject();
		}

		public final JSONObject toJsonObject() {
			JSONObject result =  customParams();
			try {
			result = result == null? new JSONObject(): result;
			result.put("path", getClass().getName());
			result.put("factory_type", factoryName());
			result.put("light_mode", lightMode());
			} catch (JSONException ignored) {}
			return result;
		}
		//TODO: remove all overrides of this
		public void emit(Emitter emitter, int index, float x, float y ){};
	}
	protected void emit(int index){}
}
