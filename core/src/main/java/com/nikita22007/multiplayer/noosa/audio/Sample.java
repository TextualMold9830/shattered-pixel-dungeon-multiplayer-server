/*
 * Pixel Dungeon Multiplayer
 * Copyright (C) 2021-2023  Nikita Shaposhnikov
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

package com.nikita22007.multiplayer.noosa.audio;


import com.nikita22007.multiplayer.utils.Log;
import com.nikita22007.multiplayer.utils.Utils;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.network.SendData;
import com.watabou.utils.DeviceCompat;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Provides player for sounds. Singleton
 */
public enum Sample {

	INSTANCE;
	/**
	 * stores sounds loaded on clients
	 */
	protected final HashSet<String> ids =
			new HashSet<>();

	/**
	 * Loads sounds on clients. Adds sounds to list
	 * @param assets sound asset
	 */
	public void load( String... assets ) {
		JSONObject actionObj = new JSONObject();
		try {
			actionObj.put("action_type", "load_sample");
			actionObj.put("samples",	Utils.putToJSONArray(assets));
		} catch (JSONException ignored) {}
		ids.addAll(Arrays.asList(assets));
		SendData.sendCustomActionForAll(actionObj);
	}

	/**
	 * Unloads unsound on clients. Removes sound to list
	 * @param src sound asset
	 */
	public void unload( String src ) {
		JSONObject actionObj = new JSONObject();
		try {
			actionObj.put("action_type", "unload_sample");
			actionObj.put("sample", src);
		} catch (JSONException ignored) {}
		SendData.sendCustomActionForAll(actionObj);
		ids.remove( src );
	}

	/**
	 * Sends list of loaded sounds to client
	 */
	public void reload(){
		JSONObject actionObj = new JSONObject();
		try {
			actionObj.put("action_type", "reload_sample");
			actionObj.put("samples",	Utils.putToJSONArray(ids.toArray()));
		} catch (JSONException ignored) {}
		SendData.sendCustomActionForAll(actionObj);
	}

	public void play( String id ) {
		play( id, null );
	}
	public void play( String id, Hero hero ) {
		play( id, 1, hero );
	}
	public void play( String id, float volume ) {
		play( id, volume, null );
	}
	public void play( String id, float volume, Hero hero ) {
		play( id, volume, volume, 1, 1, hero );
	}
	public void play( String id, float volume, float pitch, Hero hero ) {
		play( id, volume, volume, 1, pitch, hero );
	}
	public void play( String id, float leftVolume, float rightVolume, float rate ) {
		play( id, leftVolume, rightVolume, rate, 1, null );
	}
	public void play( String id, float leftVolume, float rightVolume, float rate, float pitch, @Nullable Hero hero) {
		if (!ids.contains( id )) {
			assert !DeviceCompat.isDebug(): "playing unloaded sample: " + id;
			Log.e("Sound", "playing unloaded sample: " + id);
			load(id);
		}

		JSONObject actionObj = new JSONObject();
		try {
			actionObj.put("action_type", "play_sample");
			actionObj.put("sample", id);
			actionObj.put("left_volume", leftVolume);
			actionObj.put("right_volume", rightVolume);
			actionObj.put("rate", rate);
			actionObj.put("pitch", pitch);
		} catch (JSONException ignored) {}
		if (hero != null){
			SendData.sendCustomAction(actionObj, hero);
		} else {
			SendData.sendCustomActionForAll(actionObj);
		}
	}
	public void play(String id, float volume, float pitch){
		play(id, volume, volume, 1, pitch, null);
	}
	public void playDelayed( Object id, float delay ){
		playDelayed( id, delay, 1 );
	}

	public void playDelayed( Object id, float delay, float volume ) {
		playDelayed( id, delay, volume, volume, 1 );
	}
	public void playDelayed( Object id, float delay, float volume, float pitch ) {
		playDelayed( id, delay, volume, volume, pitch );
	}

	public void playDelayed( Object id, float delay, float leftVolume, float rightVolume, float pitch ) {}

}
