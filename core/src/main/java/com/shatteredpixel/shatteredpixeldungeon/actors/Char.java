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

package com.shatteredpixel.shatteredpixeldungeon.actors;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.StormCloud;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Adrenaline;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArcaneArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barkskin;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Berserk;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bless;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corrosion;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Daze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Doom;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dread;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FireImbue;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FrostImbue;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Fury;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hex;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invulnerability;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LifeLink;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LostInventory;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Momentum;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MonkEnergy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Preparation;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShieldBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Sleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SnipersMark;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Speed;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Stamina;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.PowerOfMany;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.duelist.Challenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.DeathMark;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.Endure;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.AuraOfProtection;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.BeamingRay;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.GuidingLight;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.LifeLinkSpell;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.ShieldOfLight;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Brute;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.CrystalSpire;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DwarfKing;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Elemental;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollGeomancer;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Necromancer;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Tengu;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.YogDzewa;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.MirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.PrismaticImage;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.curses.Bulk;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.AntiMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Brimstone;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Flow;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Obfuscation;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Potential;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Swiftness;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Viscosity;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfCleansing;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Pickaxe;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfElements;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRetribution;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfChallenge;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfPsionicBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAggression;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFireblast;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLightning;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLivingEarth;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blazing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Kinetic;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Shocking;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sickle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.ShockingDart;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GeyserTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GnollRockfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrimTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.network.SendData;
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.TargetHealthIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.nikita22007.multiplayer.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;

public abstract class Char extends Actor {
	
	public int pos = 0;
	
	private CharSprite sprite;
	
	private int HT;
	private int HP;
	
	protected float baseSpeed	= 1;
	protected PathFinder.Path path;

	public int paralysed	    = 0;
	public boolean rooted		= false;
	public boolean flying		= false;
	public int invisible		= 0;

	public CharSprite getSprite() {
		return sprite;
	}

	public void setSprite(CharSprite sprite) {
		this.sprite = sprite;
		sendSelf();
	}

	public boolean needsShieldUpdate() {
		return needsShieldUpdate;
	}

	public void setNeedsShieldUpdate(boolean needsShieldUpdate) {
		this.needsShieldUpdate = needsShieldUpdate;
		if (needsShieldUpdate) {
			SendData.sendCharShield(id(), shielding());
		}
	}

	public int getHT() {
		return HT;
	}

	public int setHT(int HT) {
		if (this.HT != HT){
			this.HT = HT;
			sendSelf();
		}
		return HT;
	}

	public int getHP() {
		return HP;
	}

	public int setHP(int HP) {
		if (this.HP != HP) {
			this.HP = HP;
			sendSelf();
		}
		return HP;
	}


	//these are relative to the hero
	public enum Alignment{
		ENEMY,
		NEUTRAL,
		ALLY
	}
	public Alignment alignment;

	public int viewDistance	= 8;

	public boolean[] fieldOfView = null;
	public void sendSelf(){
		if ( !all().contains(this) ){
			return;
		}
		SendData.sendActor(this);
	}

	private LinkedHashSet<Buff> buffs = new LinkedHashSet<>();
	public JSONObject getEmoJsonObject() {
		if (getSprite() == null){
			return new JSONObject();
		}
		return getSprite().getEmoJsonObject();
	}

	@Override
	protected boolean act() {
		if (fieldOfView == null || fieldOfView.length != Dungeon.level.length()){
			fieldOfView = new boolean[Dungeon.level.length()];
		}
		Dungeon.level.updateFieldOfView( this, fieldOfView );

		//throw any items that are on top of an immovable char
		if (properties().contains(Property.IMMOVABLE)){
			throwItems();
		}
		return false;
	}

	protected void throwItems(){
		Heap heap = Dungeon.level.heaps.get( pos );
		if (heap != null && heap.type == Heap.Type.HEAP
				&& !(heap.peek() instanceof Tengu.BombAbility.BombItem)
				&& !(heap.peek() instanceof Tengu.ShockerAbility.ShockerItem)) {
			ArrayList<Integer> candidates = new ArrayList<>();
			for (int n : PathFinder.NEIGHBOURS8){
				if (Dungeon.level.passable[pos+n]){
					candidates.add(pos+n);
				}
			}
			if (!candidates.isEmpty()){
				Dungeon.level.drop( heap.pickUp(), Random.element(candidates) ).sprite.drop( pos );
			}
		}
	}

	public String name(){
		return Messages.get(this, "name");
	}

	public boolean canInteract(Char c){
		if (Dungeon.level.adjacent( pos, c.pos )){
			return true;
		} else if (c instanceof Hero
				&& alignment == Alignment.ALLY
				&& !hasProp(this, Property.IMMOVABLE)
				&& Dungeon.level.distance(pos, c.pos) <= 2* ((Hero) c).pointsInTalent(Talent.ALLY_WARP)){
			return true;
		} else {
			return false;
		}
	}

	//swaps places by default
	public boolean interact(Char c){

		//don't allow char to swap onto hazard unless they're flying
		//you can swap onto a hazard though, as you're not the one instigating the swap
		if (!Dungeon.level.passable[pos] && !c.flying){
			return true;
		}

		//can't swap into a space without room
		if (properties().contains(Property.LARGE) && !Dungeon.level.openSpace[c.pos]
			|| c.properties().contains(Property.LARGE) && !Dungeon.level.openSpace[pos]){
			return true;
		}

		//we do a little raw position shuffling here so that the characters are never
		// on the same cell when logic such as occupyCell() is triggered
		int oldPos = pos;
		int newPos = c.pos;

		//can't swap or ally warp if either char is immovable
		if (hasProp(this, Property.IMMOVABLE) || hasProp(c, Property.IMMOVABLE)){
			return true;
		}

		//warp instantly with allies in this case
		if (c instanceof Hero && ((Hero) c).hasTalent(Talent.ALLY_WARP)){
			PathFinder.buildDistanceMap(c.pos, BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null));
			if (PathFinder.distance[pos] == Integer.MAX_VALUE){
				return true;
			}
			pos = newPos;
			c.pos = oldPos;
			ScrollOfTeleportation.appear(this, newPos);
			ScrollOfTeleportation.appear(c, oldPos);
			Dungeon.observe((Hero) c);
			GameScene.updateFog();
			return true;
		}

