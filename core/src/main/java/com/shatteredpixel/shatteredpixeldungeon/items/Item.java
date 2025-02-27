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

import com.nikita22007.multiplayer.utils.Log;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Degrade;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.TippedDart;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.network.NetworkPacket;
import com.shatteredpixel.shatteredpixeldungeon.network.SendData;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.nikita22007.multiplayer.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.pixeldungeon.utils.Utils;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Reflection;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.shatteredpixel.shatteredpixeldungeon.network.SendData.sendNewInventoryItem;
import static com.shatteredpixel.shatteredpixeldungeon.network.SendData.sendRemoveItemFromInventory;
import static com.shatteredpixel.shatteredpixeldungeon.network.SendData.sendUpdateItemCount;
import static com.shatteredpixel.shatteredpixeldungeon.network.SendData.sendUpdateItemFull;
//FIXME
public class Item implements Bundlable {

	protected static final String TXT_TO_STRING_LVL		= "%s %+d";
	protected static final String TXT_TO_STRING_X		= "%s x%d";
	
	protected static final float TIME_TO_THROW		= 1.0f;
	protected static final float TIME_TO_PICK_UP	= 1.0f;
	protected static final float TIME_TO_DROP		= 1.0f;
	
	public static final String AC_DROP		= "DROP";
	public static final String AC_THROW		= "THROW";
	
	protected String defaultAction;
	public boolean usesTargeting;

	//TODO should these be private and accessed through methods?
	public int image = 0;
	public int icon = -1; //used as an identifier for items with randomized images
	
	public boolean stackable = false;
	private int quantity = 1;
	public boolean dropsDownHeap = false;
	
	private int level = 0;

	public boolean levelKnown = false;
	
	public boolean cursed;
	public boolean cursedKnown;
	
	// Unique items persist through revival
	public boolean unique = false;

	// These items are preserved even if the hero's inventory is lost via unblessed ankh
	// this is largely set by the resurrection window, items can override this to always be kept
	public boolean keptThoughLostInvent = false;

