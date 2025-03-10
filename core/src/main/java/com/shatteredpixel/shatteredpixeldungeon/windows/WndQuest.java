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

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.network.NetworkPacket;
import com.shatteredpixel.shatteredpixeldungeon.network.SendData;
import org.json.JSONObject;

public class WndQuest extends WndTitledMessage {

	public WndQuest(NPC questgiver, String text, Hero hero) {
		super( questgiver.sprite(), Messages.titleCase( questgiver.name() ), text, hero );
		JSONObject object = new JSONObject();

		object.put("sprite_name", questgiver.getSprite().getClass().getName());
		object.put("char_name", questgiver.name());
		object.put("text", text);
		SendData.sendWindow(hero.networkID, "quest", getId(), object);
	}
}
