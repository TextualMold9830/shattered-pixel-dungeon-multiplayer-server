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

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.ClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.network.SendData;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.PointF;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class WndClericSpells extends Window {

	protected static final int WIDTH = 120;

	public static int BTN_SIZE = 20;
	public ArrayList<SpellButton> spellBtns = new ArrayList<>();


	public WndClericSpells(HolyTome tome, Hero cleric, boolean info) {
		super(cleric);
		for (int i = 1; i <= Talent.MAX_TALENT_TIERS; i++) {

			ArrayList<ClericSpell> spells = ClericSpell.getSpellList(cleric, i);


			for (ClericSpell spell : spells) {
				SpellButton spellBtn = new SpellButton(spell, tome, info, i, ClericSpell.getSpellID(spell));
				add(spellBtn);
				spellBtns.add(spellBtn);
			}

		}
		SendData.sendWindow(getOwnerHero().networkID, "cleric_spells", getId(), toJSON(info));
	}
	public JSONObject toJSON(boolean info){
		JSONObject object = new JSONObject();
		object.put("info", info);
		JSONArray buttons = new JSONArray();
		for (SpellButton button : spellBtns) {
			buttons.put(button.toJSON());
		}
		object.put("buttons", buttons);
		return object;
	}

	public class SpellButton extends IconButton {

		ClericSpell spell;
		HolyTome tome;
		public boolean info;
		int tier;
		public int spellID;
		public SpellButton(ClericSpell spell, HolyTome tome, boolean info, int tier, int spellID){
			super(new HeroIcon(spell));
			this.spellID = spellID;
			this.tier = tier;
			this.spell = spell;
			this.tome = tome;
			this.info = info;
			if (!tome.canCast(getOwnerHero(), spell)){
				icon.alpha( 0.3f );
			}

		}

		@Override
		protected void onPointerUp() {
			super.onPointerUp();
			if (!tome.canCast(getOwnerHero(), spell)){
				icon.alpha( 0.3f );
			}
		}

		@Override
		protected void layout() {
		}

		public JSONObject toJSON(){
			JSONObject object = new JSONObject();
			object.put("info", info);
			object.put("alpha", tome.canCast(getOwnerHero(), spell) ? 1 : 0.3);
			object.put("tier", tier);
			object.put("icon", spell.icon());
			object.put("spell_id", spellID);
			object.put("spell_short_desc", spell.shortDesc(getOwnerHero()));
			object.put("spell_name", spell.name());
			return object;
		}
		@Override
        public void onClick() {
			if (info){
				GameScene.show(new WndTitledMessage(new HeroIcon(spell), Messages.titleCase(spell.name()), spell.desc(getOwnerHero())));
			} else {
				hide();

				if(!tome.canCast(getOwnerHero(), spell)){
					GLog.w(Messages.get(HolyTome.class, "no_spell"));
				} else {
					spell.onCast(tome, getOwnerHero());

					if (spell.targetingFlags() != -1 && Dungeon.quickslot.contains(tome)){
						tome.targetingSpell = spell;
						//todo: check this
						//QuickSlotButton.useTargeting(Dungeon.quickslot.getSlot(tome));
					}
				}

			}
		}

		@Override
		protected boolean onLongClick() {
			hide();
			tome.setQuickSpell(spell, getOwnerHero());
			return true;
		}


		@Override
		protected String hoverText() {
			return "_" + Messages.titleCase(spell.name()) + "_\n" + spell.shortDesc(getOwnerHero());
		}
	}

}
