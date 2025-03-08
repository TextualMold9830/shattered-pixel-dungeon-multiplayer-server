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

package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.HeroHelp;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.LiquidMetal;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.AlchemistsToolkit;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrinketCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.journal.Journal;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndEnergizeItem;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoItem;
import com.nikita22007.multiplayer.noosa.audio.Sample;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class AlchemyScene extends Window {

	//max of 3 inputs, and 3 potential recipe outputs
	private static final InputButton[] inputs = new InputButton[3];
	private static final CombineButton[] combines = new CombineButton[3];
	private static final Item[] outputs = new Item[3];
	
	private ArrayList<Item> lastIngredients = new ArrayList<>();
	private static final Map<Integer,ArrayList<Item>> lastIngredientsCommon = new Hashtable<>();
	private static Recipe lastRecipe = null;

	private boolean energyAddBlinking = false;
	private boolean repeat_enabled = false;

	private static AlchemyScene[] activeAlchemyScene = new AlchemyScene[0];

	private AlchemistsToolkit toolkit;

	protected void enableAlchemyScene(Hero hero){
		if (activeAlchemyScene.length != SPDSettings.maxPlayers()){
			activeAlchemyScene = new AlchemyScene[SPDSettings.maxPlayers()];
		}
		activeAlchemyScene[HeroHelp.getHeroID(hero)] = this;
	}
	protected static void disableAlchemyScene(Hero hero){
		activeAlchemyScene[HeroHelp.getHeroID(hero)] = null;
	}

	public static boolean isAlchemySceneEnabled(Hero hero) {
		if (activeAlchemyScene.length != SPDSettings.maxPlayers()){
			return false;
		}
		return activeAlchemyScene[HeroHelp.getHeroID(hero)] != null;
	}

	public static AlchemyScene getActiveAlchemyScene(Hero hero) {
		if (activeAlchemyScene.length != SPDSettings.maxPlayers()){
			return null;
		}
		return activeAlchemyScene[HeroHelp.getHeroID(hero)];
	}

	public AlchemyScene(@NotNull Hero hero, @Nullable AlchemistsToolkit toolkit){
		super(hero);
		enableAlchemyScene(hero);
		this.toolkit = toolkit;

		if (lastIngredientsCommon.containsKey(HeroHelp.getHeroID(hero))) {
			lastIngredients = lastIngredientsCommon.get(HeroHelp.getHeroID(hero));
		} else {
			lastIngredients = new ArrayList<>();
		}
		this.create();
	}

	@Override
	public void onSelect(int button, JSONObject args) {
		boolean longClick = args != null && args.has("long_click") && args.getBoolean("long_click");
		switch (button) {
			case 0: { //btn exit
				hide();
				break;
			}
			case 1: {
				//btn cancel
				clearSlots();
				updateState();
				break;
			}
			case 2: {
				//btn repeat
				if (repeat_enabled && lastRecipe != null) {
					populate(lastIngredients, getOwnerHero().belongings);
				}
				break;
			}
			case 3: {
				//energyAdd button
				WndEnergizeItem.openItemSelector(getOwnerHero());
				break;
			}
			case 100:
			case 101:
			case 102: {
				if (!longClick) {
					inputs[button - 100].onClick();
				} else {
					inputs[button - 100].onLongClick();
				}
				break;
			}
			case 200:
			case 201:
			case 202: {
				if (!longClick) {
					combines[button - 200].onClick();
				}
				break;
			}
			case 300:
			case 301:
			case 302: {
				// output
				// nobody should call this but we add fallback for it
				Item item = outputs[button - 300];
				if (item != null && item.trueName() != null)
					GameScene.show(new WndInfoItem(item, getOwnerHero()));
			}
			break;
		}
	}

	@Override
	public void hide() {
		super.hide();
		lastIngredientsCommon.put(HeroHelp.getHeroID(getOwnerHero()), lastIngredients);
		clearSlots();
		disableAlchemyScene(getOwnerHero());
	}

	private JSONObject getParamsObject() {
		JSONObject params = new JSONObject();
		params.put("energy", Dungeon.energy);
		params.put("has_toolkit", toolkit != null);
		if (toolkit != null) {
			params.put("toolkit_energy", toolkit.energyVal());
		}

		{
			JSONArray inputsArr = new JSONArray();
			for (InputButton input : inputs) {
				if (input == null) continue;
				if (input.item == null) continue;
				inputsArr.put(input.item.toJsonObject(getOwnerHero()));
			}
			params.put("input", inputsArr);
		}
		{
			JSONArray outputsArr = new JSONArray();
			for (int i = 0; i < outputs.length; i++) {
				Item output = outputs[i];
				if (output == null) continue;
				JSONObject outputObj = new JSONObject();
				outputObj.put("cost", combines[i].cost);
				outputObj.put("enabled", combines[i].enabled);
				outputObj.put("item", output.toJsonObject(getOwnerHero()));
			}
			params.put("output", outputsArr);
		}

		params.put("energyAddBlinking", energyAddBlinking);
		params.put("repeat_enabled",  repeat_enabled);
		return params;
	}

	public void create() {

		synchronized (inputs) {
			for (int i = 0; i < inputs.length; i++) {
				inputs[i] = new InputButton();
			}
		}

		lastIngredients.clear();
		lastRecipe = null;

		for (int i = 0; i < inputs.length; i++) {
			combines[i] = new CombineButton(i);
			combines[i].enable(false);

			outputs[i] = null;
		}

		try {
			Dungeon.saveAll();
			Badges.saveGlobal();
			Journal.saveGlobal();
		} catch (IOException e) {
			ShatteredPixelDungeon.reportException(e);
		}
	}


	@Override
	public void onBackPressed() {
		this.hide();
	}

	protected WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {

		@Override
		public String textPrompt() {
			return Messages.get(AlchemyScene.class, "select");
		}

		@Override
		public boolean itemSelectable(Item item) {
			return Recipe.usableInRecipe(item);
		}

		@Override
		public void onSelect(Item item) {
			synchronized (inputs) {
				if (item != null && inputs[0] != null) {
					for (int i = 0; i < inputs.length; i++) {
						if (inputs[i].item() == null) {
							if (item instanceof LiquidMetal) {
								inputs[i].item(item.detachAll(getOwner().belongings.backpack));
							} else {
								inputs[i].item(item.detach(getOwner().belongings.backpack));
							}
							break;
						}
					}
					updateState();
				}
			}
		}
	};

	private <T extends Item> ArrayList<T> filterInput(Class<? extends T> itemClass) {
		ArrayList<T> filtered = new ArrayList<>();
		for (int i = 0; i < inputs.length; i++) {
			Item item = inputs[i].item();
			if (item != null && itemClass.isInstance(item)) {
				filtered.add((T) item);
			}
		}
		return filtered;
	}

	private void updateState() {
		Hero hero = getOwnerHero();

		repeat_enabled = (false);

		ArrayList<Item> ingredients = filterInput(Item.class);
		ArrayList<Recipe> recipes = Recipe.findRecipes(ingredients);

		//disables / hides unneeded buttons
		for (int i = recipes.size(); i < combines.length; i++) {
			combines[i].enable(false);
			outputs[i] = null;
		}

		if (recipes.isEmpty()) {
			energyAddBlinking = false;
			return;
		}
		

		//positions and enables active buttons
		boolean promptToAddEnergy = false;
		for (int i = 0; i < recipes.size(); i++) {

			Recipe recipe = recipes.get(i);

			int cost = recipe.cost(ingredients);

			outputs[i] = recipe.sampleOutput(ingredients, getOwnerHero());

			int availableEnergy = Dungeon.energy;
			if (toolkit != null) {
				availableEnergy += toolkit.availableEnergy();
			}

			combines[i].enable(cost <= availableEnergy, cost);

			if (cost > availableEnergy && recipe instanceof TrinketCatalyst.Recipe) {
				promptToAddEnergy = true;
			}

		}

		energyAddBlinking = promptToAddEnergy;

	}

	private void combine(int slot) {
		Hero hero = getOwnerHero();

		ArrayList<Item> ingredients = filterInput(Item.class);
		if (ingredients.isEmpty()) return;

		lastIngredients.clear();
		for (Item i : ingredients) {
			lastIngredients.add(i.duplicate());
		}

		ArrayList<Recipe> recipes = Recipe.findRecipes(ingredients);
		if (recipes.size() <= slot) return;

		Recipe recipe = recipes.get(slot);

		Item result = null;

		if (recipe != null) {
			int cost = recipe.cost(ingredients);
			if (toolkit != null) {
				cost = toolkit.consumeEnergy(cost, hero);
			}
			Dungeon.energy -= cost;

			result = recipe.brew(ingredients, hero);
		}

		if (result != null) {
			craftItem(ingredients, result);
		}

		boolean foundItems = true;
		for (Item i : lastIngredients) {
			Item found = hero.belongings.getSimilar(i);
			if (found == null) { //atm no quantity check as items are always loaded individually
				//currently found can be true if we need, say, 3x of an item but only have 2x of it
				foundItems = false;
			}
		}

		lastRecipe = recipe;
		repeat_enabled = (foundItems);
	}

	public void craftItem(ArrayList<Item> ingredients, Item result) {
		Hero hero = getOwnerHero();

		//todo sendVisual
		//bubbleEmitter.start(Speck.factory(Speck.BUBBLE), 0.01f, 100);
		//smokeEmitter.burst(Speck.factory(Speck.WOOL), 10);
		Sample.INSTANCE.play(Assets.Sounds.PUFF, hero);

		int resultQuantity = result.quantity();
		if (!result.collect(hero)) {
			Dungeon.level.drop(result, hero.pos);
		}

		Statistics.itemsCrafted++;
		Badges.validateItemsCrafted();

		try {
			Dungeon.saveAll();
		} catch (IOException e) {
			ShatteredPixelDungeon.reportException(e);
		}

		synchronized (inputs) {
			for (int i = 0; i < inputs.length; i++) {
				if (inputs[i] != null && inputs[i].item() != null) {
					Item item = inputs[i].item();
					if (item.quantity() <= 0) {
						inputs[i].item(null);
					}
				}
			}
		}

		updateState();
		//we reset the quantity in case the result was merged into another stack in the backpack
		result.quantity(resultQuantity);
		outputs[0] = (result);
	}

	public void populate(ArrayList<Item> toFind, Belongings inventory) {
		clearSlots();

		int curslot = 0;
		for (Item finding : toFind) {
			int needed = finding.quantity();
			ArrayList<Item> found = inventory.getAllSimilar(finding);
			while (!found.isEmpty() && needed > 0) {
				Item detached;
				if (finding instanceof LiquidMetal) {
					detached = found.get(0).detachAll(inventory.backpack);
				} else {
					detached = found.get(0).detach(inventory.backpack);
				}
				inputs[curslot].item(detached);
				curslot++;
				needed -= detached.quantity();
				if (detached == found.get(0)) {
					found.remove(0);
				}
			}
		}
		updateState();
	}

	@Override
	public void destroy() {
		synchronized (inputs) {
			clearSlots();
			for (int i = 0; i < inputs.length; i++) {
				inputs[i] = null;
			}
		}

		try {
			Dungeon.saveAll();
			Badges.saveGlobal();
			Journal.saveGlobal();
		} catch (IOException e) {
			ShatteredPixelDungeon.reportException(e);
		}
		super.destroy();
	}

	public void clearSlots() {
		synchronized (inputs) {
			for (int i = 0; i < inputs.length; i++) {
				if (inputs[i] != null && inputs[i].item() != null) {
					Item item = inputs[i].item();
					if (!item.collect(getOwnerHero())) {
						Dungeon.level.drop(item, getOwnerHero().pos);
					}
					inputs[i].item(null);
				}
			}
		}
		repeat_enabled = (lastRecipe != null);
		updateState();
	}

	public void createEnergy() {
		//todo send this visual as action
		updateState();
	}

	private class InputButton {

		private Item item = null;

		public void onClick() {
			Item item = InputButton.this.item;
			if (item != null) {
				if (!item.collect(getOwnerHero())) {
					Dungeon.level.drop(item, getOwnerHero().pos);
				}
				InputButton.this.item(null);
				updateState();
			}
			AlchemyScene.this.addToFront(WndBag.getBag(itemSelector, getOwnerHero()));
		}

		protected boolean onLongClick() {
			Item item = InputButton.this.item;
			if (item != null){
				GameScene.show(new WndInfoItem(item, AlchemyScene.this.getOwnerHero()));
				return true;
			}
			return false;
		}

		public Item item() {
			return item;
		}

		public void item(Item item) {
			this.item = item;
		}
	}

	private class CombineButton {

		protected int slot;
		public int cost = 0;

		public boolean enabled = false;

		private CombineButton(int slot) {
			this.slot = slot;
		}

		public void onClick() {
			if (enabled) {
				combine(slot);
			}
		}

		public void enable(boolean enabled) {
			enable(enabled, 0);
		}

		public void enable(boolean enabled, int cost) {
			this.enabled = enabled;
			this.cost = cost;
		}

	}
}
