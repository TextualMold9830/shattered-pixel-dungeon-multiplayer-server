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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LostInventory;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.KindofMisc;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ShardOfOblivion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.network.SendData;
import com.shatteredpixel.shatteredpixeldungeon.network.SpecialSlot;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Belongings implements Iterable<Item> {
	public Item getItemInSlot(List<Integer> slot) {
		//fixes crash
		if (slot.isEmpty()) {
			return null;
		}
		if (slot.get(0) < 0) {
			SpecialSlot spec_slot = getSpecialSlots().get(-slot.get(0) - 1);
			slot.remove(0);
			if (slot.isEmpty()) {
				return spec_slot.item;
			} else {
				return ((Bag) spec_slot.item).getItemInSlot(slot);
			}
		}
		return backpack.getItemInSlot(slot);
	}

	private Hero owner;
	public ArrayList<SpecialSlot> getSpecialSlots() {
		ArrayList<SpecialSlot> slots = new ArrayList<>(5);
		slots.add(new SpecialSlot(0, "items.png", ItemSpriteSheet.WEAPON_HOLDER, weapon()));
		slots.add(new SpecialSlot(1, "items.png", ItemSpriteSheet.ARMOR_HOLDER, getRealArmor()));
		slots.add(new SpecialSlot(2, "items.png", ItemSpriteSheet.ARTIFACT_HOLDER, getRealArtifact()));
		slots.add(new SpecialSlot(3, "items.png", ItemSpriteSheet.SOMETHING, misc()));
		slots.add(new SpecialSlot(4, "items.png", ItemSpriteSheet.RING_HOLDER, getRealRing()));
		return slots;
	}

	public List<Integer> pathOfItem(@NotNull Item item) {
		assert (item != null) : "path of null item";
		List<SpecialSlot> specialSlots = getSpecialSlots();
		for (int i = 0; i < specialSlots.size(); i++) {
			if (specialSlots.get(i) == null) {
				continue;
			}
			if (specialSlots.get(i).item == item) {
				List<Integer> slot = new ArrayList<>(2);
				slot.add(-i - 1);
				return slot;
			}
			if (specialSlots.get(i).item instanceof Bag) {
				List<Integer> path = ((Bag) specialSlots.get(i).item).pathOfItem(item);
				if (path != null) {
					path.add(0, -i - 1);
					return path;
				}
			}
		}
		return backpack.pathOfItem(item);
	}

	public KindOfWeapon getRealWeapon() {
		return weapon;
	}

	public KindOfWeapon setWeapon(KindOfWeapon weapon) {
		this.weapon = weapon;
		if (weapon != null) {
			weapon.sendSelfUpdate(owner);
		} else {
			List<Integer> path = new ArrayList<>(1);
			path.add(-1);
			SendData.sendRemoveItemFromInventory(owner, path);
		}
		return weapon;
	}

	public Armor getRealArmor() {
		return armor;
	}

	public Armor setArmor(Armor armor) {
		this.armor = armor;
		if (armor != null) {
			armor.sendSelfUpdate(owner);
			owner.sendSelf();
		} else {
			List<Integer> path = new ArrayList<>(1);
			path.add(-2);
			SendData.sendRemoveItemFromInventory(owner, path);
		}
		return armor;
	}

	public Artifact getRealArtifact() {
		return artifact;
	}

	public Artifact setArtifact(Artifact artifact) {
		this.artifact = artifact;
		if (artifact != null) {
			artifact.sendSelfUpdate(owner);
		} else {
			List<Integer> path = new ArrayList<>(1);
			path.add(-3);
			SendData.sendRemoveItemFromInventory(owner, path);
		}
		return artifact;
	}

	public KindofMisc getRealMisc() {
		return misc;
	}

	public KindofMisc setMisc(KindofMisc misc) {
		this.misc = misc;
		if (misc != null) {
			misc.sendSelfUpdate(owner);
		} else {
			List<Integer> path = new ArrayList<>(1);
			path.add(-4);
			SendData.sendRemoveItemFromInventory(owner, path);
		}
		return misc;
	}

	public Ring getRealRing() {
		return ring;
	}

	public Ring setRing(Ring ring) {
		this.ring = ring;
		if (ring != null) {
			ring.sendSelfUpdate(owner);
		} else {
			List<Integer> path = new ArrayList<>(1);
			path.add(-5);
			SendData.sendRemoveItemFromInventory(owner, path);
		}
		return ring;
	}

	public static class Backpack extends Bag {
		public List<Integer> pathOfItem(Item item) {
			assert (item != null) : "path of null item";
			for (int i = 0; i < items.size(); i++) {
				Item cur_item = items.get(i);
				if (cur_item == null) {
					continue;
				}
				if (cur_item == item) {
					List<Integer> path = new ArrayList<>(2);
					path.add(i);
					return path;
				}
				if (cur_item instanceof Bag) {
					List<Integer> path = ((Bag) cur_item).pathOfItem(item);
					if (path != null) {
						path.add(0, i);
						return path;
					}
				}
			}
			return null;
		}

        @Override
        public @NotNull Icons getBagIcon() {
            return Icons.BACKPACK;
        }

        {
			image = ItemSpriteSheet.BACKPACK;
		}
		public int capacity(Hero hero){
			int cap = super.capacity();
			for (Item item : items){
				if (item instanceof Bag){
					cap++;
				}
			}
			if (hero != null && hero.belongings.secondWep != null){
				//secondary weapons still occupy an inv. slot
				cap--;
			}
			return cap;
		}
	}

	public Backpack backpack;
	
	public Belongings( Hero owner ) {
		this.owner = owner;
		
		backpack = new Backpack();
		backpack.owner = owner;
	}

	private KindOfWeapon weapon = null;
	private Armor armor = null;
	private Artifact artifact = null;
	private KindofMisc misc = null;
	private Ring ring = null;

	//used when thrown weapons temporary become the current weapon
	public KindOfWeapon thrownWeapon = null;

	//used to ensure that the duelist always uses the weapon she's using the ability of
	public KindOfWeapon abilityWeapon = null;

	//used by the champion subclass
	public KindOfWeapon secondWep = null;

	//*** these accessor methods are so that worn items can be affected by various effects/debuffs
	// we still want to access the raw equipped items in cases where effects should be ignored though,
	// such as when equipping something, showing an interface, or dealing with items from a dead hero

	//normally the primary equipped weapon, but can also be a thrown weapon or an ability's weapon
	public KindOfWeapon attackingWeapon(){
		if (thrownWeapon != null) return thrownWeapon;
		if (abilityWeapon != null) return abilityWeapon;
		return weapon();
	}

	//we cache whether belongings are lost to avoid lots of calls to hero.buff(LostInventory.class)
	private boolean lostInvent;
	public void lostInventory( boolean val ){
		lostInvent = val;
	}

	public boolean lostInventory(){
		return lostInvent;
	}

	public KindOfWeapon weapon(){
		if (!lostInventory() || (getRealWeapon() != null && getRealWeapon().keptThroughLostInventory())){
			return getRealWeapon();
		} else {
			return null;
		}
	}

	public Armor armor(){
		if (!lostInventory() || (getRealArmor() != null && getRealArmor().keptThroughLostInventory())){
			return getRealArmor();
		} else {
			return null;
		}
	}

	public Artifact artifact(){
		if (!lostInventory() || (getRealArtifact() != null && getRealArtifact().keptThroughLostInventory())){
			return getRealArtifact();
		} else {
			return null;
		}
	}

	public KindofMisc misc(){
		if (!lostInventory() || (getRealMisc() != null && getRealMisc().keptThroughLostInventory())){
			return getRealMisc();
		} else {
			return null;
		}
	}

	public Ring ring(){
		if (!lostInventory() || (getRealRing() != null && getRealRing().keptThroughLostInventory())){
			return getRealRing();
		} else {
			return null;
		}
	}

	public KindOfWeapon secondWep(){
		if (!lostInventory() || (secondWep != null && secondWep.keptThroughLostInventory())){
			return secondWep;
		} else {
			return null;
		}
	}

	// ***
	
	private static final String WEAPON		= "weapon";
	private static final String ARMOR		= "armor";
	private static final String ARTIFACT   = "artifact";
	private static final String MISC       = "misc";
	private static final String RING       = "ring";

	private static final String SECOND_WEP = "second_wep";

	public void storeInBundle( Bundle bundle ) {
		
		backpack.storeInBundle( bundle );
		
		bundle.put( WEAPON, getRealWeapon());
		bundle.put( ARMOR, getRealArmor());
		bundle.put( ARTIFACT, getRealArtifact());
		bundle.put( MISC, getRealMisc());
		bundle.put( RING, getRealRing());
		bundle.put( SECOND_WEP, secondWep );
	}
	
	public void restoreFromBundle( Bundle bundle ) {
		
		backpack.clear();
		backpack.restoreFromBundle( bundle );
		
		setWeapon((KindOfWeapon) bundle.get(WEAPON));
		if (weapon() != null)       weapon().activate(owner);
		
		setArmor((Armor)bundle.get( ARMOR ));
		if (armor() != null)        armor().activate( owner );

		setArtifact((Artifact) bundle.get(ARTIFACT));
		if (artifact() != null)     artifact().activate(owner);

		setMisc((KindofMisc) bundle.get(MISC));
		if (misc() != null)         misc().activate( owner );

		setRing((Ring) bundle.get(RING));
		if (ring() != null)         ring().activate( owner );

		secondWep = (KindOfWeapon) bundle.get(SECOND_WEP);
		if (secondWep() != null)    secondWep().activate(owner);
	}
	
	public static void preview( GamesInProgress.Info info, Bundle bundle ) {
		if (bundle.contains( ARMOR )){
			Armor armor = ((Armor)bundle.get( ARMOR ));
			if (armor instanceof ClassArmor){
				info.armorTier = 6;
			} else {
				info.armorTier = armor.tier;
			}
		} else {
			info.armorTier = 0;
		}
	}

	//ignores lost inventory debuff
	public ArrayList<Bag> getBags(){
		ArrayList<Bag> result = new ArrayList<>();

		result.add(backpack);

		for (Item i : this){
			if (i instanceof Bag){
				result.add((Bag)i);
			}
		}

		return result;
	}
	
	@SuppressWarnings("unchecked")
	public<T extends Item> T getItem( Class<T> itemClass ) {

		boolean lostInvent = lostInventory();

		for (Item item : this) {
			if (itemClass.isInstance( item )) {
				if (!lostInvent || item.keptThroughLostInventory()) {
					return (T) item;
				}
			}
		}
		
		return null;
	}

	public<T extends Item> ArrayList<T> getAllItems( Class<T> itemClass ) {
		ArrayList<T> result = new ArrayList<>();

		boolean lostInvent = lostInventory();

		for (Item item : this) {
			if (itemClass.isInstance( item )) {
				if (!lostInvent || item.keptThroughLostInventory()) {
					result.add((T) item);
				}
			}
		}

		return result;
	}
	
	public boolean contains( Item contains ){

		boolean lostInvent = lostInventory();
		
		for (Item item : this) {
			if (contains == item) {
				if (!lostInvent || item.keptThroughLostInventory()) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public Item getSimilar( Item similar ){

		boolean lostInvent = lostInventory();
		
		for (Item item : this) {
			if (similar != item && similar.isSimilar(item)) {
				if (!lostInvent || item.keptThroughLostInventory()) {
					return item;
				}
			}
		}
		
		return null;
	}
	
	public ArrayList<Item> getAllSimilar( Item similar ){
		ArrayList<Item> result = new ArrayList<>();

		boolean lostInvent = lostInventory();
		
		for (Item item : this) {
			if (item != similar && similar.isSimilar(item)) {
				if (!lostInvent || item.keptThroughLostInventory()) {
					result.add(item);
				}
			}
		}
		
		return result;
	}

	//triggers when a run ends, so ignores lost inventory effects
	public void identify() {
		for (Item item : this) {
			item.identify(false, owner);
		}
	}
	
	public void observe() {
		if (weapon() != null) {
			if (ShardOfOblivion.passiveIDDisabled() && weapon() instanceof Weapon){
				((Weapon) weapon()).setIDReady();
			} else {
				weapon().identify(owner);
				Badges.validateItemLevelAquired(weapon());
			}
		}
		if (secondWep() != null){
			if (ShardOfOblivion.passiveIDDisabled() && secondWep() instanceof Weapon){
				((Weapon) secondWep()).setIDReady();
			} else {
				secondWep().identify(owner);
				Badges.validateItemLevelAquired(secondWep());
			}
		}
		if (armor() != null) {
			if (ShardOfOblivion.passiveIDDisabled()){
				armor().setIDReady();
			} else {
				armor().identify(owner);
				Badges.validateItemLevelAquired(armor());
			}
		}
		if (artifact() != null) {
			//oblivion shard does not prevent artifact IDing
			artifact().identify(owner);
			Badges.validateItemLevelAquired(artifact());
		}
		if (misc() != null) {
			if (ShardOfOblivion.passiveIDDisabled() && misc() instanceof Ring){
				((Ring) misc()).setIDReady();
			} else {
				misc().identify(owner);
				Badges.validateItemLevelAquired(misc());
			}
		}
		if (ring() != null) {
			if (ShardOfOblivion.passiveIDDisabled()){
				ring().setIDReady();
			} else {
				ring().identify(owner);
				Badges.validateItemLevelAquired(ring());
			}
		}
		if (ShardOfOblivion.passiveIDDisabled()){
			GLog.p(Messages.get(ShardOfOblivion.class, "identify_ready_worn"));
		}
		for (Item item : backpack) {
			if (item instanceof EquipableItem || item instanceof Wand) {
				item.cursedKnown = true;
				item.sendSelfUpdate(owner);
			}
		}
		Item.updateQuickslot(owner, null);
	}
	
	public void uncurseEquipped() {
		ScrollOfRemoveCurse.uncurse( owner, armor(), weapon(), artifact(), misc(), ring(), secondWep());
	}
	
	public Item randomUnequipped() {
		if (owner.buff(LostInventory.class) != null) return null;

		return Random.element( backpack.items );
	}
	
	public int charge( float charge ) {
		
		int count = 0;
		
		for (Wand.Charger charger : owner.buffs(Wand.Charger.class)){
			charger.gainCharge(charge);
			count++;
		}
		
		return count;
	}

	@Override
	public Iterator<Item> iterator() {
		return new ItemIterator();
	}
	
	private class ItemIterator implements Iterator<Item> {

		private int index = 0;
		
		private Iterator<Item> backpackIterator = backpack.iterator();
		
		private Item[] equipped = {getRealWeapon(), getRealArmor(), getRealArtifact(), getRealMisc(), getRealRing(), secondWep};
		private int backpackIndex = equipped.length;
		
		@Override
		public boolean hasNext() {
			
			for (int i=index; i < backpackIndex; i++) {
				if (equipped[i] != null) {
					return true;
				}
			}
			
			return backpackIterator.hasNext();
		}

		@Override
		public Item next() {
			
			while (index < backpackIndex) {
				Item item = equipped[index++];
				if (item != null) {
					return item;
				}
			}
			
			return backpackIterator.next();
		}

		@Override
		public void remove() {
			switch (index) {
			case 0:
				equipped[0] = setWeapon(null);
				break;
			case 1:
				equipped[1] = setArmor(null);
				break;
			case 2:
				equipped[2] = setArtifact(null);
				break;
			case 3:
				equipped[3] = setMisc(null);
				break;
			case 4:
				equipped[4] = setRing(null);
				break;
			case 5:
				equipped[5] = secondWep = null;
				break;
			default:
				backpackIterator.remove();
			}
		}
	}
}
