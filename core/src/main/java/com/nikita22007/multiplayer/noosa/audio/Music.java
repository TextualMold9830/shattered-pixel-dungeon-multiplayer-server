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

package com.nikita22007.multiplayer.noosa.audio;

import com.badlogic.gdx.Gdx;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.network.SendData;
import com.watabou.noosa.Game;
import com.watabou.utils.Callback;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.Random;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
@SuppressWarnings("NewApi")
//TODO: add possibility to play music for a specific hero
public enum Music {
	
	INSTANCE;

	public synchronized void play( String assetName, boolean looping ) {
		new PlayAction(assetName, looping).sendToAll();
	}

	public synchronized void playTracks( String[] tracks, float[] chances, boolean shuffle){
		new PlayTracksAction(tracks, chances, shuffle).sendToAll();
	}

	public synchronized void fadeOut(float duration, MusicAction onComplete){
		new FadeOutAction(duration, onComplete).sendToAll();
	}

	
	public synchronized void end() {
		new EndAction().sendToAll();
	}



	
	public static abstract class MusicAction {
		public JSONObject toJSON(){
			JSONObject object = new JSONObject();
			object.put("action_type", "music");
			object.put("music_action_type", musicActionType());
			pack(object);
			return object;
		}
		//Indicates which action related to music client should parese
		public abstract String musicActionType();
		public void sendToAll() {
			send(null);
		}
		public void send(Hero hero){
			if (hero != null) {
				SendData.sendCustomAction(toJSON(), hero);
			} else {
				SendData.sendCustomActionForAll(toJSON());
			}
		}
		public void pack(JSONObject object){};
	}
	public static class PlayAction extends MusicAction {
		String assetName;
		boolean looping = false;
		@Override
		public String musicActionType() {
			return "play";
		}

		@Override
		public void pack(JSONObject object) {
			object.put("asset", assetName);
			object.put("looping", looping);
		}

		public PlayAction(String assetName, boolean looping) {
			this.assetName = assetName;
			this.looping = looping;
		}

		public PlayAction(String assetName) {
			this.assetName = assetName;
		}
	}
	public static class PlayTracksAction extends MusicAction {
		String[] tracks;
		float[] chances;
		boolean shuffle;
		@Override
		public String musicActionType() {
			return "play_tracks";
		}
		@Override
		public void pack(JSONObject object) {
			JSONArray tracks = new JSONArray(this.tracks);
			JSONArray chances = new JSONArray(this.chances);
			object.put("tracks", tracks);
			object.put("chances", chances);
			object.put("shuffle", shuffle);
		}

		public PlayTracksAction(String[] tracks, float[] chances, boolean shuffle) {
			this.tracks = tracks;
			this.chances = chances;
			this.shuffle = shuffle;
		}
	}
	public static class EndAction extends MusicAction{
		@Override
		public String musicActionType() {
			return "end";
		}
	}
	public static class FadeOutAction extends MusicAction{

		MusicAction callback;
		float duration;
		@Override
		public String musicActionType() {
			return "fade_out";
		}

		@Override
		public void pack(JSONObject object) {
			object.put("duration", duration);
			if (callback != null){
				object.put("callback", callback.toJSON());
			};
		}

		public FadeOutAction(float duration) {
			this.duration = duration;
		}

		public FadeOutAction(float duration, MusicAction callBack) {
			this.callback = callBack;
			this.duration = duration;
		}
	}
}
