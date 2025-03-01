/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LifeLink;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.PowerOfMany;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

public class LifeLinkSpell extends ClericSpell {

	public static LifeLinkSpell INSTANCE = new LifeLinkSpell();

	@Override
	public int icon() {
		return HeroIcon.LIFE_LINK;
	}

	@Override
	public String desc(Hero hero) {
		return Messages.get(this, "desc", 4 + 2*hero.pointsInTalent(Talent.LIFE_LINK), 30 + 5*hero.pointsInTalent(Talent.LIFE_LINK)) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(hero));
	}

	@Override
	public boolean canCast(Hero hero) {
		return super.canCast(hero)
				&& hero.hasTalent(Talent.LIFE_LINK)
				&& (PowerOfMany.getPoweredAlly() != null || Stasis.getStasisAlly(hero) != null);
	}

	@Override
	public float chargeUse(Hero hero) {
		return 2;
	}

	@Override
	public void onCast(HolyTome tome, Hero hero) {

		int duration = 4 + 2*hero.pointsInTalent(Talent.LIFE_LINK);

		Char ally = PowerOfMany.getPoweredAlly();

		if (ally != null) {
			hero.getSprite().zap(ally.pos);
			hero.getSprite().parent.add(
					new Beam.HealthRay(hero.getSprite().center(), ally.getSprite().center()));

			LifeLink lifeLink = Buff.prolong(hero, LifeLink.class, duration);
			lifeLink.source = hero;
			lifeLink.object = ally.id();
		} else {
			ally = Stasis.getStasisAlly(hero);
			hero.getSprite().operate(hero.pos);
			hero.getSprite().parent.add(
					new Beam.HealthRay(DungeonTilemap.tileCenterToWorld(hero.pos), hero.getSprite().center()));
		}

		LifeLink lifeLink = Buff.prolong(ally, LifeLink.class, duration);
		lifeLink.source = hero;
		lifeLink.object = hero.id();

		if (ally == Stasis.getStasisAlly(hero)){
			ally.buff(LifeLink.class).clearTime();
			ally.buff(LifeLinkSpellBuff.class).clearTime();
		}

		hero.spendAndNext(Actor.TICK);

		onSpellCast(tome, hero);

	}

	public static class LifeLinkSpellBuff extends FlavourBuff {
		public Hero source;
		{
			type = buffType.POSITIVE;
		}

		@Override
		public int icon() {
			return BuffIndicator.HOLY_ARMOR;
		}

		@Override
		public float iconFadePercent() {
			int duration = 4 + 2*source.pointsInTalent(Talent.LIFE_LINK);
			return Math.max(0, (duration - visualcooldown()) / duration);
		}
	}
}
