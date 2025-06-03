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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Shopkeeper;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.MasterThievesArmband;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.network.SendData;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;

import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import java.lang.reflect.Method;

public class WndTradeItem extends WndInfoItem {

	private static final float GAP		= 2;
	private static final int BTN_HEIGHT	= 18;

	private WndBag owner;

	private boolean selling = false;
	Item item;
	Shopkeeper shop;

	//selling
	public WndTradeItem( final Item item, @NotNull WndBag owner ) {

		super(item, owner.getOwnerHero());
		selling = true;

		this.owner = owner;
		this.item = item;

		//find the shopkeeper in the current level
		for (Char ch : Actor.chars()){
			if (ch instanceof Shopkeeper){
				this.shop = (Shopkeeper) ch;
				break;
			}
		}
		JSONObject object = new JSONObject();
		object.put("item", item.toJsonObject(getOwnerHero()));
		object.put("selling", true);
		object.put("price", item.value());
		SendData.sendWindow(getOwnerHero().networkID, "trade_item", getId(), object);
	}
	RedButton btnSteal = null;
	Heap heap;

	//buying
	public WndTradeItem( final Heap heap, Hero hero ) {

		super(heap, hero);

		selling = false;

		item = heap.peek();

		float pos = height;

		final int price = Shopkeeper.sellPrice( item );
		JSONObject object = new JSONObject();
		object.put("selling", false);
		object.put("price", price);
		object.put("item", item.toJsonObject(hero));
		RedButton btnBuy = new RedButton( Messages.get(this, "buy", price) ) {
			@Override
			protected void onClick() {
				hide();
				buy( heap );
			}
		};
		btnBuy.setRect( 0, pos + GAP, width, BTN_HEIGHT );
		btnBuy.icon(new ItemSprite(ItemSpriteSheet.GOLD));
		btnBuy.enable( price <= getOwnerHero().getGold());
		add( btnBuy );

		pos = btnBuy.bottom();

		final MasterThievesArmband.Thievery thievery = getOwnerHero().buff(MasterThievesArmband.Thievery.class);
		if (thievery != null && !thievery.isCursed() && thievery.chargesToUse(item) > 0) {
			final float chance = thievery.stealChance(item);
			final int chargesToUse = thievery.chargesToUse(item);
			object.put("steal", true);
			object.put("chance", Math.min(100, (int) (chance * 100)));
			object.put("charges", chargesToUse);
            btnSteal = new RedButton(Messages.get(WndTradeItem.this, "steal", Math.min(100, (int) (chance * 100)), chargesToUse), 6) {
                @Override
                protected void onClick() {
                    if (chance >= 1){
                        thievery.steal(item);
                        Hero hero1 = getOwnerHero();
                        Item item1 = heap.pickUp();
                        hide();

                        if (!item1.doPickUp(hero1)) {
                            Dungeon.level.drop(item1, heap.pos).sprite.drop();
                        }
                    } else {
                        GameScene.show(new WndOptions(hero, new ItemSprite(ItemSpriteSheet.ARTIFACT_ARMBAND),
                                Messages.titleCase(Messages.get(MasterThievesArmband.class, "name")),
                                Messages.get(WndTradeItem.class, "steal_warn"),
                                Messages.get(WndTradeItem.class, "steal_warn_yes"),
                                Messages.get(WndTradeItem.class, "steal_warn_no")){
                            @Override
                            protected void onSelect(int index) {
                                super.onSelect(index);
                                if (index == 0){
                                    if (thievery.steal(item)) {
                                        Hero hero1 = getOwnerHero();
                                        Item item1 = heap.pickUp();
                                        WndTradeItem.this.hide();

                                        if (!item1.doPickUp(hero1)) {
                                            Dungeon.level.drop(item1, heap.pos).sprite.drop();
                                        }
                                    } else {
                                        for (Mob mob : Dungeon.level.mobs) {
                                            if (mob instanceof Shopkeeper) {
                                                mob.yell(Messages.get(mob, "thief"));
                                                ((Shopkeeper) mob).flee();
                                                break;
                                            }
                                        }
                                        WndTradeItem.this.hide();
                                    }
                                }
                            }
                        });
                    }
                }
            };
		}
		SendData.sendWindow(hero.networkID, "trade_item", getId(), object);
	}

	@Override
	public void onSelect(int button) {
		if (selling) {
			if (button == 0) {
				sellOne(item, shop, owner.getOwnerHero());
			} if (button == 1){
				sell(item, shop, owner.getOwnerHero());
			}
			//Selling logic
		} else {
			if (button == 0) {
				buy(heap);
			}
			if (button == 1 && btnSteal != null) {
				//HACK
				//don't think I should change onClick
                try {
                    Method onClickMethod = Button.class.getMethod("onClick");
					onClickMethod.setAccessible(true);
					onClickMethod.invoke(btnSteal);
                } catch (Exception e) {

                    throw new RuntimeException(e);
                }
			}
			//buying logic
		}
		hide();
	}

	@Override
	public void hide() {
		
		super.hide();
		
		if (owner != null) {
			owner.hide();
		}
		if (selling) Shopkeeper.sell(getOwnerHero());
	}

	public static void sell( Item item, Hero hero ) {
		sell(item, null, hero);
	}

	public static void sell( Item item, Shopkeeper shop, Hero hero ) {


		if (item.isEquipped( hero ) && !((EquipableItem)item).doUnequip( hero, false )) {
			return;
		}
		item.detachAll( hero.belongings.backpack );

		//selling items in the sell interface doesn't spend time
		hero.spend(-hero.cooldown());

		new Gold( item.value() ).doPickUp( hero );

		if (shop != null){
			shop.buybackItems.add(item);
			while (shop.buybackItems.size() > Shopkeeper.MAX_BUYBACK_HISTORY){
				shop.buybackItems.remove(0);
			}
		}
	}

	public static void sellOne( Item item, Hero hero ) {
		sellOne( item, null, hero );
	}

	public static void sellOne( Item item, Shopkeeper shop, Hero hero ) {
		
		if (item.quantity() <= 1) {
			sell( item, shop, hero);
		} else {
			

			item = item.detach( hero.belongings.backpack );

			//selling items in the sell interface doesn't spend time
			hero.spend(-hero.cooldown());

			new Gold( item.value() ).doPickUp( hero );

			if (shop != null){
				shop.buybackItems.add(item);
				while (shop.buybackItems.size() > Shopkeeper.MAX_BUYBACK_HISTORY){
					shop.buybackItems.remove(0);
				}
			}
		}
	}
	
	private void buy( Heap heap ) {
		if (getOwnerHero().getGold() >= Shopkeeper.sellPrice(item)) {
		Item item = heap.pickUp();
		if (item == null) return;

		int price = Shopkeeper.sellPrice( item );
		getOwnerHero().setGold(getOwnerHero().getGold() - price);
		Catalog.countUses(Gold.class, price);

		if (!item.doPickUp( getOwnerHero())) {
			Dungeon.level.drop( item, heap.pos ).sprite.drop();
		}
		}
		GLog.n("Come back when you're a little... mmmmmm ...richer!");

	}
}
