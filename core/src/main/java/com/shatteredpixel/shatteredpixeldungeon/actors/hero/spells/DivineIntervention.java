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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShieldBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.AscendedForm;
import com.nikita22007.multiplayer.server.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.nikita22007.multiplayer.noosa.audio.Sample;

public class DivineIntervention extends ClericSpell {

	public static DivineIntervention INSTANCE = new DivineIntervention();

	@Override
	public int icon() {
		return HeroIcon.DIVINE_INTERVENTION;
	}

	@Override
	public float chargeUse(Hero hero) {
		return 5;
	}

	@Override
	public boolean canCast(Hero hero) {
		return super.canCast(hero)
				&& hero.hasTalent(Talent.DIVINE_INTERVENTION)
				&& hero.buff(AscendedForm.AscendBuff.class) != null
				&& !hero.buff(AscendedForm.AscendBuff.class).divineInverventionCast;
	}

	@Override
	public void onCast(HolyTome tome, Hero hero) {

		Sample.INSTANCE.play(Assets.Sounds.CHARGEUP, 1, 1.2f);
		hero.getSprite().operate(hero.pos);

		for (Char ch : Actor.chars()){
			if (ch.alignment == Char.Alignment.ALLY && ch != hero){
				DivineShield divineShield = Buff.affect(ch, DivineShield.class);
				divineShield.source = hero;
				divineShield.setShield(100 + 50*hero.pointsInTalent(Talent.DIVINE_INTERVENTION));
				new Flare(6, 32).color(0xFFFF00, true).show(ch.getSprite(), 2f);
			}
		}

		hero.spendAndNext( 1f );
		onSpellCast(tome, hero);

		//we apply buffs here so that the 5 charge cost and shield boost do not stack
		hero.buff(AscendedForm.AscendBuff.class).setShield(100 + 50*hero.pointsInTalent(Talent.DIVINE_INTERVENTION));
		new Flare(6, 32).color(0xFFFF00, true).show(hero.getSprite(), 2f);

		hero.buff(AscendedForm.AscendBuff.class).divineInverventionCast = true;
		hero.buff(AscendedForm.AscendBuff.class).extend(hero.pointsInTalent(Talent.DIVINE_INTERVENTION));

	}

	@Override
	public String desc(Hero hero) {
		int shield = 100 + 50*hero.pointsInTalent(Talent.DIVINE_INTERVENTION);
		int leftBonus = hero.pointsInTalent(Talent.DIVINE_INTERVENTION);
		return Messages.get(this, "desc", shield, leftBonus) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(hero));
	}

	public static class DivineShield extends ShieldBuff{
	Hero source;
		@Override
		public boolean act() {

			if (target == null || source.buff(AscendedForm.AscendBuff.class) == null){
				detach();
			}

			spend(TICK);
			return true;
		}

		@Override
		public int shielding() {
			if (source == null || source.buff(AscendedForm.AscendBuff.class) == null){
				return 0;
			}
			return super.shielding();
		}

		@Override
		public void fx(boolean on) {
			if (on) target.getSprite().add(CharSprite.State.SHIELDED);
			else    target.getSprite().remove(CharSprite.State.SHIELDED);
		}
	}
}
