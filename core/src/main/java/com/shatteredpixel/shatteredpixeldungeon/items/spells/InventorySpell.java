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

package com.shatteredpixel.shatteredpixeldungeon.items.spells;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.nikita22007.multiplayer.noosa.audio.Sample;
import com.watabou.utils.Random;

public abstract class InventorySpell extends Spell {
	
	@Override
	protected void onCast(Hero hero) {
		GameScene.selectItem( itemSelector, hero );
	}

	private String inventoryTitle(){
		return Messages.get(this, "inv_title");
	}

	protected Class<?extends Bag> preferredBag = null;

	protected boolean usableOnItem( Item item ){
		return true;
	}
	
	protected abstract void onItemSelected( Item item, Hero hero );
	
	protected WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {

		@Override
		public String textPrompt() {
			return inventoryTitle();
		}

		@Override
		public Class<? extends Bag> preferredBag() {
			return preferredBag;
		}

		@Override
		public boolean itemSelectable(Item item) {
			return usableOnItem(item);
		}

		@Override
		public void onSelect( Item item ) {
			
			//FIXME this safety check shouldn't be necessary
			//it would be better to eliminate the curItem static variable.
			if (!(curItem instanceof InventorySpell)){
				return;
			}
			
			if (item != null) {

				//Infusion opens a separate window that can be cancelled
				//so we don't do a lot of logic here
				if (!(curItem instanceof MagicalInfusion)) {
					curItem = detach(curUser.belongings.backpack);
				}
				
				((InventorySpell)curItem).onItemSelected( item, getOwner() );
				if (!(curItem instanceof MagicalInfusion)) {
					curUser.spend(1f);
					curUser.busy();
					(curUser.getSprite()).operate(curUser.pos);

					Sample.INSTANCE.play(Assets.Sounds.READ);
					Invisibility.dispel(getOwner());

					Catalog.countUse(curItem.getClass());
					if (Random.Float() < ((Spell) curItem).talentChance) {
						Talent.onScrollUsed(curUser, curUser.pos, ((Spell) curItem).talentFactor);
					}
				}
				
			}
		}
	};
}
