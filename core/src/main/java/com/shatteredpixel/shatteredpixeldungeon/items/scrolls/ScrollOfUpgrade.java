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

package com.shatteredpixel.shatteredpixeldungeon.items.scrolls;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Degrade;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.optional.FragmentOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndUpgrade;

public class ScrollOfUpgrade extends InventoryScroll {
	
	{
		icon = ItemSpriteSheet.Icons.SCROLL_UPGRADE;
		preferredBag = Belongings.Backpack.class;

		unique = true;

		talentFactor = 2f;
	}

	@Override
	protected boolean usableOnItem(Item item) {
		return item.isUpgradable();
	}

	@Override
	protected void onItemSelected( Item item, Hero hero ) {
		//TODO: this is temporary fix until WndUpgrade is finished
		upgradeItem(item);
		//GameScene.show(new WndUpgrade(this, item, identifiedByUse, hero));

	}

	public void reShowSelector(boolean force, Hero hero){
		identifiedByUse = force;
		curItem = this;
		GameScene.selectItem(itemSelector, hero);
	}

	public Item upgradeItem( Item item ){
		ScrollOfUpgrade.upgradeAnimation( curUser );

		Degrade.detach( curUser, Degrade.class );

		//logic for telling the user when item properties change from upgrades
		//...yes this is rather messy
		if (item instanceof Weapon){
			Weapon w = (Weapon) item;
			boolean wasCursed = w.cursed;
			boolean wasHardened = w.enchantHardened;
			boolean hadCursedEnchant = w.hasCurseEnchant();
			boolean hadGoodEnchant = w.hasGoodEnchant();

			item = w.upgrade();

			if (w.cursedKnown && hadCursedEnchant && !w.hasCurseEnchant()){
				removeCurse(curUser);
			} else if (w.cursedKnown && wasCursed && !w.cursed){
				weakenCurse(curUser);
			}
			if (wasHardened && !w.enchantHardened){
				GLog.w( Messages.get(Weapon.class, "hardening_gone") );
			} else if (hadGoodEnchant && !w.hasGoodEnchant()){
				GLog.w( Messages.get(Weapon.class, "incompatible") );
			}

		} else if (item instanceof Armor){
			Armor a = (Armor) item;
			boolean wasCursed = a.cursed;
			boolean wasHardened = a.glyphHardened;
			boolean hadCursedGlyph = a.hasCurseGlyph();
			boolean hadGoodGlyph = a.hasGoodGlyph();

			item = a.upgrade();

			if (a.cursedKnown && hadCursedGlyph && !a.hasCurseGlyph()){
				removeCurse( curUser );
			} else if (a.cursedKnown && wasCursed && !a.cursed){
				weakenCurse( curUser );
			}
			if (wasHardened && !a.glyphHardened){
				GLog.w( Messages.get(Armor.class, "hardening_gone") );
			} else if (hadGoodGlyph && !a.hasGoodGlyph()){
				GLog.w( Messages.get(Armor.class, "incompatible") );
			}

		} else if (item instanceof Wand || item instanceof Ring) {
			boolean wasCursed = item.cursed;

			item = item.upgrade();

			if (item.cursedKnown && wasCursed && !item.cursed){
				removeCurse( curUser );
			}

		} else {
			item = item.upgrade();
		}

		Badges.validateItemLevelAquired( item );
		Statistics.upgradesUsed++;
		Badges.validateMageUnlock();

		Catalog.countUse(item.getClass());

		return item;
	}
	
	public static void upgradeAnimation(Hero hero){
		hero.getSprite().emitter().start( Speck.factory( Speck.UP ), 0.2f, 3 );
	}

	public static void weakenCurse( Hero hero ){
		GLog.p( Messages.get(ScrollOfUpgrade.class, "weaken_curse") );
		hero.getSprite().emitter().start( ShadowParticle.UP, 0.05f, 5 );
	}

	public static void removeCurse( Hero hero ){
		GLog.p( Messages.get(ScrollOfUpgrade.class, "remove_curse") );
		hero.getSprite().emitter().start( ShadowParticle.UP, 0.05f, 10 );
		Badges.validateClericUnlock();
	}
	
	@Override
	public int value() {
		return isKnown() ? 50 * quantity() : super.value();
	}

	@Override
	public int energyVal() {
		return isKnown() ? 10 * quantity() : super.energyVal();
	}
	//We can not use upgrade(Hero) because it will conflict with upgrade(Hero) of item
	public static void upgradeVisual( Hero hero ) {
		hero.getSprite().emitter().start( Speck.factory( Speck.UP ), 0.2f, 3 );
	}

	@Override
	public void doRead(Hero hero) {
		if (Dungeon.useFragments){
			for (Hero h: Dungeon.heroes){
                if (h != null) {
                    new FragmentOfUpgrade(hero).collect(h);
                }
            }
		} else {
			super.doRead(hero);
		}
	}
}
