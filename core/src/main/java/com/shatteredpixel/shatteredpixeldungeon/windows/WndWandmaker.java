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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Wandmaker;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.network.SendData;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import org.json.JSONObject;

import java.util.ArrayList;

public class WndWandmaker extends Window {

	Wandmaker wandmaker;
	Item questItem;

	public WndWandmaker(final Wandmaker wandmaker, final Item item, Hero hero) {
		
		super(hero);

		this.wandmaker = wandmaker;
		this.questItem = item;
		JSONObject object = new JSONObject();
		//object;
		object.put("wand1", Wandmaker.Quest.wand1.toJsonObject(hero));
		object.put("wand2", Wandmaker.Quest.wand2.toJsonObject(hero));
		object.put("quest_item", Item.packItem(questItem, hero));
		object.put("quest_item_class", questItem.getClass().getName());
		SendData.sendWindow(hero.networkID, "wandmaker", getId(), object);
	}
	private void selectReward(Item reward ) {

		if (reward == null){
			return;
		}

		hide();

		questItem.detach( getOwnerHero().belongings.backpack );
		Wandmaker.collectedHeroUUIDs.add(getOwnerHero().uuid);
		reward.identify(false, getOwnerHero());
		if(Dungeon.balance.multipleWandmakerReward){
			reward.bind(getOwnerHero());
		}
		if (reward.doPickUp( getOwnerHero())) {
			GLog.i( Messages.capitalize(Messages.get(getOwnerHero(), "you_now_have", reward.name())) );
		} else {
			Dungeon.level.drop( reward, wandmaker.pos ).sprite.drop();
		}
		if (!Dungeon.balance.multipleWandmakerReward) {
			wandmaker.yell( Messages.get(this, "farewell", Messages.titleCase(getOwnerHero().name())) );
			wandmaker.destroy();

			wandmaker.getSprite().die();
		}
		Wandmaker.Quest.complete();
	}

	@Override
	protected void onSelect(int button) {
		if(button == 0) {
			selectReward(Wandmaker.Quest.wand1);
		}
		if(button == 1) {
			selectReward(Wandmaker.Quest.wand2);
		}
		hide();

	}

}
