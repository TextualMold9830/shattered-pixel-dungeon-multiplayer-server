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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.network.SendData;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.FetidRatSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GnollTricksterSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GreatCrabSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import org.json.JSONObject;

public class WndSadGhost extends Window {

	private static final int WIDTH		= 120;
	private static final int BTN_SIZE	= 32;
	private static final int BTN_GAP	= 5;
	private static final int GAP		= 2;

	Ghost ghost;
	
	public WndSadGhost( final Ghost ghost, final int type, Hero hero ) {
		super(hero);
		JSONObject args = new JSONObject();
		args.put("type", type);
		args.put("weapon", Item.packItem(Ghost.Quest.weapon, hero));
		args.put("armor", Item.packItem(Ghost.Quest.armor, hero));
		this.ghost = ghost;
		SendData.sendWindow(hero.networkID, "sad_ghost", getId(), args);
	}
	
	private void selectReward( Item reward ) {
		
		hide();
		
		if (reward == null) return;

		if (reward instanceof Weapon && Ghost.Quest.enchant != null){
			((Weapon) reward).enchant(Ghost.Quest.enchant);
		} else if (reward instanceof Armor && Ghost.Quest.glyph != null){
			((Armor) reward).inscribe(Ghost.Quest.glyph);
		}
		
		reward.identify(false, getOwnerHero());
		if (reward.doPickUp( getOwnerHero())) {
			GLog.i( Messages.capitalize(Messages.get(Dungeon.heroes, "you_now_have", reward.name())) );
		} else {
			Dungeon.level.drop( reward, ghost.pos ).sprite.drop();
		}
		
		ghost.yell( Messages.get(this, "farewell") );
		ghost.die( new Char.DamageCause(null) );
		
		Ghost.Quest.complete();
	}

	@Override
	protected void onSelect(int button) {
		if (Ghost.Quest.processed() && !Ghost.Quest.completed()) {
			if (button == 0) {
				WndSadGhost.this.selectReward( Ghost.Quest.weapon );
			} else if(button == 1){
				WndSadGhost.this.selectReward( Ghost.Quest.armor );
			}
			Ghost.Quest.complete();
		}
	}

	private class RewardWindow extends WndInfoItem {

		public RewardWindow( Item item, Hero rewardTarget ) {
			super(item, rewardTarget);

			RedButton btnConfirm = new RedButton(Messages.get(WndSadGhost.class, "confirm")){
				@Override
				protected void onClick() {
					RewardWindow.this.hide();

					WndSadGhost.this.selectReward( item );
				}
			};
			btnConfirm.setRect(0, height+2, width/2-1, 16);
			add(btnConfirm);

			RedButton btnCancel = new RedButton(Messages.get(WndSadGhost.class, "cancel")){
				@Override
				protected void onClick() {
					RewardWindow.this.hide();
				}
			};
			btnCancel.setRect(btnConfirm.right()+2, height+2, btnConfirm.width(), 16);
			add(btnCancel);

			resize(width, (int)btnCancel.bottom());
		}
	}
}