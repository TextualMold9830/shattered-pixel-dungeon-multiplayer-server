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

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.utils.Random;


public abstract class KindofMisc extends EquipableItem {

	@Override
	public boolean doEquip(final Hero hero) {

		boolean equipFull = false;
		if ( this instanceof Artifact
				&& hero.belongings.getRealArtifact() != null
				&& hero.belongings.getRealMisc() != null){

			//see if we can re-arrange items first
			if (hero.belongings.getRealMisc() instanceof Ring && hero.belongings.getRealRing() == null){
				hero.belongings.setRing((Ring) hero.belongings.getRealMisc());
				hero.belongings.setMisc(null);
			} else {
				equipFull = true;
			}
		} else if (this instanceof Ring
				&& hero.belongings.getRealMisc() != null
				&& hero.belongings.getRealRing() != null){

			//see if we can re-arrange items first
			if (hero.belongings.getRealMisc() instanceof Artifact && hero.belongings.getRealArtifact() == null){
				hero.belongings.setArtifact((Artifact) hero.belongings.getRealMisc());
				hero.belongings.setMisc(null);
			} else {
				equipFull = true;
			}
		}

		if (equipFull) {

			final KindofMisc[] miscs = new KindofMisc[3];
			miscs[0] = hero.belongings.getRealArtifact();
			miscs[1] = hero.belongings.getRealMisc();
			miscs[2] = hero.belongings.getRealRing();

			final boolean[] enabled = new boolean[3];
			enabled[0] = miscs[0] != null;
			enabled[1] = miscs[1] != null;
			enabled[2] = miscs[2] != null;

			//force swapping with the same type of item if 2x of that type is already present
			if (this instanceof Ring && hero.belongings.getRealMisc() instanceof Ring){
				enabled[0] = false; //disable artifact
			} else if (this instanceof Artifact && hero.belongings.getRealMisc() instanceof Artifact){
				enabled[2] = false; //disable ring
			}

			GameScene.show(
					new WndOptions(hero, new ItemSprite(this),
							Messages.get(KindofMisc.class, "unequip_title"),
							Messages.get(KindofMisc.class, "unequip_message"),
							miscs[0] == null ? "---" : Messages.titleCase(miscs[0].title()),
							miscs[1] == null ? "---" : Messages.titleCase(miscs[1].title()),
							miscs[2] == null ? "---" : Messages.titleCase(miscs[2].title())) {

						@Override
						protected void onSelect(int index) {

							KindofMisc equipped = miscs[index];
							//we directly remove the item because we want to have inventory capacity
							// to unequip the equipped one, but don't want to trigger any other
							// item detaching logic
							int slot = Dungeon.quickslot.getSlot(KindofMisc.this);
							slotOfUnequipped = -1;
							getOwnerHero().belongings.backpack.items.remove(KindofMisc.this);
							if (equipped.doUnequip(hero, true, false)) {
								//swap out equip in misc slot if needed
								if (index == 0 && KindofMisc.this instanceof Ring){
									hero.belongings.setArtifact((Artifact) hero.belongings.getRealMisc());
									hero.belongings.setMisc(null);
								} else if (index == 2 && KindofMisc.this instanceof Artifact){
									hero.belongings.setRing((Ring) hero.belongings.getRealMisc());
									hero.belongings.setMisc(null);
								}
								getOwnerHero().belongings.backpack.items.add(KindofMisc.this);
								doEquip(hero);
							} else {
								getOwnerHero().belongings.backpack.items.add(KindofMisc.this);
							}
							if (slot != -1) {
								Dungeon.quickslot.setSlot(slot, KindofMisc.this);
							} else if (slotOfUnequipped != -1 && defaultAction() != null){
								Dungeon.quickslot.setSlot(slotOfUnequipped, KindofMisc.this);
							}
							updateQuickslot();
						}

						@Override
						protected boolean enabled(int index) {
							return enabled[index];
						}
					});

			return false;

		} else {

			// 15/25% chance
			if (hero.heroClass != HeroClass.CLERIC && hero.hasTalent(Talent.HOLY_INTUITION)
					&& cursed && !cursedKnown
					&& Random.Int(20) < 1 + 2*hero.pointsInTalent(Talent.HOLY_INTUITION)){
				cursedKnown = true;
				GLog.p(Messages.get(this, "curse_detected"));
				return false;
			}

			if (this instanceof Artifact){
				if (hero.belongings.getRealArtifact() == null)   hero.belongings.setArtifact((Artifact) this);
				else                                    hero.belongings.setMisc((Artifact) this);
			} else if (this instanceof Ring){
				if (hero.belongings.getRealRing() == null)   hero.belongings.setRing((Ring) this);
				else                                hero.belongings.setMisc((Ring) this);
			}

			detach( hero.belongings.backpack );

			Talent.onItemEquipped(hero, this);
			activate( hero );

			cursedKnown = true;
			if (cursed) {
				equipCursed( hero );
				GLog.n( Messages.get(this, "equip_cursed", this) );
			}

			hero.spendAndNext( timeToEquip(hero) );
			return true;

		}

	}

	@Override
	public boolean doUnequip(Hero hero, boolean collect, boolean single) {
		if (super.doUnequip(hero, collect, single)){

			if (hero.belongings.getRealArtifact() == this) {
				hero.belongings.setArtifact(null);
			} else if (hero.belongings.getRealMisc() == this) {
				hero.belongings.setMisc(null);
			} else if (hero.belongings.getRealRing() == this){
				hero.belongings.setRing(null);
			}

			return true;

		} else {

			return false;

		}
	}

	@Override
	public boolean isEquipped( Hero hero ) {
		return hero != null && (hero.belongings.artifact() == this
				|| hero.belongings.misc() == this
				|| hero.belongings.ring() == this);
	}

}
