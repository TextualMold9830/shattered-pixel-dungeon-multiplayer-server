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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;

public class Dagger extends MeleeWeapon {
	
	{
		image = ItemSpriteSheet.DAGGER;
		hitSound = Assets.Sounds.HIT_STAB;
		hitSoundPitch = 1.1f;

		tier = 1;
		
		bones = false;
	}

	@Override
	public int max(int lvl) {
		return  4*(tier+1) +    //8 base, down from 10
				lvl*(tier+1);   //scaling unchanged
	}
	
	@Override
	public int damageRoll(Char owner) {
		if (owner instanceof Hero) {
			Hero hero = (Hero)owner;
			Char enemy = hero.enemy();
			if (enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)) {
				//deals 75% toward max to max on surprise, instead of min to max.
				int diff = max(owner) - min(owner);
				int damage = augment.damageFactor(Char.combatRoll(
						min(owner) + Math.round(diff*0.75f),
						max(owner)));
				int exStr = hero.STR() - STRReq();
				if (exStr > 0) {
					damage += Char.combatRoll(0, exStr);
				}
				return damage;
			}
		}
		return super.damageRoll(owner);
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	public boolean useTargeting(){
		return false;
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		sneakAbility(hero, target, 5, 2+buffedLvl(), this);
	}

	@Override
	public String abilityInfo(Hero hero) {
		if (levelKnown){
			return Messages.get(this, "ability_desc", 2+buffedLvl());
		} else {
			return Messages.get(this, "typical_ability_desc", 2);
		}
	}

	public static void sneakAbility(Hero hero, Integer target, int maxDist, int invisTurns, MeleeWeapon wep){
		if (target == null) {
			return;
		}

		PathFinder.buildDistanceMap(hero.pos, BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null), maxDist);
		if (PathFinder.distance[target] == Integer.MAX_VALUE || !hero.fieldOfView[target] || hero.rooted) {
			GLog.w(Messages.get(wep, "ability_target_range"));
			if (hero.rooted) PixelScene.shake( 1, 1f );
			return;
		}

		if (Actor.findChar(target) != null) {
			GLog.w(Messages.get(wep, "ability_occupied"));
			return;
		}

		wep.beforeAbilityUsed(hero, null);
		Buff.affect(hero, Invisibility.class, invisTurns-1); //1 fewer turns as ability is instant

		hero.sprite.turnTo( hero.pos, target);
		hero.pos = target;
		Dungeon.level.occupyCell(hero);
		Dungeon.observe();
		GameScene.updateFog();
		hero.checkVisibleMobs();

		hero.sprite.place( hero.pos );
		CellEmitter.get( hero.pos ).burst( Speck.factory( Speck.WOOL ), 6 );
		Sample.INSTANCE.play( Assets.Sounds.PUFF );

		hero.next();
		wep.afterAbilityUsed(hero);
	}
}
