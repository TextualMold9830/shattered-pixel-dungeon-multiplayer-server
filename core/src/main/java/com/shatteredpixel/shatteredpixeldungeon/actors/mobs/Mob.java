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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import static com.shatteredpixel.shatteredpixeldungeon.HeroHelp.getHeroID;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Adrenaline;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dread;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.GreaterHaste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MonkEnergy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Preparation;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Sleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SoulMark;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.PowerOfMany;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.duelist.Feint;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.ShadowClone;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.ClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.GuidingLight;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.Stasis;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.DirectableAlly;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.nikita22007.multiplayer.server.effects.Surprise;
import com.nikita22007.multiplayer.server.effects.Wound;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.MasterThievesArmband;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAggression;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ExoticCrystals;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ShardOfOblivion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Lucky;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.nikita22007.multiplayer.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class Mob extends Char {

	{
		actPriority = MOB_PRIO;

		alignment = Alignment.ENEMY;
	}

	public AiState SLEEPING = new Sleeping();
	public AiState HUNTING = new Hunting();
	public AiState WANDERING = new Wandering();
	public AiState FLEEING = new Fleeing();
	public AiState PASSIVE = new Passive();
	public AiState state = SLEEPING;

	public Class<? extends CharSprite> spriteClass;

	protected int target = -1;

	public int defenseSkill = 0;

	public int EXP = 1;
	public int maxLvl = Hero.MAX_LEVEL - 1;

	protected Char enemy;
	protected int enemyID = -1; //used for save/restore
	protected boolean enemySeen;
	protected boolean alerted = false;

	protected static final float TIME_TO_WAKE_UP = 1f;

	protected boolean firstAdded = true;

	protected void onAdd() {
		super.onAdd();
		if (firstAdded) {
			//modify health for ascension challenge if applicable, only on first add
			float percent = getHP() / (float) getHT();
			setHT(Math.round(getHT() * AscensionChallenge.statModifier(this)));
			setHP(Math.round(getHT() * percent));
			firstAdded = false;
		}
	}

	private static final String STATE = "state";
	private static final String SEEN = "seen";
	private static final String TARGET = "target";
	private static final String MAX_LVL = "max_lvl";

	private static final String ENEMY_ID = "enemy_id";

	@Override
	public void storeInBundle(Bundle bundle) {

		super.storeInBundle(bundle);

		if (state == SLEEPING) {
			bundle.put(STATE, Sleeping.TAG);
		} else if (state == WANDERING) {
			bundle.put(STATE, Wandering.TAG);
		} else if (state == HUNTING) {
			bundle.put(STATE, Hunting.TAG);
		} else if (state == FLEEING) {
			bundle.put(STATE, Fleeing.TAG);
		} else if (state == PASSIVE) {
			bundle.put(STATE, Passive.TAG);
		}
		bundle.put(SEEN, enemySeen);
		bundle.put(TARGET, target);
		bundle.put(MAX_LVL, maxLvl);

		if (enemy != null) {
			bundle.put(ENEMY_ID, enemy.id());
		}
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {

		super.restoreFromBundle(bundle);

		String state = bundle.getString(STATE);
		if (state.equals(Sleeping.TAG)) {
			this.state = SLEEPING;
		} else if (state.equals(Wandering.TAG)) {
			this.state = WANDERING;
		} else if (state.equals(Hunting.TAG)) {
			this.state = HUNTING;
		} else if (state.equals(Fleeing.TAG)) {
			this.state = FLEEING;
		} else if (state.equals(Passive.TAG)) {
			this.state = PASSIVE;
		}

		enemySeen = bundle.getBoolean(SEEN);

		target = bundle.getInt(TARGET);

		if (bundle.contains(MAX_LVL)) maxLvl = bundle.getInt(MAX_LVL);

		if (bundle.contains(ENEMY_ID)) {
			enemyID = bundle.getInt(ENEMY_ID);
		}

		//no need to actually save this, must be false
		firstAdded = false;
	}

	//mobs need to remember their targets after every actor is added
	public void restoreEnemy() {
		if (enemyID != -1 && enemy == null) enemy = (Char) Actor.findById(enemyID);
	}

	public CharSprite sprite() {
		return Reflection.newInstance(spriteClass);
	}

	@Override
	protected boolean act() {

		super.act();

		boolean justAlerted = alerted;
		alerted = false;
		if(getSprite() != null) {
			if (justAlerted) {
				getSprite().showAlert();
			} else {
				getSprite().hideAlert();
				getSprite().hideLost();
			}
		}
		if (paralysed > 0) {
			enemySeen = false;
			spend(TICK);
			return true;
		}

		if (buff(Terror.class) != null || buff(Dread.class) != null) {
			state = FLEEING;
		}

		enemy = chooseEnemy();

		boolean enemyInFOV = enemy != null && enemy.isAlive() && fieldOfView[enemy.pos] && enemy.invisible <= 0;

		//prevents action, but still updates enemy seen status
		if (buff(Feint.AfterImage.FeintConfusion.class) != null) {
			enemySeen = enemyInFOV;
			spend(TICK);
			return true;
		}

		boolean result = state.act(enemyInFOV, justAlerted);

		//for updating hero FOV
		if (buff(PowerOfMany.PowerBuff.class) != null){
			Dungeon.level.updateFieldOfView( this, fieldOfView );
			GameScene.updateFog(pos, viewDistance+(int)Math.ceil(speed()));
		}

		return result;
	}

	//FIXME this is sort of a band-aid correction for allies needing more intelligent behaviour
	protected boolean intelligentAlly = false;

	protected Char chooseEnemy() {

		Dread dread = buff(Dread.class);
		if (dread != null) {
			Char source = (Char) Actor.findById(dread.object);
			if (source != null) {
				return source;
			}
		}

		Terror terror = buff(Terror.class);
		if (terror != null) {
			Char source = (Char) Actor.findById(terror.object);
			if (source != null) {
				return source;
			}
		}

		//if we are an alert enemy, auto-hunt a target that is affected by aggression, even another enemy
		if ((alignment == Alignment.ENEMY || buff(Amok.class) != null) && state != PASSIVE && state != SLEEPING) {
			if (enemy != null && enemy.buff(StoneOfAggression.Aggression.class) != null) {
				state = HUNTING;
				return enemy;
			}
			for (Char ch : Actor.chars()) {
				if (ch != this && fieldOfView[ch.pos] &&
						ch.buff(StoneOfAggression.Aggression.class) != null) {
					state = HUNTING;
					return ch;
				}
			}
		}

		//find a new enemy if..
		boolean newEnemy = false;
		//we have no enemy, or the current one is dead/missing
		if (enemy == null || !enemy.isAlive() || !Actor.chars().contains(enemy) || state == WANDERING) {
			newEnemy = true;
			//We are amoked and current enemy is the hero
		} else if (buff(Amok.class) != null && enemy instanceof Hero) {
			newEnemy = true;
			//We are charmed and current enemy is what charmed us
		} else if (buff(Charm.class) != null && buff(Charm.class).object == enemy.id()) {
			newEnemy = true;
		}

		//additionally, if we are an ally, find a new enemy if...
		if (!newEnemy && alignment == Alignment.ALLY) {
			//current enemy is also an ally
			if (enemy.alignment == Alignment.ALLY) {
				newEnemy = true;
				//current enemy is invulnerable
			} else if (enemy.isInvulnerable(getClass())) {
				newEnemy = true;
			}
		}

		if (newEnemy) {

			HashSet<Char> enemies = new HashSet<>();

			//if we are amoked...
			if (buff(Amok.class) != null) {
				//try to find an enemy mob to attack first.
				for (Mob mob : Dungeon.level.mobs)
					if (mob.alignment == Alignment.ENEMY && mob != this
							&& fieldOfView[mob.pos] && mob.invisible <= 0) {
						enemies.add(mob);
					}

				if (enemies.isEmpty()) {
					//try to find ally mobs to attack second.
					for (Mob mob : Dungeon.level.mobs)
						if (mob.alignment == Alignment.ALLY && mob != this
								&& fieldOfView[mob.pos] && mob.invisible <= 0) {
							enemies.add(mob);
						}

					if (enemies.isEmpty()) {
						//TODO: check this
						//try to find the hero third
						HashSet<Char> candidates = new HashSet<>();
						int distance = Integer.MAX_VALUE;
						for (Hero hero : Dungeon.heroes) {
							if (hero != null) {

								if (fieldOfView[hero.pos] && hero.invisible <= 0) {
									int dist = Dungeon.level.distance(hero.pos, pos);
									if (dist < distance) {
										candidates.clear();
										distance = dist;
									}
									if (dist == distance) {
										candidates.add(hero);
									}
								}
							}
						}
						enemies.addAll(candidates);
					}
				}

				//if we are an ally...
			} else if (alignment == Alignment.ALLY) {
				//look for hostile mobs to attack
				for (Mob mob : Dungeon.level.mobs)
					if (mob.alignment == Alignment.ENEMY && fieldOfView[mob.pos]
							&& mob.invisible <= 0 && !mob.isInvulnerable(getClass()))
						//do not target passive mobs
						//intelligent allies also don't target mobs which are wandering or asleep
						if (mob.state != mob.PASSIVE &&
								(!intelligentAlly || (mob.state != mob.SLEEPING && mob.state != mob.WANDERING))) {
							enemies.add(mob);
						}

				//if we are an enemy...
			} else if (alignment == Alignment.ENEMY) {
				//look for ally mobs to attack
				for (Mob mob : Dungeon.level.mobs)
					if (mob.alignment == Alignment.ALLY && fieldOfView[mob.pos] && mob.invisible <= 0)
						enemies.add(mob);
				//TODO: check this
				//and look for the hero
				HashSet<Char> candidates = new HashSet<>();
				int distance = Integer.MAX_VALUE;
				for (Hero hero : Dungeon.heroes) {
					if (hero != null) {

						if (fieldOfView[hero.pos] && hero.invisible <= 0) {
							int dist = Dungeon.level.distance(hero.pos, pos);
							if (dist < distance) {
								candidates.clear();
								distance = dist;
							}
							if (dist == distance) {
								candidates.add(hero);
							}
						}
					}
				}
				enemies.addAll(candidates);

			}

			//do not target anything that's charming us
			Charm charm = buff(Charm.class);
			if (charm != null) {
				Char source = (Char) Actor.findById(charm.object);
				if (source != null && enemies.contains(source) && enemies.size() > 1) {
					enemies.remove(source);
				}
			}

			//neutral characters in particular do not choose enemies.
			if (enemies.isEmpty()) {
				return null;
			} else {
				//go after the closest potential enemy, preferring enemies that can be reached/attacked, and the hero if two are equidistant
				PathFinder.buildDistanceMap(pos, Dungeon.findPassable(this, Dungeon.level.passable, fieldOfView, true));
				Char closest = null;
				int closestDist = Integer.MAX_VALUE;

				for (Char curr : enemies) {
					int currDist = Integer.MAX_VALUE;
					//we aren't trying to move into the target, just toward them
					for (int i : PathFinder.NEIGHBOURS8){
						if (PathFinder.distance[curr.pos+i] < currDist){
							currDist = PathFinder.distance[curr.pos+i];
						}
					}
					if (closest == null) {
						closest = curr;
						closestDist = currDist;
					} else if (canAttack(closest) && !canAttack(curr)) {
						continue;
					} else if ((canAttack(curr) && !canAttack(closest))
							|| (currDist < closestDist)) {
						closest = curr;
					} else if (curr instanceof Hero &&
							(currDist == closestDist) || (canAttack(curr) && canAttack(closest))) {
						closest = curr;
					}
				}
				//if we were going to target the hero, but an afterimage exists, target that instead
				if (closest instanceof Hero) {
					for (Char ch : enemies) {
						if (ch instanceof Feint.AfterImage) {
							closest = ch;
							break;
						}
					}
				}

				return closest;
			}

		} else
			return enemy;
	}

	@Override
	public boolean add(Buff buff) {
		if (super.add(buff)) {
			if (buff instanceof Amok || buff instanceof AllyBuff) {
				state = HUNTING;
			} else if (buff instanceof Terror || buff instanceof Dread) {
				state = FLEEING;
			} else if (buff instanceof Sleep) {
				state = SLEEPING;
				postpone(Sleep.SWS);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean remove(Buff buff) {
		if (super.remove(buff)) {
			if (state == FLEEING && ((buff instanceof Terror && buff(Dread.class) == null)
					|| (buff instanceof Dread && buff(Terror.class) == null))) {
				if (enemySeen) {
					getSprite().showStatus(CharSprite.WARNING, Messages.get(this, "rage"));
					state = HUNTING;
				} else {
					state = WANDERING;
				}
			}
			return true;
		}
		return false;
	}

	protected boolean canAttack(Char enemy) {
		if (Dungeon.level.adjacent(pos, enemy.pos)) {
			return true;
		}
		for (ChampionEnemy buff : buffs(ChampionEnemy.class)) {
			if (buff.canAttackWithExtraReach(enemy)) {
				return true;
			}
		}
		return false;
	}

	private boolean cellIsPathable(int cell) {
		if (!Dungeon.level.passable[cell]) {
			if (flying || buff(Amok.class) != null) {
				if (!Dungeon.level.avoid[cell]) {
					return false;
				}
			} else {
				return false;
			}
		}
		if (Char.hasProp(this, Property.LARGE) && !Dungeon.level.openSpace[cell]) {
			return false;
		}
		if (Actor.findChar(cell) != null) {
			return false;
		}

		return true;
	}

	protected boolean getCloser(int target) {

		if (rooted || target == pos) {
			return false;
		}

		int step = -1;

		if (Dungeon.level.adjacent(pos, target)) {

			path = null;

			if (cellIsPathable(target)) {
				step = target;
			}

		} else {

			boolean newPath = false;
			float longFactor = state == WANDERING ? 2f : 1.33f;
			//scrap the current path if it's empty, no longer connects to the current location
			//or if it's quite inefficient and checking again may result in a much better path
			//mobs are much more tolerant of inefficient paths if wandering
			if (path == null || path.isEmpty()
					|| !Dungeon.level.adjacent(pos, path.getFirst())
					|| path.size() > longFactor * Dungeon.level.distance(pos, target))
				newPath = true;
			else if (path.getLast() != target) {
				//if the new target is adjacent to the end of the path, adjust for that
				//rather than scrapping the whole path.
				if (Dungeon.level.adjacent(target, path.getLast())) {
					int last = path.removeLast();

					if (path.isEmpty()) {

						//shorten for a closer one
						if (Dungeon.level.adjacent(target, pos)) {
							path.add(target);
							//extend the path for a further target
						} else {
							path.add(last);
							path.add(target);
						}

					} else {
						//if the new target is simply 1 earlier in the path shorten the path
						if (path.getLast() == target) {

							//if the new target is closer/same, need to modify end of path
						} else if (Dungeon.level.adjacent(target, path.getLast())) {
							path.add(target);

							//if the new target is further away, need to extend the path
						} else {
							path.add(last);
							path.add(target);
						}
					}

				} else {
					newPath = true;
				}

			}

			//checks if the next cell along the current path can be stepped into
			if (!newPath) {
				int nextCell = path.removeFirst();
				if (!cellIsPathable(nextCell)) {

					newPath = true;
					//If the next cell on the path can't be moved into, see if there is another cell that could replace it
					if (!path.isEmpty()) {
						for (int i : PathFinder.NEIGHBOURS8) {
							if (Dungeon.level.adjacent(pos, nextCell + i) && Dungeon.level.adjacent(nextCell + i, path.getFirst())) {
								if (cellIsPathable(nextCell + i)) {
									path.addFirst(nextCell + i);
									newPath = false;
									break;
								}
							}
						}
					}
				} else {
					path.addFirst(nextCell);
				}
			}

			//generate a new path
			if (newPath) {
				//If we aren't hunting, always take a full path
				PathFinder.Path full = Dungeon.findPath(this, target, Dungeon.level.passable, fieldOfView, true);
				if (state != HUNTING) {
					path = full;
				} else {
					//otherwise, check if other characters are forcing us to take a very slow route
					// and don't try to go around them yet in response, basically assume their blockage is temporary
					PathFinder.Path ignoreChars = Dungeon.findPath(this, target, Dungeon.level.passable, fieldOfView, false);
					if (ignoreChars != null && (full == null || full.size() > 2 * ignoreChars.size())) {
						//check if first cell of shorter path is valid. If it is, use new shorter path. Otherwise do nothing and wait.
						path = ignoreChars;
						if (!cellIsPathable(ignoreChars.getFirst())) {
							return false;
						}
					} else {
						path = full;
					}
				}
			}

			if (path != null) {
				step = path.removeFirst();
			} else {
				return false;
			}
		}
		if (step != -1) {
			move(step);
			return true;
		} else {
			return false;
		}
	}

	protected boolean getFurther(int target) {
		if (rooted || target == pos) {
			return false;
		}

		int step = Dungeon.flee(this, target, Dungeon.level.passable, fieldOfView, true);
		if (step != -1) {
			move(step);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void updateSpriteState() {
		super.updateSpriteState();
		for (Hero hero : Dungeon.heroes) {
			if (hero != null) {

			if (hero.buff(TimekeepersHourglass.timeFreeze.class) != null
					|| hero.buff(Swiftthistle.TimeBubble.class) != null) {
                getSprite().add(CharSprite.State.PARALYSED);
				break;
            }
		}
	}
}

	public float attackDelay() {
		float delay = 1f;
		if ( buff(Adrenaline.class) != null) delay /= 1.5f;
		return delay;
	}
	
	protected boolean doAttack( Char enemy ) {
		
		if (getSprite() != null && (getSprite().visible || enemy.getSprite().visible)) {
			getSprite().attack( enemy.pos );
			return false;
			
		} else {
			attack( enemy );
			Invisibility.dispel(this);
			spend( attackDelay() );
			return true;
		}
	}
	
	@Override
	public void onAttackComplete() {
		attack( enemy );
		Invisibility.dispel(this);
		spend( attackDelay() );
		super.onAttackComplete();
	}
	
	@Override
	public int defenseSkill( Char enemy ) {
		if (buff(GuidingLight.Illuminated.class) != null){
			//if the attacker is the cleric, they must be using a weapon they have the str for
			if (enemy instanceof Hero){
				Hero h = (Hero) enemy;
				if ((!(h.belongings.attackingWeapon() instanceof Weapon)
						|| ((Weapon) h.belongings.attackingWeapon()).STRReq() <= h.STR()) && h.heroClass == HeroClass.CLERIC){
					return 0;
				}
			} else {
				return 0;
			}
		}

		if ( !surprisedBy(enemy)
				&& paralysed == 0
				&& !(alignment == Alignment.ALLY && enemy instanceof Hero)) {
			return this.defenseSkill;
		} else {
			return 0;
		}
	}
	
	@Override
	public int defenseProc( Char enemy, int damage ) {
		
		if (enemy instanceof Hero
				&& ((Hero) enemy).belongings.attackingWeapon() instanceof MissileWeapon){
			Statistics.thrownAttacks++;
			Badges.validateHuntressUnlock();
		}
		
		if (surprisedBy(enemy)) {
			Statistics.sneakAttacks++;
			Badges.validateRogueUnlock();
			//TODO this is somewhat messy, it would be nicer to not have to manually handle delays here
			// playing the strong hit sound might work best as another property of weapon?
			if(enemy instanceof Hero) {
				Hero hero = (Hero) enemy;
				if (hero.belongings.attackingWeapon() instanceof SpiritBow.SpiritArrow
						|| hero.belongings.attackingWeapon() instanceof Dart) {
					Sample.INSTANCE.playDelayed(Assets.Sounds.HIT_STRONG, 0.125f);
				} else {
					Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
				}
			}
			if (enemy.buff(Preparation.class) != null) {
				Wound.hit(this);
			} else {
				Surprise.hit(this);
			}
		}

		//if attacked by something else than current target, and that thing is closer, switch targets
		//or if attacked by target, simply update target position
		if (state != FLEEING) {
			if (state != HUNTING) {
				aggro(enemy);
				target = enemy.pos;
			} else {
				recentlyAttackedBy.add(enemy);
			}
		}

		if (buff(SoulMark.class) != null) {
			int restoration = Math.min(damage, getHP() + shielding());
			//TODO: change this
			for (Hero hero : Dungeon.heroes) {
				if (hero != null) {
					if (!(enemy instanceof Hero)) {
						restoration = Math.round(restoration * 0.4f * hero.pointsInTalent(Talent.SOUL_SIPHON) / 3f);
					}
					if (restoration > 0) {
						Buff.affect(hero, Hunger.class).affectHunger(restoration * hero.pointsInTalent(Talent.SOUL_EATER) / 3f);

						if (hero.getHP() < hero.getHT()) {
							int heal = (int) Math.ceil(restoration * 0.4f);
							hero.setHP(Math.min(hero.getHT(), hero.getHP() + heal));
							hero.getSprite().showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(heal), FloatingText.HEALING);
						}
					}
				}
			}
		}

		return super.defenseProc(enemy, damage);
	}

	@Override
	public float speed() {
		return super.speed() * AscensionChallenge.enemySpeedModifier(this);
	}

	public final boolean surprisedBy( Char enemy ){
		return surprisedBy( enemy, true);
	}

	public boolean surprisedBy( Char enemy, boolean attacking ){
		return enemy instanceof Hero
				&& (enemy.invisible > 0 || !enemySeen || (fieldOfView != null && fieldOfView.length == Dungeon.level.length() && !fieldOfView[enemy.pos]))
				&& (!attacking || enemy.canSurpriseAttack());
	}

	//whether the hero should interact with the mob (true) or attack it (false)
	public boolean heroShouldInteract(){
		return alignment != Alignment.ENEMY && buff(Amok.class) == null;
	}

	public void aggro( Char ch ) {
		enemy = ch;
		if (state != PASSIVE){
			state = HUNTING;
		}
	}

	public void clearEnemy(){
		enemy = null;
		enemySeen = false;
		if (state == HUNTING) state = WANDERING;
	}
	
	public boolean isTargeting( Char ch){
		return enemy == ch;
	}

	@Override
	public void damage(int dmg, @NotNull DamageCause source ) {
		Object src = source.getCause();
		if (!isInvulnerable(src.getClass())) {
			if (state == SLEEPING) {
				state = WANDERING;
			}
			if (!(src instanceof Corruption) && state != FLEEING) {
				if (state != HUNTING) {
					alerted = true;
					//assume the hero is hitting us in these common cases
					if (src instanceof Wand || src instanceof ClericSpell || src instanceof ArmorAbility) {
						aggro(source.getDamageOwner());
						target = source.getDamageOwner().pos;
					}
				} else {
					if (src instanceof Wand || src instanceof ClericSpell || src instanceof ArmorAbility) {
						recentlyAttackedBy.add(source.getDamageOwner());
					}
				}
			}
		}
		
		super.damage( dmg, source );
	}
	
	
	@Override
	public void destroy() {

		super.destroy();

		Dungeon.level.mobs.remove(this);
		for (Hero hero : Dungeon.heroes) {
			if (hero != null) {
				if (hero.buff(MindVision.class) != null) {
					Dungeon.observe(hero);
					GameScene.updateFog(pos, 2);
				}
			}
		}
		for(Hero hero: Dungeon.heroes) {
			if (hero != null) {
				if (hero.isAlive()) {

					if (alignment == Alignment.ENEMY) {
						Statistics.enemiesSlain++;
						Badges.validateMonstersSlain();
						Statistics.qualifiedForNoKilling = false;
				Bestiary.setSeen(getClass());
				Bestiary.countEncounter(getClass());

						AscensionChallenge.processEnemyKill(this, hero);

						int exp = hero.lvl <= maxLvl ? EXP : 0;

						//during ascent, under-levelled enemies grant 10 xp each until level 30
						// after this enemy kills which reduce the amulet curse still grant 10 effective xp
						// for the purposes of on-exp effects, see AscensionChallenge.processEnemyKill
						if (hero.buff(AscensionChallenge.class) != null &&
								exp == 0 && maxLvl > 0 && EXP > 0 && hero.lvl < Hero.MAX_LEVEL) {
							exp = Math.round(10 * spawningWeight());
						}

						if (exp > 0) {
							hero.getSprite().showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(exp), FloatingText.EXPERIENCE);
						}
						hero.earnExp(exp, getClass());

						if (hero.subClass == HeroSubClass.MONK) {
							Buff.affect(hero, MonkEnergy.class).gainEnergy(this);
						}
					}
				}
			}
		}
	}
	
	@Override
	public void die(@NotNull DamageCause damageCause ) {
		final Object cause = damageCause.getCause();
		final Char damageSource = damageCause.getDamageOwner();
		final Hero damageSourceHero;
		if (damageSource instanceof Hero){
			damageSourceHero = (Hero) damageSource;
		} else if (cause instanceof Hero) {
			//In most cases, that should be covered by the previous branch
			damageSourceHero = (Hero) cause;
		} else if (cause instanceof Item){
			//This is usually covered through Item.currUser and damageSource
			damageSourceHero = ((Item)cause).findOwner();
		} else if (cause instanceof Weapon.Enchantment) {
			//This is usually covered through Item.currUser and damageSource
			damageSourceHero = null; //search items?
 		} else {
			damageSourceHero = null;
		}

		if (cause == Chasm.class){
			//50% chance to round up, 50% to round down
			if (EXP % 2 == 1) EXP += Random.Int(2);
			EXP /= 2;
		}

		if (alignment == Alignment.ENEMY){
			rollToDropLoot(damageSourceHero);
			//TODO: check this
			if ((damageSourceHero != null) && (damageSource instanceof Hero || cause instanceof Hero || cause instanceof Weapon || cause instanceof Weapon.Enchantment)){
				if (damageSourceHero.hasTalent(Talent.LETHAL_MOMENTUM)
						&& Random.Float() < 0.34f + 0.33f * damageSourceHero.pointsInTalent(Talent.LETHAL_MOMENTUM)) {
					Buff.affect(damageSourceHero, Talent.LethalMomentumTracker.class, 0f);
				}
				if (damageSourceHero.heroClass != HeroClass.DUELIST
						&& damageSourceHero.hasTalent(Talent.LETHAL_HASTE)
						&& (damageSourceHero.buff(Talent.LethalHasteCooldown.class) == null)) {
					Buff.affect(damageSourceHero, Talent.LethalHasteCooldown.class, 100f);
					Buff.affect(damageSourceHero, GreaterHaste.class).set( 2 + 2 *damageSourceHero.pointsInTalent(Talent.LETHAL_HASTE));
				}
				}

		}

		String message = Messages.get(this, "died");
		for (Hero hero : Dungeon.heroes) {
			if (hero == null) {
				continue;
			}
			if (!hero.fieldOfView[pos]) {
				GLog.iWithTarget(hero.networkID, message);
			}
		}
		boolean soulMarked = buff(SoulMark.class) != null;

		super.die( damageCause );

		if ((damageSourceHero != null) && !(this instanceof Wraith)
				&& soulMarked
				&& Random.Float() < (0.4f*damageSourceHero.pointsInTalent(Talent.NECROMANCERS_MINIONS)/3f)){
			Wraith w = Wraith.spawnAt(pos, Wraith.class);
			if (w != null) {
				Buff.affect(w, Corruption.class);
				if (Dungeon.visibleforAnyHero(pos)) {
					CellEmitter.get(pos).burst(ShadowParticle.CURSE, 6);
					Sample.INSTANCE.play(Assets.Sounds.CURSED);
				}
			}
		}
	}

	public float lootChance(Hero hero){
		float lootChance = this.lootChance;
		float dropBonus = 1f;
		if(hero != null) {
			dropBonus = RingOfWealth.dropChanceMultiplier(hero);

			Talent.BountyHunterTracker bhTracker = hero.buff(Talent.BountyHunterTracker.class);
			if (bhTracker != null) {
				Preparation prep = hero.buff(Preparation.class);
				if (prep != null) {
					// 2/4/8/16% per prep level, multiplied by talent points
					float bhBonus = 0.02f * (float) Math.pow(2, prep.attackLevel() - 1);
					bhBonus *= hero.pointsInTalent(Talent.BOUNTY_HUNTER);
					dropBonus += bhBonus;
				}
			}

			dropBonus += ShardOfOblivion.lootChanceMultiplier(hero) - 1f;
		}
		return lootChance * dropBonus;
	}
	
	public void rollToDropLoot(Hero hero){
		if (hero == null){
			for (Hero anyHero: Dungeon.heroes) {
				if (anyHero != null && anyHero.lvl > maxLvl + 2) return;
			}
		} else {
			if (hero.lvl > maxLvl + 2) return;
		}

		MasterThievesArmband.StolenTracker stolen = buff(MasterThievesArmband.StolenTracker.class);
		if (stolen == null || !stolen.itemWasStolen()) {
			if (Random.Float() < lootChance(hero)) {
				Item loot = createLoot();
				if (loot != null) {
					Dungeon.level.drop(loot, pos).sprite.drop();
				}
			}
		}
		
		//ring of wealth logic
		if (hero != null && Ring.getBuffedBonus(hero, RingOfWealth.Wealth.class) > 0) {
			int rolls = 1;
			if (properties.contains(Property.BOSS)) rolls = 15;
			else if (properties.contains(Property.MINIBOSS)) rolls = 5;
			ArrayList<Item> bonus = RingOfWealth.tryForBonusDrop(hero, rolls);
			if (bonus != null && !bonus.isEmpty()) {
				for (Item b : bonus) Dungeon.level.drop(b, pos).sprite.drop();
				RingOfWealth.showFlareForBonusDrop(getSprite());
			}
		}
		
		//lucky enchant logic
		if (buff(Lucky.LuckProc.class) != null){
			Dungeon.level.drop(buff(Lucky.LuckProc.class).genLoot(), pos).sprite.drop();
			Lucky.showFlare(getSprite());
		}

		//soul eater talent
		if (hero != null && buff(SoulMark.class) != null &&
				Random.Int(10) < hero.pointsInTalent(Talent.SOUL_EATER)){
			Talent.onFoodEaten(hero, 0, null);
		}

	}
	
	protected Object loot = null;
	protected float lootChance = 0;
	
	@SuppressWarnings("unchecked")
	public Item createLoot() {
		Item item;
		if (loot instanceof Generator.Category) {

			item = Generator.randomUsingDefaults( (Generator.Category)loot );

		} else if (loot instanceof Class<?>) {

			if (ExoticPotion.regToExo.containsKey(loot)){
				if (Random.Float() < ExoticCrystals.consumableExoticChance()){
					return Generator.random(ExoticPotion.regToExo.get(loot));
				}
			} else if (ExoticScroll.regToExo.containsKey(loot)){
				if (Random.Float() < ExoticCrystals.consumableExoticChance()){
					return Generator.random(ExoticScroll.regToExo.get(loot));
				}
			}

			item = Generator.random( (Class<? extends Item>)loot );

		} else {

			item = (Item)loot;

		}
		return item;
	}

	//how many mobs this one should count as when determining spawning totals
	public float spawningWeight(){
		return 1;
	}
	
	public boolean reset() {
		return false;
	}
	
	public void beckon( int cell ) {
		
		notice();
		
		if (state != HUNTING && state != FLEEING) {
			state = WANDERING;
		}
		target = cell;
	}

	public Hero chooseClosestHero() {
		return (Hero) selectClosestChar(Dungeon.heroes, true);
	}

	public Char selectClosestChar(Char[] chars, boolean skipInvisible){
		List<Char> targets = new LinkedList<>();
		for (Char target : chars) {
			if (target == null) continue;
			if (!skipInvisible && target.invisible > 0) {
				continue;
			}
			if (!target.isActive()) {
				continue;
			}
			targets.add(target);
		}
		if (targets.isEmpty()) {
			return null;
		}

		Char closestTarget = null;
		int distance = Integer.MAX_VALUE;
		PathFinder.buildDistanceMap(this.pos, Dungeon.level.passable);
		for (Char target : targets) {
			if (distance > PathFinder.distance[target.pos]) {
				closestTarget = target;
				distance = PathFinder.distance[target.pos];
			}
		}
		return closestTarget;
	}

	public Char beckon( Char[] chars, boolean skipInvisible ) {
		Char target = selectClosestChar(chars, skipInvisible);;
		if (target != null){
			this.beckon(target.pos);
		}
		return target;
	}

	public String description() {
		return Messages.get(this, "desc");
	}

	public String info(){
		String desc = description();

		for (Buff b : buffs(ChampionEnemy.class)){
			desc += "\n\n_" + Messages.titleCase(b.name()) + "_\n" + b.desc();
		}

		return desc;
	}
	
	public void notice() {
		getSprite().showAlert();
	}
	public void yell( String str, Hero hero ) {
		GLog.newLine();
		GLog.n( "%s: \"%s\" ", Messages.titleCase(name()), str, hero);
	}
	public void yell( String str ) {
		GLog.newLine();
		GLog.n( "%s: \"%s\" ", Messages.titleCase(name()), str );
	}

	//some mobs have an associated landmark entry, which is added when the hero sees them
	//mobs may also remove this landmark in some cases, such as when a quest is complete or they die
	public Notes.Landmark landmark(){
		return null;
	}

	public interface AiState {
		boolean act( boolean enemyInFOV, boolean justAlerted );
	}

	protected class Sleeping implements AiState {

		public static final String TAG	= "SLEEPING";

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {

			//debuffs cause mobs to wake as well
			for (Buff b : buffs()){
				if (b.type == Buff.buffType.NEGATIVE){
					awaken(enemyInFOV);
					if (state == SLEEPING){
						spend(TICK); //wait if we can't wake up for some reason
					}
					return true;
				}
			}

			//can be awoken by the least stealthy hostile present, not necessarily just our target
			if (enemyInFOV || (enemy != null && enemy.invisible > 0)) {

				float closestHostileDist = Float.POSITIVE_INFINITY;

				for (Char ch : Actor.chars()){
					if (fieldOfView[ch.pos] && ch.invisible == 0 && ch.alignment != alignment && ch.alignment != Alignment.NEUTRAL){
						float chDist = ch.stealth() + distance(ch);
						//silent steps rogue talent, which also applies to rogue's shadow clone
						Hero owner = null;
						if (ch instanceof ShadowClone.ShadowAlly) {
							owner = ((ShadowClone.ShadowAlly) ch).getOwner();
						}
						if ((ch instanceof Hero || ch instanceof ShadowClone.ShadowAlly)
								&& (owner != null && owner.hasTalent(Talent.SILENT_STEPS))){
							if (distance(ch) >= 4 - owner.pointsInTalent(Talent.SILENT_STEPS)) {
								chDist = Float.POSITIVE_INFINITY;
							}
						}
						//flying characters are naturally stealthy
						if (ch.flying && distance(ch) >= 2){
							chDist = Float.POSITIVE_INFINITY;
						}
						if (chDist < closestHostileDist){
							closestHostileDist = chDist;
						}
					}
				}

				if (Random.Float( closestHostileDist ) < 1) {
					awaken(enemyInFOV);
					if (state == SLEEPING){
						spend(TICK); //wait if we can't wake up for some reason
					}
					return true;
				}

			}

			enemySeen = false;
			spend( TICK );

			return true;
		}

		protected void awaken( boolean enemyInFOV ){
			if (enemyInFOV) {
				enemySeen = true;
				notice();
				state = HUNTING;
				target = enemy.pos;
			} else {
				notice();
				state = WANDERING;
				target = Dungeon.level.randomDestination( Mob.this );
			}

			if (alignment == Alignment.ENEMY && Dungeon.isChallenged(Challenges.SWARM_INTELLIGENCE)) {
				for (Mob mob : Dungeon.level.mobs) {
					if (mob.paralysed <= 0
							&& Dungeon.level.distance(pos, mob.pos) <= 8
							&& mob.state != mob.HUNTING) {
						mob.beckon(target);
					}
				}
			}
			spend(TIME_TO_WAKE_UP);
		}
	}

	protected class Wandering implements AiState {

		public static final String TAG	= "WANDERING";

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			if (enemyInFOV && (justAlerted || Random.Float( distance( enemy ) / 2f + enemy.stealth() ) < 1)) {

				return noticeEnemy();

			} else {

				return continueWandering();

			}
		}
		
		protected boolean noticeEnemy(){
			enemySeen = true;
			
			notice();
			alerted = true;
			state = HUNTING;
			target = enemy.pos;
			
			if (alignment == Alignment.ENEMY && Dungeon.isChallenged( Challenges.SWARM_INTELLIGENCE )) {
				for (Mob mob : Dungeon.level.mobs) {
					if (mob.paralysed <= 0
							&& Dungeon.level.distance(pos, mob.pos) <= 8
							&& mob.state != mob.HUNTING) {
						mob.beckon( target );
					}
				}
			}
			
			return true;
		}
		
		protected boolean continueWandering(){
			enemySeen = false;
			
			int oldPos = pos;
			if (target != -1 && getCloser( target )) {
				spend( 1 / speed() );
				return moveSprite( oldPos, pos );
			} else {
				target = randomDestination();
				spend( TICK );
			}
			
			return true;
		}

		protected int randomDestination(){
			return Dungeon.level.randomDestination( Mob.this );
		}
		
	}

	//we keep a list of characters we were recently hit by, so we can switch targets if needed
	protected ArrayList<Char> recentlyAttackedBy = new ArrayList<>();

	protected class Hunting implements AiState {

		public static final String TAG	= "HUNTING";

		//prevents rare infinite loop cases
		private boolean recursing = false;

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			enemySeen = enemyInFOV;
			if (enemyInFOV && !isCharmedBy( enemy ) && canAttack( enemy )) {

				recentlyAttackedBy.clear();
				target = enemy.pos;
				return doAttack( enemy );

			} else {

				//if we cannot attack our target, but were hit by something else that
				// is visible and attackable or closer, swap targets
				if (!recentlyAttackedBy.isEmpty()){
					boolean swapped = false;
					for (Char ch : recentlyAttackedBy){
						if (ch != null && ch.isActive() && Actor.chars().contains(ch) && alignment != ch.alignment && fieldOfView[ch.pos] && ch.invisible == 0 && !isCharmedBy(ch)) {
							if (canAttack(ch) || enemy == null || Dungeon.level.distance(pos, ch.pos) < Dungeon.level.distance(pos, enemy.pos)) {
								enemy = ch;
								target = ch.pos;
								enemyInFOV = true;
								swapped = true;
							}
						}
					}
					recentlyAttackedBy.clear();
					if (swapped){
						return act( enemyInFOV, justAlerted );
					}
				}

				if (enemyInFOV) {
					target = enemy.pos;
				} else if (enemy == null) {
					getSprite().showLost();
					state = WANDERING;
					target = ((Wandering)WANDERING).randomDestination();
					spend( TICK );
					return true;
				}
				
				int oldPos = pos;
				if (target != -1 && getCloser( target )) {
					
					spend( 1 / speed() );
					return moveSprite( oldPos,  pos );

				} else {

					//if moving towards an enemy isn't possible, try to switch targets to another enemy that is closer
					//unless we have already done that and still can't move toward them, then move on.
					if (!recursing) {
						Char oldEnemy = enemy;
						enemy = null;
						enemy = chooseEnemy();
						if (enemy != null && enemy != oldEnemy) {
							recursing = true;
							boolean result = act(enemyInFOV, justAlerted);
							recursing = false;
							return result;
						}
					}

					spend( TICK );
					if (!enemyInFOV) {
						getSprite().showLost();
						state = WANDERING;
						target = ((Wandering)WANDERING).randomDestination();
					}
					return true;
				}
			}
		}
	}

	protected class Fleeing implements AiState {

		public static final String TAG	= "FLEEING";

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			enemySeen = enemyInFOV;
			//triggers escape logic when 0-dist rolls a 6 or greater.
			if (enemy == null || !enemyInFOV && 1 + Random.Int(Dungeon.level.distance(pos, target)) >= 6){
				escaped();
				if (state != FLEEING){
					spend( TICK );
					return true;
				}
			
			//if enemy isn't in FOV, keep running from their previous position.
			} else if (enemyInFOV) {
				target = enemy.pos;
			}

			int oldPos = pos;
			if (target != -1 && getFurther( target )) {

				spend( 1 / speed() );
				return moveSprite( oldPos, pos );

			} else {

				spend( TICK );
				nowhereToRun();

				return true;
			}
		}

		protected void escaped(){
			//does nothing by default, some enemies have special logic for this
		}

		//enemies will turn and fight if they have nowhere to run and aren't affect by terror
		protected void nowhereToRun() {
			if (buff( Terror.class ) == null && buff( Dread.class ) == null) {
				if (enemySeen) {
					getSprite().showStatus(CharSprite.WARNING, Messages.get(Mob.class, "rage"));
					state = HUNTING;
				} else {
					state = WANDERING;
				}
			}
		}
	}

	protected class Passive implements AiState {

		public static final String TAG	= "PASSIVE";

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			enemySeen = enemyInFOV;
			spend( TICK );
			return true;
		}
	}
	
	
	private static final Map<Integer,ArrayList<Mob>> heldAllies = new Hashtable<>();

	public static void holdAlliesForAllHeroes( Level level ) {
		for (Hero hero: Dungeon.heroes) {
			if (hero == null) continue;
			holdAllies(level, hero);
		}
	}
	public static void holdAllies( Level level, Hero hero ) {
		holdAllies(level, hero.pos, hero);
	}

	public static void holdAlliesForAllHeroes( Level level, int pos ) {
		for (Hero hero: Dungeon.heroes) {
			if (hero == null) continue;
			holdAllies(level, pos, hero);
		}
	}

	public static void holdAllies( Level level, int holdFromPos, Hero hero ){
		if (!Mob.heldAllies.containsKey(getHeroID(hero))) {
			Mob.heldAllies.put(getHeroID(hero), new ArrayList<>());
		}
		@NotNull ArrayList<Mob> heldAllies = Mob.heldAllies.get(getHeroID(hero));
		heldAllies.clear();
		for (Mob mob : level.mobs.toArray( new Mob[0] )) {
			//preserve directable allies or empowered intelligent allies no matter where they are
			if (mob instanceof DirectableAlly
				|| (mob.intelligentAlly && PowerOfMany.getPoweredAlly() == mob)) {
				if (mob instanceof DirectableAlly) {
					((DirectableAlly) mob).clearDefensingPos();
				}
				level.mobs.remove( mob );
				heldAllies.add(mob);
				
			//preserve other intelligent allies if they are near the hero
			} else if (mob.alignment == Alignment.ALLY
					&& mob.intelligentAlly
					&& Dungeon.level.distance(holdFromPos, mob.pos) <= 5){
				level.mobs.remove( mob );
				heldAllies.add(mob);
			}
		}
	}

	public static void restoreAllies( Level level, int pos, Hero hero ){
		restoreAllies(level, pos, -1, hero);
	}

	public static void restoreAllies( Level level, int pos, int gravitatePos, Hero hero ){
		if (!Mob.heldAllies.containsKey(getHeroID(hero))){
			Mob.heldAllies.put(getHeroID(hero), new ArrayList<>());
		}
		@NotNull ArrayList<Mob> heldAllies = Mob.heldAllies.get(getHeroID(hero));
		if ((heldAllies!= null) && !heldAllies.isEmpty()){
			
			ArrayList<Integer> candidatePositions = new ArrayList<>();
			for (int i : PathFinder.NEIGHBOURS8) {
				if (!Dungeon.level.solid[i+pos] && !Dungeon.level.avoid[i+pos] && level.findMob(i+pos) == null){
					candidatePositions.add(i+pos);
				}
			}

			//gravitate pos sets a preferred location for allies to be closer to
			if (gravitatePos == -1) {
				Collections.shuffle(candidatePositions);
			} else {
				Collections.sort(candidatePositions, new Comparator<Integer>() {
					@Override
					public int compare(Integer t1, Integer t2) {
						return Dungeon.level.distance(gravitatePos, t1) -
								Dungeon.level.distance(gravitatePos, t2);
					}
				});
			}

			//can only have one empowered ally at once, prioritize incoming ally
			if (Stasis.getStasisAlly(hero) != null){
				for (Mob mob : level.mobs.toArray( new Mob[0] )) {
					if (mob.buff(PowerOfMany.PowerBuff.class) != null){
						mob.buff(PowerOfMany.PowerBuff.class).detach();
					}
				}
			}

			for (Mob ally : heldAllies) {

				//can only have one empowered ally at once, prioritize incoming ally
				if (ally.buff(PowerOfMany.PowerBuff.class) != null){
					for (Mob mob : level.mobs.toArray( new Mob[0] )) {
						if (mob.buff(PowerOfMany.PowerBuff.class) != null){
							mob.buff(PowerOfMany.PowerBuff.class).detach();
						}
					}
				}

				level.mobs.add(ally);
				ally.state = ally.WANDERING;
				
				if (!candidatePositions.isEmpty()){
					ally.pos = candidatePositions.remove(0);
				} else {
					ally.pos = pos;
				}
				if (ally.getSprite() != null) ally.getSprite().place(ally.pos);

				if (ally.fieldOfView == null || ally.fieldOfView.length != level.length()){
					ally.fieldOfView = new boolean[level.length()];
				}
				Dungeon.level.updateFieldOfView( ally, ally.fieldOfView );
				
			}
		}
		heldAllies.clear();
	}
	
	public static void clearHeldAllies(){
		heldAllies.clear();
	}
}