	// whether an item can be included in heroes remains
	public boolean bones = false;
	// Can be changed to be used in custom textures
	private String spriteSheet = Assets.Sprites.ITEMS;
	// I don't know what I am doing
	protected static final String TXT_STRENGTH = ":%d";
	public static final int DEGRADED = 0xFF4444;
	public static final int UPGRADED = 0x44FF44;
	public static final int WARNING = 0xFF8800;
	public static final int MASTERED	= 0xFFFF44;
	public static final int CURSE_INFUSED	= 0x8800FF;
	protected static final String TXT_TYPICAL_STR = "%d?";
	protected static final String TXT_LEVEL = "%+d";
	protected static final String TXT_CURSED = "";//"-";
	public static final Comparator<Item> itemComparator = new Comparator<Item>() {
		@Override
		public int compare( Item lhs, Item rhs ) {
			return Generator.Category.order( lhs ) - Generator.Category.order( rhs );
		}
	};
	
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = new ArrayList<>();
		actions.add( AC_DROP );
		actions.add( AC_THROW );
		return actions;
	}

	public String actionName(String action, Hero hero){
		return Messages.get(this, "ac_" + action);
	}

	public final boolean doPickUp( Hero hero ) {
		return doPickUp( hero, hero.pos );
	}

	public boolean doPickUp(Hero hero, int pos) {
		if (collect( hero.belongings.backpack )) {
			
			GameScene.pickUp( this, pos );
			Sample.INSTANCE.play( Assets.Sounds.ITEM );
			hero.spendAndNext( TIME_TO_PICK_UP );
			return true;
			
		} else {
			return false;
		}
	}
	
	public void doDrop( Hero hero ) {
		hero.spendAndNext(TIME_TO_DROP);
		int pos = hero.pos;
		Dungeon.level.drop(detachAll(hero.belongings.backpack), pos).sprite.drop(pos);
	}

	//resets an item's properties, to ensure consistency between runs
	public void reset(){
		keptThoughLostInvent = false;
	}

	public boolean keptThroughLostInventory(){
		return keptThoughLostInvent;
	}

	public void doThrow( Hero hero ) {
		GameScene.selectCell(hero, thrower);
	}
	
	public void execute( Hero hero, String action ) {

		GameScene.cancel(hero);
		curUser = hero;
		curItem = this;
		
		if (action.equals( AC_DROP )) {
			
			if (hero.belongings.backpack.contains(this) || isEquipped(hero)) {
				doDrop(hero);
			}
			
		} else if (action.equals( AC_THROW )) {
			
			if (hero.belongings.backpack.contains(this) || isEquipped(hero)) {
				doThrow(hero);
			}
			
		}
	}

	//can be overridden if default action is variable
	//TODO: might want to change this
	public String defaultAction(){
		return defaultAction;
	}
	public String defaultAction(Hero hero){
		return defaultAction;
	}

	public void execute( Hero hero ) {
		String action = defaultAction();
		if (action != null) {
			execute(hero, defaultAction());
		}
	}
	
	protected void onThrow( int cell ) {
		Heap heap = Dungeon.level.drop( this, cell );
		if (!heap.isEmpty()) {
			heap.sprite.drop( cell );
		}
	}
	protected void onThrow( int cell, Hero hero ) {
		onThrow(cell);
	}

	//takes two items and merges them (if possible)
	public Item merge( Item other ){
		if (isSimilar( other )){
			quantity(quantity() + other.quantity());
			other.quantity(0);
		}
		return this;
	}
	public boolean collect( Bag container) {
		return collect(container, new ArrayList<Integer>(2)) != null;
	}
	public List<Integer> collect(Bag container, List<Integer> path) {

		if (quantity() <= 0){
			return null;
		}

		ArrayList<Item> items = container.items;

		{
			int index = items.indexOf(this);
			if (index >= 0) {
				path.add(index);
				return path;
			}
		}
		for (Item item:items) {
			if (item instanceof Bag && ((Bag)item).canHold( this )) {
				List<Integer> newPath = new ArrayList(path);
				newPath.add(items.indexOf(item));
				newPath = collect( (Bag)item, newPath );
				if (newPath != null) {
					return newPath;
				}
			}
		}


		if (!container.canHold(this)){
			return null;
		}
		Hero hero = (container.owner instanceof Hero)? (Hero)container.owner : null;
		if (stackable) {
			for (Item item:items) {
				if (isSimilar( item )) {
					item.merge( this );
					path.add(items.indexOf(item));
					sendUpdateItemCount(container.owner, item, item.quantity(), path);
					item.updateQuickslot();
					if (hero != null && hero.isAlive()) {
						Badges.validateItemLevelAquired( this );
						Talent.onItemCollected(hero, item);
						if (isIdentified()) {
							Catalog.setSeen(getClass());
							Statistics.itemTypesDiscovered.add(getClass());
						}
					}
					if (TippedDart.lostDarts > 0){
						Dart d = new Dart();
						d.quantity(TippedDart.lostDarts);
						TippedDart.lostDarts = 0;
						if (!d.collect(hero)){
							//have to handle this in an actor as we can't manipulate the heap during pickup
							Actor.add(new Actor() {
								{ actPriority = VFX_PRIO; }
								@Override
								protected boolean act() {
									Dungeon.level.drop(d, hero.pos).sprite.drop();
									Actor.remove(this);
									return true;
								}
							});
						}
					}
					return path;
				}
			}
		}

		if (hero != null && hero.isAlive()) {
			Badges.validateItemLevelAquired( this );
			Talent.onItemCollected( hero, this );
			if (isIdentified()){
				Catalog.setSeen(getClass());
				Statistics.itemTypesDiscovered.add(getClass());
			}
		}

		items.add( this );
		Dungeon.quickslot.replacePlaceholder(this);
		Collections.sort( items, itemComparator );
		path.add(items.indexOf(this));
		sendNewInventoryItem(container.owner, this, path);
		updateQuickslot();
		return path;

	}
	
	public final boolean collect(Hero hero) {
		return collect( hero.belongings.backpack );
	}
	
	//returns a new item if the split was sucessful and there are now 2 items, otherwise null
	public Item split( int amount ){
		if (amount <= 0 || amount >= quantity()) {
			return null;
		} else {
			//pssh, who needs copy constructors?
			Item split = Reflection.newInstance(getClass());
			
			if (split == null){
				return null;
			}
			
			Bundle copy = new Bundle();
			this.storeInBundle(copy);
			split.restoreFromBundle(copy);
			split.quantity(amount);
			quantity(quantity() - amount);
			
			return split;
		}
	}

	public Item duplicate(){
		Item dupe = Reflection.newInstance(getClass());
		if (dupe == null){
			return null;
		}
		Bundle copy = new Bundle();
		this.storeInBundle(copy);
		dupe.restoreFromBundle(copy);
		return dupe;
	}
	
	public final Item detach( Bag container ) {
		
		if (quantity() <= 0) {
			
			return null;
			
		} else
		if (quantity() == 1) {

			if (stackable){
				Dungeon.quickslot.convertToPlaceholder(this);
			}

			return detachAll( container );
			
		} else {
			
			
			Item detached = split(1);
			updateQuickslot();
			if (detached != null) detached.onDetach( );
			return detached;
			
		}
	}
	
	public final Item detachAll( Bag container ) {
		Dungeon.quickslot.clearItem( this );
		Hero owner = null;
		if (container.owner instanceof Hero) {
			owner = (Hero) container.owner;
		}
		for (Item item : container.items) {
			if (item == this) {
				if (owner != null) {
					sendRemoveItemFromInventory(owner, getSlot(owner));
				}
				container.items.remove(this);
				item.onDetach();
				container.grabItems(); //try to put more items into the bag as it now has free space
				updateQuickslot();
				return this;
			} else if (item instanceof Bag) {
				Bag bag = (Bag)item;
				if (bag.contains( this )) {
					return detachAll( bag );
				}
			}
		}

		updateQuickslot();
		return this;
	}
	
	public boolean isSimilar( Item item ) {
		return getClass() == item.getClass();
	}

	protected void onDetach(){}
	protected void onDetach(Hero hero){
		onDetach();
	}

	//returns the true level of the item, ignoring all modifiers aside from upgrades
	public final int trueLevel(){
		return level;
	}

	//returns the persistant level of the item, only affected by modifiers which are persistent (e.g. curse infusion)
	public int level(){
		return level;
	}
	public int level(Hero hero){
		return level();
	}

	//returns the level of the item, after it may have been modified by temporary boosts/reductions
	//note that not all item properties should care about buffs/debuffs! (e.g. str requirement)
	public int buffedLvl(Char owner) {
		//only the hero can be affected by Degradation
		if (owner instanceof Hero) {
			if (owner.buff(Degrade.class) != null
					&& (isEquipped((Hero) owner) || ((Hero) owner).belongings.contains(this))) {
				return Degrade.reduceLevel(level());
			} else {
				return level();
			}
		}
		return level();
	}
	//TODO: might want to remove this?
	public int buffedLvl(){
		return level();
	}

	public void level( int value ){
		level = value;

		updateQuickslot();
	}
	@Deprecated
	public Item upgrade(){
		this.level++;
		updateQuickslot();
		return this;
	}
	public Item upgrade(Hero hero){
		return upgrade();
	}
	@Deprecated
	public Item upgrade(int n){
		for (int i=0; i < n; i++) {
			upgrade();
		}
		return this;
	}
	public Item upgrade(int n, Hero hero){
		return upgrade(n);
	}

	public Item degrade() {
		
		this.level--;
		
		return this;
	}
	
	final public Item degrade( int n ) {
		for (int i=0; i < n; i++) {
			degrade();
		}
		
		return this;
	}
	
	public int visiblyUpgraded() {
		return levelKnown ? level() : 0;
	}

	public int buffedVisiblyUpgraded(Hero hero) {
		return levelKnown ? buffedLvl(hero) : 0;
	}
	
	public boolean visiblyCursed() {
		return cursed && cursedKnown;
	}
	
	public boolean isUpgradable() {
		return true;
	}
	
	public boolean isIdentified() {
		return levelKnown && cursedKnown;
	}
	
	public boolean isEquipped( Hero hero ) {
		return false;
	}

	public final Item identify(Hero hero){
		if (hero != null) {
			return identify(true, hero);
		}
		return identify(false, null);
	}

	@Contract("true,null->fail")
	public Item identify( boolean byHero, @Nullable Hero hero ) {

		if (byHero && hero != null && hero.isAlive()){
			Catalog.setSeen(getClass());
            //todo: might remove onItemIdentified
			if (!isIdentified()) Talent.onItemIdentified(hero, this);
			Statistics.itemTypesDiscovered.add(getClass());
		}

		levelKnown = true;
		cursedKnown = true;
		Item.updateQuickslot();
		
		return this;
	}
	
	public void onHeroGainExp( float levelPercent, Hero hero ){
		//do nothing by default
	}
	
	public static void evoke( Hero hero ) {
		hero.getSprite().emitter().burst( Speck.factory( Speck.EVOKE ), 5 );
	}

	public String title() {

		String name = name();

		if (visiblyUpgraded() != 0)
			name = Messages.format( TXT_TO_STRING_LVL, name, visiblyUpgraded()  );

		if (quantity() > 1)
			name = Messages.format( TXT_TO_STRING_X, name, quantity());

		return name;

	}
	
	public String name() {
		return trueName();
	}
	
	public final String trueName() {
		return Messages.get(this, "name");
	}
	
	public int image() {
		return image;
	}
	
	public ItemSprite.Glowing glowing() {
		return null;
	}

	public Emitter emitter() { return null; }
	
	public String info(Hero hero) {
		return desc(hero);
	}
	public String info(){

		if (true) {
			Notes.CustomRecord note;
			if (this instanceof EquipableItem) {
				note = Notes.findCustomRecord(((EquipableItem) this).customNoteID);
			} else {
				note = Notes.findCustomRecord(getClass());
			}
			if (note != null){
				//we swap underscore(0x5F) with low macron(0x2CD) here to avoid highlighting in the item window
				return Messages.get(this, "custom_note", note.title().replace('_', 'ˍ')) + "\n\n" + desc();
			}
		}
		return desc();
	}
	
	public String desc(Hero hero) {
		return desc();
	}
	protected String desc(){
		return Messages.get(this, "desc");
	}
	
	public int quantity() {
		return quantity;
	}

	public Item quantity(int quantity) {
		return quantity(quantity, true);
	}
	public Item quantity(int quantity,  boolean send) {
		this.quantity = quantity;
		if (send){
			sendUpdateItemFull(this);
		}
		return this;
	}

	//item's value in gold coins
	public int value() {
		return 0;
	}

	//item's value in energy crystals
	public int energyVal() {
		return 0;
	}
	
	public Item virtual(){
		Item item = Reflection.newInstance(getClass());
		if (item == null) return null;
		
		item.quantity(0);
		item.level = level;
		return item;
	}
	
	public Item random() {
		return this;
	}
	
	public String status() {
		return quantity() != 1 ? Integer.toString(quantity()) : null;
	}
	public String status(Hero hero){
		return status();
	};

	public static void updateQuickslot() {
		GameScene.updateItemDisplays = true;
	}
	
	private static final String QUANTITY		= "quantity";
	private static final String LEVEL			= "level";
	private static final String LEVEL_KNOWN		= "levelKnown";
	private static final String CURSED			= "cursed";
	private static final String CURSED_KNOWN	= "cursedKnown";
	private static final String QUICKSLOT		= "quickslotpos";
	private static final String KEPT_LOST       = "kept_lost";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		bundle.put( QUANTITY, quantity());
		bundle.put( LEVEL, level );
		bundle.put( LEVEL_KNOWN, levelKnown );
		bundle.put( CURSED, cursed );
		bundle.put( CURSED_KNOWN, cursedKnown );
		if (Dungeon.quickslot.contains(this)) {
			bundle.put( QUICKSLOT, Dungeon.quickslot.getSlot(this) );
		}
		bundle.put( KEPT_LOST, keptThoughLostInvent );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		quantity(bundle.getInt( QUANTITY ));
		levelKnown	= bundle.getBoolean( LEVEL_KNOWN );
		cursedKnown	= bundle.getBoolean( CURSED_KNOWN );
		
		int level = bundle.getInt( LEVEL );
		if (level > 0) {
			upgrade( level );
		} else if (level < 0) {
			degrade( -level );
		}
		
		cursed	= bundle.getBoolean( CURSED );

		//only want to populate slot on first load.
		if (Dungeon.heroes == null) {
			if (bundle.contains(QUICKSLOT)) {
				Dungeon.quickslot.setSlot(bundle.getInt(QUICKSLOT), this);
			}
		}

		keptThoughLostInvent = bundle.getBoolean( KEPT_LOST );
	}

	public int targetingPos( Hero user, int dst ){
		return throwPos( user, dst );
	}

	public int throwPos( Hero user, int dst){
		return new Ballistica( user.pos, dst, Ballistica.PROJECTILE ).collisionPos;
	}

	public void throwSound(){
		Sample.INSTANCE.play(Assets.Sounds.MISS, 0.6f, 0.6f, 1.5f);
	}
	public void throwSound(Hero hero){
		Sample.INSTANCE.play(Assets.Sounds.MISS, 0.6f, 0.6f, 1.5f);
	}

	public void cast( final Hero user, final int dst ) {
		
		final int cell = throwPos( user, dst );
		user.getSprite().zap( cell );
		user.busy();

		throwSound();

		Char enemy = Actor.findChar( cell );
		QuickSlotButton.target(enemy);
		
		final float delay = castDelay(user, dst);

		if (enemy != null) {
			((MissileSprite) user.getSprite().parent.recycle(MissileSprite.class)).
					reset(user.getSprite(),
							enemy.getSprite(),
							this,
							new Callback() {
						@Override
						public void call() {
							curUser = user;
							Item i = Item.this.detach(user.belongings.backpack);
							if (i != null) i.onThrow(cell, user);
							if (curUser.hasTalent(Talent.IMPROVISED_PROJECTILES)
									&& !(Item.this instanceof MissileWeapon)
									&& curUser.buff(Talent.ImprovisedProjectileCooldown.class) == null){
								if (enemy != null && enemy.alignment != curUser.alignment){
									Sample.INSTANCE.play(Assets.Sounds.HIT);
									Buff.affect(enemy, Blindness.class, 1f + curUser.pointsInTalent(Talent.IMPROVISED_PROJECTILES));
									Buff.affect(curUser, Talent.ImprovisedProjectileCooldown.class, 50f);
								}
							}
							if (user.buff(Talent.LethalMomentumTracker.class) != null){
								user.buff(Talent.LethalMomentumTracker.class).detach();
								user.next();
							} else {
								user.spendAndNext(delay);
							}
						}
					});
		} else {
			((MissileSprite) user.getSprite().parent.recycle(MissileSprite.class)).
					reset(user.getSprite(),
							cell,
							this,
							new Callback() {
						@Override
						public void call() {
							curUser = user;
							Item i = Item.this.detach(user.belongings.backpack);
							user.spend(delay);
							if (i != null) i.onThrow(cell, user);
							user.next();
						}
					});
		}
	}
	
	public float castDelay( Char user, int dst ){
		return TIME_TO_THROW;
	}
	
	protected static Hero curUser = null;
	protected static Item curItem = null;
	public void setCurrent( Hero hero ){
		curUser = hero;
		curItem = this;
	}

	protected static CellSelector.Listener thrower = new CellSelector.Listener() {
		@Override
		public void onSelect( Integer target ) {
			if (target != null) {
				curItem.cast( curUser, target );
			}
		}
		@Override
		public String prompt() {
			return Messages.get(Item.class, "prompt");
		}
	};
	public static JSONObject packItem(@NotNull Item item, @Nullable Hero hero) {
		return item.toJsonObject(hero);
	}

	public List<Integer> getSlot(Hero owner) {
		return owner.belongings.pathOfItem(this);
	}

	public final JSONObject toJsonObject(@Nullable Hero hero) {
		JSONObject itemObj = new JSONObject();
		try {
			if (hero != null) {
				itemObj.put("actions", NetworkPacket.packActions(this, hero));
				itemObj.put("default_action", defaultAction == null ? "null" : defaultAction);
				itemObj.put("info", info(hero));
				itemObj.put("ui", itemUI(hero));
			}
			itemObj.put("sprite_sheet", spriteSheet());
			itemObj.put("image", image());
			itemObj.put("icon", icon);
			itemObj.put("name", name());
			itemObj.put("stackable", stackable);
			itemObj.put("quantity", quantity());
			itemObj.put("known", isIdentified());
			itemObj.put("cursed", visiblyCursed());
			itemObj.put("identified", isIdentified());
			itemObj.put("level_known", levelKnown);
			itemObj.put("level", visiblyUpgraded());
			ItemSprite.Glowing glowing = glowing();
			if (glowing != null) {
				itemObj.put("glowing", glowing.toJsonObject());
			}
			else{
				itemObj.put("glowing", JSONObject.NULL);
			}
			if (this instanceof Bag) {
				itemObj = NetworkPacket.packBag((Bag) this, hero, itemObj);
			}
		} catch (JSONException e) {
			Log.e("Packet", "JSONException inside packItem. " + e.toString());
		}
		return itemObj;
	}

	@NotNull
	protected JSONObject itemUI(@NotNull Hero owner) throws JSONException {
		Objects.requireNonNull(owner);
		@NotNull Item item = this;
		JSONObject ui = new JSONObject();
		JSONObject topLeft = new JSONObject();
		JSONObject topRight = new JSONObject();
		JSONObject bottomRight = new JSONObject();

		topLeft.put("visible", true);
		topRight.put("visible", true);
		bottomRight.put("visible", true);

		topLeft.put("text", item.status());

		boolean isArmor = item instanceof Armor;
		boolean isWeapon = item instanceof Weapon;
		boolean isWand = item instanceof Wand;
		if (isArmor || isWeapon) {
			if (item.levelKnown || (isWeapon && !(item instanceof MeleeWeapon))) {
				int str = isArmor ? ((Armor) item).STRReq() : ((Weapon) item).STRReq();
				boolean masteryBuff = isArmor ? ((Armor) item).masteryPotionBonus : ((Weapon) item).masteryPotionBonus;
				topRight.put("text", Utils.format(TXT_STRENGTH, str));
				if(masteryBuff) {
					topRight.put("color", MASTERED);
				} else if (str > owner.STR()) {
					topRight.put("color", DEGRADED);
				} else {
					topRight.put("color", JSONObject.NULL);
				}
			} else {
				topRight.put("text", Utils.format(TXT_TYPICAL_STR, isArmor ?
						((Armor) item).STRReq(0) :
						((MeleeWeapon) item).STRReq(0)));
				topRight.put("color", WARNING);
			}
		} else {
			topRight.put("text", JSONObject.NULL);
		}

		int level = item.visiblyUpgraded();
		if (level != 0 || (item.cursed && item.cursedKnown)) {
			bottomRight.put("text", item.levelKnown ? Utils.format(TXT_LEVEL, level) : TXT_CURSED);
			boolean curseInfusionBonus = false;
			if (isWeapon){
				curseInfusionBonus = ((Weapon)item).curseInfusionBonus;
			}
			if (isArmor){
				curseInfusionBonus = ((Armor)item).curseInfusionBonus;
			}
			if (isWand){
				curseInfusionBonus = ((Wand)item).curseInfusionBonus;
			}



			bottomRight.put("color", level > 0 ? (UPGRADED) : DEGRADED);
			if (curseInfusionBonus){
				bottomRight.put("color", CURSE_INFUSED);
			}
		} else {
			bottomRight.put("text", JSONObject.NULL);
		}
		ui.put("top_left", topLeft);
		ui.put("top_right", topRight);
		ui.put("bottom_right", bottomRight);
		return ui;
	}
	public String spriteSheet() {
		return spriteSheet;
	}

	public void spriteSheet(String newSpriteSheet) {
		spriteSheet = newSpriteSheet;
		sendUpdateItemFull(this);
	}
	public String getNameKey(){
		return getClass().getName() + "name";
	}
	//Only use when owner can't be found in any other way
	public final Hero findOwner() {
		for (Hero hero : Dungeon.heroes) {
			if (hero != null && hero.belongings.contains(this)) {
				return hero;
		}
	}
		return null;
	}

	public void sendSelfUpdate(){
		sendSelfUpdate(null);
	}
	public void sendSelfUpdate(Hero heroToFlush){
		sendUpdateItemFull(this);
		if (heroToFlush != null){
			SendData.flush(heroToFlush);
		}
	}
}