		//can't swap places if one char has restricted movement
		if (paralysed > 0 || c.paralysed > 0 || rooted || c.rooted
				|| buff(Vertigo.class) != null || c.buff(Vertigo.class) != null){
			return true;
		}

		c.pos = oldPos;
		moveSprite( oldPos, newPos );
		move( newPos );

		c.pos = newPos;
		c.getSprite().move( newPos, oldPos );
		c.move( oldPos );

		c.spend( 1 / c.speed() );

		if (c instanceof Hero){
			if (((Hero) c).subClass == HeroSubClass.FREERUNNER){
				Buff.affect(c, Momentum.class).gainStack();
			}

			((Hero) c).busy();
		}

		return true;
	}

	protected boolean moveSprite( int from, int to ) {

		if (getSprite().isVisible() && getSprite().parent != null && (Dungeon.visibleforAnyHero(from) || Dungeon.visibleforAnyHero(to))) {
			getSprite().move( from, to );
			return true;
		} else {
			getSprite().turnTo(from, to);
			getSprite().place( to );
			return true;
		}
	}

	public void hitSound( float pitch ){
		Sample.INSTANCE.play(Assets.Sounds.HIT, 1, pitch);
	}

	public boolean blockSound( float pitch ) {
		return false;
	}

	protected static final String POS       = "pos";
	protected static final String TAG_HP    = "HP";
	protected static final String TAG_HT    = "HT";
	protected static final String TAG_SHLD  = "SHLD";
	protected static final String BUFFS	    = "buffs";

	@Override
	public void storeInBundle( Bundle bundle ) {

		super.storeInBundle( bundle );

		bundle.put( POS, pos );
		bundle.put( TAG_HP, getHP());
		bundle.put( TAG_HT, getHT());
		bundle.put( BUFFS, buffs );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {

		super.restoreFromBundle( bundle );

		pos = bundle.getInt( POS );
		setHP(bundle.getInt( TAG_HP ));
		setHT(bundle.getInt( TAG_HT ));

		for (Bundlable b : bundle.getCollection( BUFFS )) {
			if (b != null) {
				((Buff)b).attachTo( this );
			}
		}
	}

	final public boolean attack( Char enemy ){
		return attack(enemy, 1f, 0f, 1f);
	}

	public boolean attack( Char enemy, float dmgMulti, float dmgBonus, float accMulti ) {

		if (enemy == null) return false;
		boolean visibleFight = false;
		if (enemy instanceof Hero) {
			visibleFight = ((Hero) enemy).fieldOfView[pos] || ((Hero) enemy).fieldOfView[enemy.pos];
		}
		if (enemy.isInvulnerable(getClass())) {

			if (visibleFight) {
				enemy.getSprite().showStatus( CharSprite.POSITIVE, Messages.get(this, "invulnerable") );

				Sample.INSTANCE.play(Assets.Sounds.HIT_PARRY, 1f, Random.Float(0.96f, 1.05f));
			}

			return false;

		} else if (hit( this, enemy, accMulti, false )) {

			int dr = Math.round(enemy.drRoll() * AscensionChallenge.statModifier(enemy));

			if (this instanceof Hero){
				Hero h = (Hero)this;
				if (h.belongings.attackingWeapon() instanceof MissileWeapon
						&& h.subClass == HeroSubClass.SNIPER
						&& !Dungeon.level.adjacent(h.pos, enemy.pos)){
					dr = 0;
				}

				if (h.buff(MonkEnergy.MonkAbility.UnarmedAbilityTracker.class) != null){
					dr = 0;
				}
			}

			//we use a float here briefly so that we don't have to constantly round while
			// potentially applying various multiplier effects
			float dmg;
			Preparation prep = buff(Preparation.class);
			if (prep != null){
				dmg = prep.damageRoll(this);
				if (this instanceof Hero && ((Hero) this).hasTalent(Talent.BOUNTY_HUNTER)) {
					Buff.affect(this, Talent.BountyHunterTracker.class, 0.0f);
				}
			} else {
				dmg = damageRoll();
			}

			dmg = dmg*dmgMulti;

			//flat damage bonus is affected by multipliers
			dmg += dmgBonus;

			if (enemy.buff(GuidingLight.Illuminated.class) != null){
				enemy.buff(GuidingLight.Illuminated.class).detach();
				if (this instanceof Hero && ((Hero) this).hasTalent(Talent.SEARING_LIGHT)){
					dmg += 2 + 2* ((Hero) this).pointsInTalent(Talent.SEARING_LIGHT);
				}
				if (!(this instanceof Hero) && enemy.buff(GuidingLight.Illuminated.class).source.subClass == HeroSubClass.PRIEST){
					Hero cause = enemy.buff(GuidingLight.Illuminated.class).source;
					enemy.damage(cause.lvl, new DamageCause(GuidingLight.INSTANCE, cause));
				}
			}

			Berserk berserk = buff(Berserk.class);
			if (berserk != null) dmg = berserk.damageFactor(dmg);

			if (buff( Fury.class ) != null) {
				dmg *= 1.5f;
			}

			if (buff( PowerOfMany.PowerBuff.class) != null){
				if (buff( BeamingRay.BeamingRayBoost.class) != null
					&& buff( BeamingRay.BeamingRayBoost.class).object == enemy.id()){
					dmg *= 1.3f + 0.05f*buff(BeamingRay.BeamingRayBoost.class).source.pointsInTalent(Talent.BEAMING_RAY);
				} else {
					dmg *= 1.25f;
				}
			}

			for (ChampionEnemy buff : buffs(ChampionEnemy.class)){
				dmg *= buff.meleeDamageFactor();
			}

			dmg *= AscensionChallenge.statModifier(this);

			//friendly endure
			Endure.EndureTracker endure = buff(Endure.EndureTracker.class);
			if (endure != null) dmg = endure.damageFactor(dmg);

			//enemy endure
			endure = enemy.buff(Endure.EndureTracker.class);
			if (endure != null){
				dmg = endure.adjustDamageTaken(dmg);
			}

			if (enemy.buff(ScrollOfChallenge.ChallengeArena.class) != null){
				dmg *= 0.67f;
			}
			for (Hero hero: Dungeon.heroes) {
				if (hero != null) {
					if (hero.alignment == enemy.alignment
							&& hero.buff(AuraOfProtection.AuraBuff.class) != null
							&& (Dungeon.level.distance(enemy.pos, hero.pos) <= 2 || enemy.buff(LifeLinkSpell.LifeLinkSpellBuff.class) != null)) {
						dmg *= 0.925f - 0.075f * hero.pointsInTalent(Talent.AURA_OF_PROTECTION);
					}
				}
			}
			if (enemy.buff(MonkEnergy.MonkAbility.Meditate.MeditateResistance.class) != null){
				dmg *= 0.2f;
			}

			if ( buff(Weakness.class) != null ){
				dmg *= 0.67f;
			}

			//characters influenced by aggression deal 1/2 damage to bosses
			if ( enemy.buff(StoneOfAggression.Aggression.class) != null
					&& enemy.alignment == alignment
					&& (Char.hasProp(enemy, Property.BOSS) || Char.hasProp(enemy, Property.MINIBOSS))){
				dmg *= 0.5f;
				//yog-dzewa specifically takes 1/4 damage
				if (enemy instanceof YogDzewa){
					dmg *= 0.5f;
				}
			}
			int effectiveDamage = enemy.defenseProc( this, Math.round(dmg) );
			//do not trigger on-hit logic if defenseProc returned a negative value
			if (effectiveDamage >= 0) {
				effectiveDamage = Math.max(effectiveDamage - dr, 0);

				if (enemy.buff(Viscosity.ViscosityTracker.class) != null) {
					effectiveDamage = enemy.buff(Viscosity.ViscosityTracker.class).deferDamage(effectiveDamage);
					enemy.buff(Viscosity.ViscosityTracker.class).detach();
				}

				//vulnerable specifically applies after armor reductions
				if (enemy.buff(Vulnerable.class) != null) {
					effectiveDamage *= 1.33f;
				}

				effectiveDamage = attackProc(enemy, effectiveDamage);
			}
			if (visibleFight) {
				if (effectiveDamage > 0 || !enemy.blockSound(Random.Float(0.96f, 1.05f))) {
					hitSound(Random.Float(0.87f, 1.15f));
				}
			}

			// If the enemy is already dead, interrupt the attack.
			// This matters as defence procs can sometimes inflict self-damage, such as armor glyphs.
			if (!enemy.isAlive()){
				return true;
			}

			enemy.damage( effectiveDamage,  this );

			if (buff(FireImbue.class) != null)  buff(FireImbue.class).proc(enemy);
			if (buff(FrostImbue.class) != null) buff(FrostImbue.class).proc(enemy);
			//TODO: check this
			if (enemy.isAlive() && enemy.alignment != alignment && prep != null && prep.canKO(enemy, (Hero) prep.target)){
				enemy.setHP(0);
				if (enemy.buff(Brute.BruteRage.class) != null){
					enemy.buff(Brute.BruteRage.class).detach();
				}
				if (!enemy.isAlive()) {
					enemy.die(new DamageCause(this, this));
				} else {
					//helps with triggering any on-damage effects that need to activate
					enemy.damage(-1, this);
					DeathMark.processFearTheReaper(enemy);
				}
				if (enemy.getSprite() != null) {
					enemy.getSprite().showStatus(CharSprite.NEGATIVE, Messages.get(Preparation.class, "assassinated"));
				}
			}

			Talent.CombinedLethalityAbilityTracker combinedLethality = buff(Talent.CombinedLethalityAbilityTracker.class);
			if (combinedLethality != null && this instanceof Hero && ((Hero) this).belongings.attackingWeapon() instanceof MeleeWeapon && combinedLethality.weapon != ((Hero) this).belongings.attackingWeapon()){
				if ( enemy.isAlive() && enemy.alignment != alignment && !Char.hasProp(enemy, Property.BOSS)
						&& !Char.hasProp(enemy, Property.MINIBOSS) &&
						(enemy.getHP() /(float) enemy.getHT()) <= 0.4f*((Hero)this).pointsInTalent(Talent.COMBINED_LETHALITY)/3f) {
					enemy.setHP(0);
					if (enemy.buff(Brute.BruteRage.class) != null){
						enemy.buff(Brute.BruteRage.class).detach();
					}
					if (!enemy.isAlive()) {
						enemy.die(new DamageCause(this));
					} else {
						//helps with triggering any on-damage effects that need to activate
						enemy.damage(-1, this);
						DeathMark.processFearTheReaper(enemy);
					}
					if (enemy.getSprite() != null) {
						enemy.getSprite().showStatus(CharSprite.NEGATIVE, Messages.get(Talent.CombinedLethalityAbilityTracker.class, "executed"));
					}
				}
				combinedLethality.detach();
			}

			if (enemy.getSprite() != null) {
				enemy.getSprite().bloodBurstA(getSprite().center(), effectiveDamage);
				enemy.getSprite().flash();
			}

			if (!enemy.isAlive() && visibleFight) {
				if (enemy instanceof Hero) {

					if (this instanceof Hero) {
						return true;
					}

					if (this instanceof WandOfLivingEarth.EarthGuardian
							|| this instanceof MirrorImage || this instanceof PrismaticImage){
						Badges.validateDeathFromFriendlyMagic();
					}
					Dungeon.fail( this );
					GLog.n( Messages.capitalize(Messages.get(Char.class, "kill", name())) );

				} else if (this instanceof Hero) {
					GLog.i( Messages.capitalize(Messages.get(Char.class, "defeat", enemy.name())) );
				}
			}

			return true;

		} else {

			enemy.getSprite().showStatus( CharSprite.NEUTRAL, enemy.defenseVerb() );
			if (visibleFight) {
				//TODO enemy.defenseSound? currently miss plays for monks/crab even when they parry
				Sample.INSTANCE.play(Assets.Sounds.MISS);
			}

			return false;

		}
	}

	public static int INFINITE_ACCURACY = 1_000_000;
	public static int INFINITE_EVASION = 1_000_000;

	final public static boolean hit( Char attacker, Char defender, boolean magic ) {
		return hit(attacker, defender, magic ? 2f : 1f, magic);
	}

	public static boolean hit( Char attacker, Char defender, float accMulti, boolean magic ) {
		float acuStat = attacker.attackSkill(defender);
		float defStat = defender.defenseSkill(attacker);

		if (defender instanceof Hero && ((Hero) defender).damageInterrupt) {
			((Hero) defender).interrupt();
		}

		//invisible chars always hit (for the hero this is surprise attacking)
		if (attacker.invisible > 0 && attacker.canSurpriseAttack()) {
			acuStat = INFINITE_ACCURACY;
		}

		if (defender.buff(MonkEnergy.MonkAbility.Focus.FocusBuff.class) != null) {
			defStat = INFINITE_EVASION;
		}

		//if accuracy or evasion are large enough, treat them as infinite.
		//note that infinite evasion beats infinite accuracy
		if (defStat >= INFINITE_EVASION) {
			return false;
		} else if (acuStat >= INFINITE_ACCURACY) {
			return true;
		}

		float acuRoll = Random.Float(acuStat);
		if (attacker.buff(Bless.class) != null) acuRoll *= 1.25f;
		if (attacker.buff(Hex.class) != null) acuRoll *= 0.8f;
		if (attacker.buff(Daze.class) != null) acuRoll *= 0.5f;
		for (ChampionEnemy buff : attacker.buffs(ChampionEnemy.class)) {
			acuRoll *= buff.evasionAndAccuracyFactor();
		}
		acuRoll *= AscensionChallenge.statModifier(attacker);
		for (Hero hero : Dungeon.heroes) {
			if (hero != null) {
				if (hero.heroClass != HeroClass.CLERIC
						&& hero.hasTalent(Talent.BLESS)
						&& attacker.alignment == Alignment.ALLY) {
					// + 3%/5%
					acuRoll *= 1.01f + 0.02f * hero.pointsInTalent(Talent.BLESS);
				}
			}
		}

		float defRoll = Random.Float(defStat);
		if (defender.buff(Bless.class) != null) defRoll *= 1.25f;
		if (defender.buff(Hex.class) != null) defRoll *= 0.8f;
		if (defender.buff(Daze.class) != null) defRoll *= 0.5f;
		for (ChampionEnemy buff : defender.buffs(ChampionEnemy.class)) {
			defRoll *= buff.evasionAndAccuracyFactor();
		}
		defRoll *= AscensionChallenge.statModifier(defender);
		for (Hero hero : Dungeon.heroes) {
			if (hero != null) {
			if (hero.heroClass != HeroClass.CLERIC
					&& hero.hasTalent(Talent.BLESS)
					&& defender.alignment == Alignment.ALLY) {
				// + 3%/5%
				defRoll *= 1.01f + 0.02f * hero.pointsInTalent(Talent.BLESS);
			}
		}
	}

		return (acuRoll * accMulti) >= defRoll;
	}

	public int attackSkill( Char target ) {
		return 0;
	}

	public int defenseSkill( Char enemy ) {
		return 0;
	}

	public String defenseVerb() {
		return Messages.get(this, "def_verb");
	}

	public int drRoll() {
		int dr = 0;

		dr += Random.NormalIntRange( 0 , Barkskin.currentLevel(this) );

		return dr;
	}

	public int damageRoll() {
		return 1;
	}

	//TODO it would be nice to have a pre-armor and post-armor proc.
	// atm attack is always post-armor and defence is already pre-armor

	public int attackProc( Char enemy, int damage ) {
		for (ChampionEnemy buff : buffs(ChampionEnemy.class)){
			buff.onAttackProc( enemy );
		}
		return damage;
	}

	public int defenseProc( Char enemy, int damage ) {

		Earthroot.Armor armor = buff( Earthroot.Armor.class );
		if (armor != null) {
			damage = armor.absorb( damage );
		}

		ShieldOfLight.ShieldOfLightTracker shield = buff( ShieldOfLight.ShieldOfLightTracker.class);
		if (shield != null && shield.object == enemy.id()){

			int min = 1 + buff(ShieldOfLight.ShieldOfLightTracker.class).source.pointsInTalent(Talent.SHIELD_OF_LIGHT);
			damage -= Random.NormalIntRange(min, 2*min);
			damage = Math.max(damage, 0);
		} else if (this instanceof Hero
				&& ((Hero) this).heroClass != HeroClass.CLERIC
				&& ((Hero) this).hasTalent(Talent.SHIELD_OF_LIGHT)
				&& TargetHealthIndicator.instance.target() == enemy){
			//33/50%
			if (Random.Int(6) < 1+ ((Hero) this).pointsInTalent(Talent.SHIELD_OF_LIGHT)){
				damage -= 1;
			}
		}

		// hero and pris images skip this as they already benefit from hero's armor glyph proc
		if (!(this instanceof Hero || this instanceof PrismaticImage)) {
			for (Hero hero : Dungeon.heroes) {
				if (hero != null) {
				if (hero.alignment == alignment && hero.belongings.armor() != null
						&& hero.buff(AuraOfProtection.AuraBuff.class) != null
						&& (Dungeon.level.distance(pos, hero.pos) <= 2 || buff(LifeLinkSpell.LifeLinkSpellBuff.class) != null)) {
					damage = hero.belongings.armor().proc(enemy, this, damage);
				}
				}
			}
		}

		return damage;
	}
//Returns the level a glyph is at for a char, or -1 if they are not benefitting from that glyph
	//This function is needed as (unlike enchantments) many glyphs trigger in a variety of cases
	//TODO: check this
	public int glyphLevel(Class<? extends Armor.Glyph> cls){
		for(Hero hero: Dungeon.heroes) {
			if (hero != null && Dungeon.level != null
					&& this != hero && hero.alignment == alignment
					&& hero.buff(AuraOfProtection.AuraBuff.class) != null
					&& (Dungeon.level.distance(pos, hero.pos) <= 2 || buff(LifeLinkSpell.LifeLinkSpellBuff.class) != null)) {
				return hero.glyphLevel(cls);
			}
		}
		return -1;
	}

	public float speed() {
		float speed = baseSpeed;
		if ( buff( Cripple.class ) != null ) speed /= 2f;
		if ( buff( Stamina.class ) != null) speed *= 1.5f;
		if ( buff( Adrenaline.class ) != null) speed *= 2f;
		if ( buff( Haste.class ) != null) speed *= 3f;
		if ( buff( Dread.class ) != null) speed *= 2f;

		speed *= Swiftness.speedBoost(this, glyphLevel(Swiftness.class));
		speed *= Flow.speedBoost(this, glyphLevel(Flow.class));
		speed *= Bulk.speedBoost(this, glyphLevel(Bulk.class));

		return speed;
	}

	//currently only used by invisible chars, or by the hero
	public boolean canSurpriseAttack(){
		return true;
	}

	//used so that buffs(Shieldbuff.class) isn't called every time unnecessarily
	private int cachedShield = 0;
	private boolean needsShieldUpdate = true; //todo add to send

	public int shielding(){
		if (!needsShieldUpdate){
			return cachedShield;
		}

		cachedShield = 0;
		for (ShieldBuff s : buffs(ShieldBuff.class)){
			cachedShield += s.shielding();
		}
		setNeedsShieldUpdate(false);
		return cachedShield;
	}

	public void damage(int dmg, @NotNull Buff buff ) {
		damage(dmg, new DamageCause(buff, null));
	}
	public void damage(int dmg, @NotNull Trap trap ) {
		damage(dmg, new DamageCause(trap, null));
	}
	public void damage(int dmg, @NotNull Char source ) {
		damage(dmg, new DamageCause(source));
	}
	public void damage(int dmg, @NotNull DamageCause source ) {
		Object src = source.getCause();
		if (!isAlive() || dmg < 0) {
			return;
		}

		if(isInvulnerable(src.getClass())){
			getSprite().showStatus(CharSprite.POSITIVE, Messages.get(this, "invulnerable"));
			return;
		}

		if (!(src instanceof LifeLink || src instanceof Hunger) && buff(LifeLink.class) != null){
			HashSet<LifeLink> links = buffs(LifeLink.class);
			for (LifeLink link : links.toArray(new LifeLink[0])){
				if (Actor.findById(link.object) == null){
					links.remove(link);
					link.detach();
				}
			}
			dmg = (int)Math.ceil(dmg / (float)(links.size()+1));
			for (LifeLink link : links){
				Char ch = (Char)Actor.findById(link.object);
				if (ch != null) {
					ch.damage(dmg, new DamageCause(link, source.getDamageOwner()));
					if (!ch.isAlive()) {
						link.detach();
					}
				}
			}
		}

		//temporarily assign to a float to avoid rounding a bunch
		float damage = dmg;

		//if dmg is from a character we already reduced it in defenseProc
		if (!(src instanceof Char)) {
			for (Hero hero: Dungeon.heroes) {
				if (hero != null) {
					if (hero.alignment == alignment
							&& hero.buff(AuraOfProtection.AuraBuff.class) != null
							&& (Dungeon.level.distance(pos, hero.pos) <= 2 || buff(LifeLinkSpell.LifeLinkSpellBuff.class) != null)) {
						damage *= 0.925f - 0.075f * hero.pointsInTalent(Talent.AURA_OF_PROTECTION);
					}
				}
			}
		}

		if (buff(PowerOfMany.PowerBuff.class) != null){
			if (buff(LifeLinkSpell.LifeLinkSpellBuff.class) != null){

				damage *= 0.70f - 0.05f*buff(LifeLinkSpell.LifeLinkSpellBuff.class).source.pointsInTalent(Talent.LIFE_LINK);
			} else {
				damage *= 0.75f;
			}
		}

		Terror t = buff(Terror.class);
		if (t != null){
			t.recover();
		}
		Dread d = buff(Dread.class);
		if (d != null){
			d.recover();
		}
		Charm c = buff(Charm.class);
		if (c != null){
			c.recover(src);
		}
		if (this.buff(Frost.class) != null){
			Buff.detach( this, Frost.class );
		}
		if (this.buff(MagicalSleep.class) != null){
			Buff.detach(this, MagicalSleep.class);
		}
		if (this.buff(Doom.class) != null && !isImmune(Doom.class)){
			damage *= 1.67f;
		}
		if (alignment != Alignment.ALLY && this.buff(DeathMark.DeathMarkTracker.class) != null){
			damage *= 1.25f;
		}

		if (buff(Sickle.HarvestBleedTracker.class) != null){
			buff(Sickle.HarvestBleedTracker.class).detach();

			if (!isImmune(Bleeding.class)){
				Bleeding b = buff(Bleeding.class);
				if (b == null){
					b = new Bleeding();
				}
				b.announced = false;
				b.set(dmg, Sickle.HarvestBleedTracker.class);
				b.setSourceHero(buff(Sickle.HarvestBleedTracker.class).getHero());
				b.attachTo(this);
				getSprite().showStatus(CharSprite.WARNING, Messages.titleCase(b.name()) + " " + (int)b.level());
				return;
			}
		}

		Class<?> srcClass = src.getClass();
		if (isImmune( srcClass )) {
			damage = 0;
		} else {
			damage *= resist( srcClass );
		}

		dmg = Math.round(damage);

		//we ceil these specifically to favor the player vs. champ dmg reduction
		// most important vs. giant champions in the earlygame
		for (ChampionEnemy buff : buffs(ChampionEnemy.class)){
			dmg = (int) Math.ceil(dmg * buff.damageTakenFactor());
		}

		//TODO improve this when I have proper damage source logic
		if (AntiMagic.RESISTS.contains(src.getClass())){
			dmg -= AntiMagic.drRoll(this, glyphLevel(AntiMagic.class));
			if (buff(ArcaneArmor.class) != null) {
				dmg -= Random.NormalIntRange(0, buff(ArcaneArmor.class).level());
			}
			if (dmg < 0) dmg = 0;
		}

		if (buff( Paralysis.class ) != null) {
			buff( Paralysis.class ).processDamage(dmg);
		}

		int shielded = dmg;
		//FIXME: when I add proper damage properties, should add an IGNORES_SHIELDS property to use here.
		if (!(src instanceof Hunger)){
			for (ShieldBuff s : buffs(ShieldBuff.class)){
				dmg = s.absorbDamage(dmg);
				if (dmg == 0) break;
			}
		}
		shielded -= dmg;
		setHP(getHP() - dmg);

		if (getHP() > 0 && shielded > 0 && shielding() == 0){
			if (this instanceof Hero && ((Hero) this).hasTalent(Talent.PROVOKED_ANGER)){
				Buff.affect(this, Talent.ProvokedAngerTracker.class, 5f);
			}
		}

		if (getHP() > 0 && buff(Grim.GrimTracker.class) != null){

			float finalChance = buff(Grim.GrimTracker.class).maxChance;
			finalChance *= (float)Math.pow( ((getHT() - getHP()) / (float) getHT()), 2);

			if (Random.Float() < finalChance) {
				int extraDmg = Math.round(getHP() *resist(Grim.class));
				dmg += extraDmg;
				setHP(getHP() - extraDmg);

				getSprite().emitter().burst( ShadowParticle.UP, 5 );
				if (!isAlive() && buff(Grim.GrimTracker.class).qualifiesForBadge){
					Badges.validateGrimWeapon();
				}
			}
		}

		if (getHP() < 0 && src instanceof Char && alignment == Alignment.ENEMY){
			if (((Char) src).buff(Kinetic.KineticTracker.class) != null){
				int dmgToAdd = -getHP();
				dmgToAdd -= ((Char) src).buff(Kinetic.KineticTracker.class).conservedDamage;
				dmgToAdd = Math.round(dmgToAdd * Weapon.Enchantment.genericProcChanceMultiplier((Char) src));
				if (dmgToAdd > 0) {
					Buff.affect((Char) src, Kinetic.ConservedDamage.class).setBonus(dmgToAdd);
				}
				((Char) src).buff(Kinetic.KineticTracker.class).detach();
			}
		}

		if (getSprite() != null) {
			//defaults to normal damage icon if no other ones apply
			int                                                         icon = FloatingText.PHYS_DMG;
			if (NO_ARMOR_PHYSICAL_SOURCES.contains(src.getClass()))     icon = FloatingText.PHYS_DMG_NO_BLOCK;
			if (AntiMagic.RESISTS.contains(src.getClass()))             icon = FloatingText.MAGIC_DMG;
			if (src instanceof Pickaxe)                                 icon = FloatingText.PICK_DMG;

			//special case for sniper when using ranged attacks
			if (src instanceof Hero
					&& ((Hero) src).subClass == HeroSubClass.SNIPER
					&& !Dungeon.level.adjacent(((Hero) src).pos, pos)
					&& ((Hero) src).belongings.attackingWeapon() instanceof MissileWeapon){
				icon = FloatingText.PHYS_DMG_NO_BLOCK;
			}

			//special case for monk using unarmed abilities
			if (src instanceof Hero
					&& ((Hero)src).buff(MonkEnergy.MonkAbility.UnarmedAbilityTracker.class) != null){
				icon = FloatingText.PHYS_DMG_NO_BLOCK;
			}

			if (src instanceof Hunger)                                  icon = FloatingText.HUNGER;
			if (src instanceof Burning)                                 icon = FloatingText.BURNING;
			if (src instanceof Chill || src instanceof Frost)           icon = FloatingText.FROST;
			if (src instanceof GeyserTrap || src instanceof StormCloud) icon = FloatingText.WATER;
			if (src instanceof Burning)                                 icon = FloatingText.BURNING;
			if (src instanceof Electricity)                             icon = FloatingText.SHOCKING;
			if (src instanceof Bleeding)                                icon = FloatingText.BLEEDING;
			if (src instanceof ToxicGas)                                icon = FloatingText.TOXIC;
			if (src instanceof Corrosion)                               icon = FloatingText.CORROSION;
			if (src instanceof Poison)                                  icon = FloatingText.POISON;
			if (src instanceof Ooze)                                    icon = FloatingText.OOZE;
			if (src instanceof Viscosity.DeferedDamage)                 icon = FloatingText.DEFERRED;
			if (src instanceof Corruption)                              icon = FloatingText.CORRUPTION;
			if (src instanceof AscensionChallenge)                      icon = FloatingText.AMULET;

			getSprite().showStatusWithIcon(CharSprite.NEGATIVE, Integer.toString(dmg + shielded), icon);
		}

		if (getHP() < 0) setHP(0);

		if (!isAlive()) {
			die( source );
		} else if (getHP() == 0 && buff(DeathMark.DeathMarkTracker.class) != null){
			DeathMark.processFearTheReaper(this);
		}
		sendSelf();
	}

	//these are misc. sources of physical damage which do not apply armor, they get a different icon
	private static HashSet<Class> NO_ARMOR_PHYSICAL_SOURCES = new HashSet<>();
	{
		NO_ARMOR_PHYSICAL_SOURCES.add(CrystalSpire.SpireSpike.class);
		NO_ARMOR_PHYSICAL_SOURCES.add(GnollGeomancer.Boulder.class);
		NO_ARMOR_PHYSICAL_SOURCES.add(GnollGeomancer.GnollRockFall.class);
		NO_ARMOR_PHYSICAL_SOURCES.add(GnollRockfallTrap.class);
		NO_ARMOR_PHYSICAL_SOURCES.add(DwarfKing.KingDamager.class);
		NO_ARMOR_PHYSICAL_SOURCES.add(DwarfKing.Summoning.class);
		NO_ARMOR_PHYSICAL_SOURCES.add(LifeLink.class);
		NO_ARMOR_PHYSICAL_SOURCES.add(Chasm.class);
		NO_ARMOR_PHYSICAL_SOURCES.add(WandOfBlastWave.Knockback.class);
		NO_ARMOR_PHYSICAL_SOURCES.add(Heap.class); //damage from wraiths attempting to spawn from heaps
		NO_ARMOR_PHYSICAL_SOURCES.add(Necromancer.SummoningBlockDamage.class);
		NO_ARMOR_PHYSICAL_SOURCES.add(DriedRose.GhostHero.NoRoseDamage.class);
	}

	public void destroy() {
		setHP(0);
		Actor.remove( this );

		for (Char ch : Actor.chars().toArray(new Char[0])){
			if (ch.buff(Charm.class) != null && ch.buff(Charm.class).object == id()){
				ch.buff(Charm.class).detach();
			}
			if (ch.buff(Dread.class) != null && ch.buff(Dread.class).object == id()){
				ch.buff(Dread.class).detach();
			}
			if (ch.buff(Terror.class) != null && ch.buff(Terror.class).object == id()){
				ch.buff(Terror.class).detach();
			}
			if (ch.buff(SnipersMark.class) != null && ch.buff(SnipersMark.class).object == id()){
				ch.buff(SnipersMark.class).detach();
			}
			if (ch.buff(Talent.FollowupStrikeTracker.class) != null
					&& ch.buff(Talent.FollowupStrikeTracker.class).object == id()){
				ch.buff(Talent.FollowupStrikeTracker.class).detach();
			}
			if (ch.buff(Talent.DeadlyFollowupTracker.class) != null
					&& ch.buff(Talent.DeadlyFollowupTracker.class).object == id()){
				ch.buff(Talent.DeadlyFollowupTracker.class).detach();
			}
		}
	}

	public static final class DamageCause {
		private final Object cause;
		private final Char damageOwner;

		public DamageCause(Char damageSource){
			this.cause = this.damageOwner = damageSource;
		}
		public DamageCause(Object cause, Char damageSource){
			this.cause = cause;
			this.damageOwner = damageSource;
		}

		public Object getCause() {
			return cause;
		}
		public  Char getDamageOwner(){
			return damageOwner;
		}
	}

	public void die(@NotNull DamageCause src ) {
		destroy();
		if (src.getCause() != Chasm.class) {
			getSprite().die();
			if (!flying && Dungeon.level != null && getSprite() instanceof MobSprite && Dungeon.level.map[pos] == Terrain.CHASM){
				((MobSprite) getSprite()).fall();
			}
		}
	}

	//we cache this info to prevent having to call buff(...) in isAlive.
	//This is relevant because we call isAlive during drawing, which has both performance
	//and thread coordination implications
	public boolean deathMarked = false;

	public boolean isAlive() {
		return getHP() > 0 || deathMarked;
	}

	public boolean isActive() {
		return isAlive();
	}

	@Override
	protected void spendConstant(float time) {
		TimekeepersHourglass.timeFreeze freeze = buff(TimekeepersHourglass.timeFreeze.class);
		if (freeze != null) {
			freeze.processTime(time);
			return;
		}

		Swiftthistle.TimeBubble bubble = buff(Swiftthistle.TimeBubble.class);
		if (bubble != null){
			bubble.processTime(time);
			return;
		}

		super.spendConstant(time);
	}

	@Override
	public void spend(float time) {

		float timeScale = 1f;
		if (buff( Slow.class ) != null) {
			timeScale *= 0.5f;
			//slowed and chilled do not stack
		} else if (buff( Chill.class ) != null) {
			timeScale *= buff( Chill.class ).speedFactor();
		}
		if (buff( Speed.class ) != null) {
			timeScale *= 2.0f;
		}

		super.spend( time / timeScale );
	}

	public synchronized LinkedHashSet<Buff> buffs() {
		return new LinkedHashSet<>(buffs);
	}

	@SuppressWarnings("unchecked")
	//returns all buffs assignable from the given buff class
	public synchronized <T extends Buff> HashSet<T> buffs( Class<T> c ) {
		HashSet<T> filtered = new HashSet<>();
		for (Buff b : buffs) {
			if (c.isInstance( b )) {
				filtered.add( (T)b );
			}
		}
		return filtered;
	}

	@SuppressWarnings("unchecked")
	//returns an instance of the specific buff class, if it exists. Not just assignable
	public synchronized  <T extends Buff> T buff( Class<T> c ) {
		for (Buff b : buffs) {
			if (b.getClass() == c) {
				return (T)b;
			}
		}
		return null;
	}

	public synchronized boolean isCharmedBy( Char ch ) {
		int chID = ch.id();
		for (Buff b : buffs) {
			if (b instanceof Charm && ((Charm)b).object == chID) {
				return true;
			}
		}
		return false;
	}

	public synchronized boolean add( Buff buff ) {

		if (buff(PotionOfCleansing.Cleanse.class) != null) { //cleansing buff
			if (buff.type == Buff.buffType.NEGATIVE
					&& !(buff instanceof AllyBuff)
					&& !(buff instanceof LostInventory)){
				return false;
			}
		}

		if (getSprite() != null && buff(Challenge.SpectatorFreeze.class) != null){
			return false; //can't add buffs while frozen and game is loaded
		}

		buffs.add( buff );
		if (Actor.chars().contains(this)) Actor.add( buff );

		if (getSprite() != null && buff.announced) {
			switch (buff.type) {
				case POSITIVE:
					getSprite().showStatus(CharSprite.POSITIVE, Messages.titleCase(buff.name()));
					break;
				case NEGATIVE:
					getSprite().showStatus(CharSprite.WARNING, Messages.titleCase(buff.name()));
					break;
				case NEUTRAL:
				default:
					getSprite().showStatus(CharSprite.NEUTRAL, Messages.titleCase(buff.name()));
					break;
			}
		}

		return true;

	}

	public synchronized boolean remove( Buff buff ) {

		buffs.remove( buff );
		Actor.remove( buff );

		return true;
	}

	public synchronized void remove( Class<? extends Buff> buffClass ) {
		for (Buff buff : buffs( buffClass )) {
			remove( buff );
		}
	}

	@Override
	protected synchronized void onRemove() {
		for (Buff buff : buffs.toArray(new Buff[buffs.size()])) {
			buff.detach();
		}
	}

	public synchronized void updateSpriteState() {
		for (Buff buff:buffs) {
			buff.fx( true );
		}
	}

	public float stealth() {
		float stealth = 0;

		stealth += Obfuscation.stealthBoost(this, glyphLevel(Obfuscation.class));

		return stealth;
	}

	public final void move( int step ) {
		move( step, true );
	}

	//travelling may be false when a character is moving instantaneously, such as via teleportation
	public void move( int step, boolean travelling ) {

		if (travelling && Dungeon.level.adjacent( step, pos ) && buff( Vertigo.class ) != null) {
			getSprite().interruptMotion();
			int newPos = pos + PathFinder.NEIGHBOURS8[Random.Int( 8 )];
			if (!(Dungeon.level.passable[newPos] || Dungeon.level.avoid[newPos])
					|| (properties().contains(Property.LARGE) && !Dungeon.level.openSpace[newPos])
					|| Actor.findChar( newPos ) != null)
				return;
			else {
				getSprite().move(pos, newPos);
				step = newPos;
			}
		}

		if (Dungeon.level.map[pos] == Terrain.OPEN_DOOR) {
			Door.leave( pos );
		}

		pos = step;

		if (!(this instanceof Hero)) {
			getSprite().visible = Dungeon.visibleforAnyHero(pos);
		}

		Dungeon.level.occupyCell(this );
	}

	public int distance( Char other ) {
		return Dungeon.level.distance( pos, other.pos );
	}

	public boolean[] modifyPassable( boolean[] passable){
		//do nothing by default, but some chars can pass over terrain that others can't
		return passable;
	}

	public void onMotionComplete() {
		//Does nothing by default
		//The main actor thread already accounts for motion,
		// so calling next() here isn't necessary (see Actor.process)
	}

	public void onAttackComplete() {
		next();
	}

	public void onOperateComplete() {
		next();
	}

	protected final HashSet<Class> resistances = new HashSet<>();

	//returns percent effectiveness after resistances
	//TODO currently resistances reduce effectiveness by a static 50%, and do not stack.
	public float resist( Class effect ){
		HashSet<Class> resists = new HashSet<>(resistances);
		for (Property p : properties()){
			resists.addAll(p.resistances());
		}
		for (Buff b : buffs()){
			resists.addAll(b.resistances());
		}

		float result = 1f;
		for (Class c : resists){
			if (ClassReflection.isAssignableFrom(c, effect)){
				result *= 0.5f;
			}
		}
		return result * RingOfElements.resist(this, effect);
	}

	protected final HashSet<Class> immunities = new HashSet<>();

	public boolean isImmune(Class effect ){
		HashSet<Class> immunes = new HashSet<>(immunities);
		for (Property p : properties()){
			immunes.addAll(p.immunities());
		}
		for (Buff b : buffs()){
			immunes.addAll(b.immunities());
		}
if (glyphLevel(Brimstone.class) >= 0){
			immunes.add(Burning.class);
		}

		for (Class c : immunes){
			if (c.isAssignableFrom(effect)){
				return true;
			}
		}
		return false;
	}

	//similar to isImmune, but only factors in damage.
	//Is used in AI decision-making
	public boolean isInvulnerable( Class effect ){
		return buff(Challenge.SpectatorFreeze.class) != null || buff(Invulnerability.class) != null;
	}

	protected HashSet<Property> properties = new HashSet<>();

	public HashSet<Property> properties() {
		HashSet<Property> props = new HashSet<>(properties);
		//TODO any more of these and we should make it a property of the buff, like with resistances/immunities
		if (buff(ChampionEnemy.Giant.class) != null) {
			props.add(Property.LARGE);
		}
		return props;
	}

	public enum Property{
		BOSS ( new HashSet<Class>( Arrays.asList(Grim.class, GrimTrap.class, ScrollOfRetribution.class, ScrollOfPsionicBlast.class)),
				new HashSet<Class>( Arrays.asList(AllyBuff.class, Dread.class) )),
		MINIBOSS ( new HashSet<Class>(),
				new HashSet<Class>( Arrays.asList(AllyBuff.class, Dread.class) )),
		BOSS_MINION,
		UNDEAD,
		DEMONIC,
		INORGANIC ( new HashSet<Class>(),
				new HashSet<Class>( Arrays.asList(Bleeding.class, ToxicGas.class, Poison.class) )),
		FIERY ( new HashSet<Class>( Arrays.asList(WandOfFireblast.class, Elemental.FireElemental.class)),
				new HashSet<Class>( Arrays.asList(Burning.class, Blazing.class))),
		ICY ( new HashSet<Class>( Arrays.asList(WandOfFrost.class, Elemental.FrostElemental.class)),
				new HashSet<Class>( Arrays.asList(Frost.class, Chill.class))),
		ACIDIC ( new HashSet<Class>( Arrays.asList(Corrosion.class)),
				new HashSet<Class>( Arrays.asList(Ooze.class))),
		ELECTRIC ( new HashSet<Class>( Arrays.asList(WandOfLightning.class, Shocking.class, Potential.class,
										Electricity.class, ShockingDart.class, Elemental.ShockElemental.class )),
				new HashSet<Class>()),
		LARGE,
		IMMOVABLE ( new HashSet<Class>(),
				new HashSet<Class>( Arrays.asList(Vertigo.class) )),
		//A character that acts in an unchanging manner. immune to AI state debuffs or stuns/slows
		STATIC( new HashSet<Class>(),
				new HashSet<Class>( Arrays.asList(AllyBuff.class, Dread.class, Terror.class, Amok.class, Charm.class, Sleep.class,
									Paralysis.class, Frost.class, Chill.class, Slow.class, Speed.class) ));

		private HashSet<Class> resistances;
		private HashSet<Class> immunities;

		Property(){
			this(new HashSet<Class>(), new HashSet<Class>());
		}

		Property( HashSet<Class> resistances, HashSet<Class> immunities){
			this.resistances = resistances;
			this.immunities = immunities;
		}

		public HashSet<Class> resistances(){
			return new HashSet<>(resistances);
		}

		public HashSet<Class> immunities(){
			return new HashSet<>(immunities);
		}

	}

	public static boolean hasProp( Char ch, Property p){
		return (ch != null && ch.properties().contains(p));
	}
}
