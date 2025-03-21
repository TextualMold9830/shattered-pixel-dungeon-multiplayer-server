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

package com.shatteredpixel.shatteredpixeldungeon.items.food;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class SupplyRation extends Food {

	{
		image = ItemSpriteSheet.SUPPLY_RATION;
		energy = 2*Hunger.HUNGRY/3f; //200 food value

		bones = false;
	}

	@Override
	protected float eatingTime(Hero hero){
		if (hero.hasTalent(Talent.IRON_STOMACH)
				|| hero.hasTalent(Talent.ENERGIZING_MEAL)
				|| hero.hasTalent(Talent.MYSTICAL_MEAL)
				|| hero.hasTalent(Talent.INVIGORATING_MEAL)
				|| hero.hasTalent(Talent.FOCUSED_MEAL)
				|| hero.hasTalent(Talent.ENLIGHTENING_MEAL)){
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	protected void satisfy(Hero hero) {
		super.satisfy(hero);

		hero.setHP(Math.min(hero.getHP() + 5, hero.getHT()));
		hero.getSprite().showStatusWithIcon( CharSprite.POSITIVE, "5", FloatingText.HEALING );

		CloakOfShadows cloak = hero.belongings.getItem(CloakOfShadows.class);
		if (cloak != null) {
			cloak.directCharge(1, hero);
			ScrollOfRecharging.charge(hero);
		}
	}

	@Override
	public int value() {
		return 10 * quantity();
	}

}
