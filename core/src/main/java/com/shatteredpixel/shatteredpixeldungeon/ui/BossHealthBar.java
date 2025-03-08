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

package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.network.SendData;
import org.json.JSONObject;

public class BossHealthBar {
	private static Mob boss;
	private static boolean bleeding;


	public static void assignBoss(Mob boss){
		if (BossHealthBar.boss == boss) {
			return;
		}
		BossHealthBar.boss = boss;
		bleed(false);
	}

	private static void updateBoss() {
		if (boss == null || !boss.isAlive() || !Dungeon.level.mobs.contains(boss)) {
			boss = null;
		}
	}

	public static boolean isAssigned(){
		updateBoss();
		return boss != null && boss.isAlive() && Dungeon.level.mobs.contains(boss);
	}

	public static void bleed(boolean value){
		bleeding = value;
		sendSelf();
	}

	public static boolean isBleeding(){
		return isAssigned() && bleeding;
	}
	public static void sendSelf(){
		if (isAssigned()) {
			JSONObject object = new JSONObject();
			object.put("bleeding", bleeding);
			object.put("id", boss.id());
			object.put("action_type", "boss_health_bar");
			SendData.sendCustomActionForAll(object);
		}
	}

}
