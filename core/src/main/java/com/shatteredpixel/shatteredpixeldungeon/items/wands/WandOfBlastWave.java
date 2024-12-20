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

package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Effects;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Elastic;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.TenguDartTrap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.nikita22007.multiplayer.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class WandOfBlastWave extends DamageWand {

	{
		image = ItemSpriteSheet.WAND_BLAST_WAVE;

		collisionProperties = Ballistica.PROJECTILE;
	}

	public int min(int lvl){
		return 1+lvl;
	}

	public int max(int lvl){
		return 3+3*lvl;
	}

	@Override
	public void onZap(Ballistica bolt, Hero hero) {
		Sample.INSTANCE.play( Assets.Sounds.BLAST );
		BlastWave.blast(bolt.collisionPos, hero);

		//presses all tiles in the AOE first, with the exception of tengu dart traps
		for (int i : PathFinder.NEIGHBOURS9){
			if (!(Dungeon.level.traps.get(bolt.collisionPos+i) instanceof TenguDartTrap)) {
				Dungeon.level.pressCell(bolt.collisionPos + i);
			}
		}

		//throws other chars around the center.
		for (int i  : PathFinder.NEIGHBOURS8){
			Char ch = Actor.findChar(bolt.collisionPos + i);

			if (ch != null){
				wandProc(ch, chargesPerCast(), hero);
				if (ch.alignment != Char.Alignment.ALLY) ch.damage(damageRoll(hero), new Char.DamageCause(this, curUser));

				if (ch.pos == bolt.collisionPos + i) {
					Ballistica trajectory = new Ballistica(ch.pos, ch.pos + i, Ballistica.MAGIC_BOLT);
					int strength = 1 + Math.round(buffedLvl() / 2f);
					throwChar(ch, trajectory, strength, false, true, this);
				}

			}
		}

		//throws the char at the center of the blast
		Char ch = Actor.findChar(bolt.collisionPos);
		if (ch != null){
			wandProc(ch, chargesPerCast(), hero);
			ch.damage(damageRoll(hero), new Char.DamageCause(this, curUser));

			if (bolt.path.size() > bolt.dist+1 && ch.pos == bolt.collisionPos) {
				Ballistica trajectory = new Ballistica(ch.pos, bolt.path.get(bolt.dist + 1), Ballistica.MAGIC_BOLT);
				int strength = buffedLvl() + 3;
				throwChar(ch, trajectory, strength, false, true, this);
			}
		}
		
	}

	public static void throwChar(final Char ch, final Ballistica trajectory, int power,
	                             boolean closeDoors, boolean collideDmg, Object cause){
		if (ch.properties().contains(Char.Property.BOSS)) {
			power = (power+1)/2;
		}

		int dist = Math.min(trajectory.dist, power);

		boolean collided = dist == trajectory.dist;

		if (dist <= 0
				|| ch.rooted
				|| ch.properties().contains(Char.Property.IMMOVABLE)) return;

		//large characters cannot be moved into non-open space
		if (Char.hasProp(ch, Char.Property.LARGE)) {
			for (int i = 1; i <= dist; i++) {
				if (!Dungeon.level.openSpace[trajectory.path.get(i)]){
					dist = i-1;
					collided = true;
					break;
				}
			}
		}

		if (Actor.findChar(trajectory.path.get(dist)) != null){
			dist--;
			collided = true;
		}

		if (dist < 0) return;

		final int newPos = trajectory.path.get(dist);

		if (newPos == ch.pos) return;

		final int finalDist = dist;
		final boolean finalCollided = collided && collideDmg;
		final int initialpos = ch.pos;

		Actor.add(new Pushing(ch, ch.pos, newPos, new Callback() {
			public void call() {
				if (initialpos != ch.pos || Actor.findChar(newPos) != null) {
					//something caused movement or added chars before pushing resolved, cancel to be safe.
					ch.getSprite().place(ch.pos);
					return;
				}
				int oldPos = ch.pos;
				ch.pos = newPos;
				if (finalCollided && ch.isActive()) {
					final Char.DamageCause damageCause;
					if (cause instanceof Char){
						damageCause = new Char.DamageCause(new Knockback(), (Char)cause);
					} else if (cause instanceof Char.DamageCause){
						damageCause = new Char.DamageCause(new Knockback(), ((Char.DamageCause)cause).getDamageOwner());
					} else {
						damageCause = new Char.DamageCause(new Knockback(), null);
					}
					ch.damage(Random.NormalIntRange(finalDist, 2*finalDist), damageCause);
					if (ch.isActive()) {
						Paralysis.prolong(ch, Paralysis.class, 1 + finalDist/2f);
					} else if (ch instanceof Hero){
						if (cause instanceof WandOfBlastWave){
							Badges.validateDeathFromFriendlyMagic();
						}
						Dungeon.fail(cause);
					}
				}
				if (closeDoors && Dungeon.level.map[oldPos] == Terrain.OPEN_DOOR){
					Door.leave(oldPos);
				}
				Dungeon.level.occupyCell(ch);
				if (ch instanceof Hero){
					Dungeon.observe((Hero) ch);
					GameScene.updateFog();
				}
			}
		}));
	}

	public static class Knockback{}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {

		Talent.EmpoweredStrikeTracker tracker = attacker.buff(Talent.EmpoweredStrikeTracker.class);

		if (tracker != null){
			tracker.delayedDetach = true;
		}

		//acts like elastic enchantment
		//we delay this with an actor to prevent conflicts with regular elastic
		//so elastic always fully resolves first, then this effect activates
		Actor.add(new Actor() {
			{
				actPriority = VFX_PRIO+9; //act after pushing effects
			}

			@Override
			protected boolean act() {
				Actor.remove(this);
				if (defender.isAlive()) {
					new BlastWaveOnHit().proc(staff, attacker, defender, damage);
				}
				if (tracker != null) tracker.detach();
				return true;
			}
		});
	}

	private static class BlastWaveOnHit extends Elastic{
		@Override
		protected float procChanceMultiplier(Char attacker) {
			return Wand.procChanceMultiplier(attacker);
		}
	}

	@Override
	public String upgradeStat2(int level) {
		return Integer.toString(3 + level);
	}

	@Override
	public void fx(Ballistica bolt, Callback callback) {
		MagicMissile.boltFromChar( curUser.getSprite().parent,
				MagicMissile.FORCE,
                curUser.getSprite(),
				bolt.collisionPos,
				callback);
		Sample.INSTANCE.play(Assets.Sounds.ZAP);
	}

	@Override
	public void staffFx(MagesStaff.StaffParticle particle) {
		particle.color( 0x664422 ); particle.am = 0.6f;
		particle.setLifespan(3f);
		particle.speed.polar(Random.Float(PointF.PI2), 0.3f);
		particle.setSize( 1f, 2f);
		particle.radiateXY(2.5f);
	}

	public static class BlastWave extends Image {

		private static final float TIME_TO_FADE = 0.2f;

		private float time;
		private float size;

		public BlastWave(){
			super(Effects.get(Effects.Type.RIPPLE));
			origin.set(width / 2, height / 2);
		}

		public void reset(int pos, float size) {
			revive();

			x = (pos % Dungeon.level.width()) * DungeonTilemap.SIZE + (DungeonTilemap.SIZE - width) / 2;
			y = (pos / Dungeon.level.width()) * DungeonTilemap.SIZE + (DungeonTilemap.SIZE - height) / 2;

			time = TIME_TO_FADE;
			this.size = size;
		}

		@Override
		public void update() {
			super.update();

			if ((time -= Game.elapsed) <= 0) {
				kill();
			} else {
				float p = time / TIME_TO_FADE;
				alpha(p);
				scale.y = scale.x = (1-p)*size;
			}
		}

		//FIXME
		public static void blast(int pos, Char user) {
			blast(pos, 3, user);
		}

		public static void blast(int pos, float radius, Char user) {
			Group parent = user.getSprite().parent;
			BlastWave b = (BlastWave) parent.recycle(BlastWave.class);
			parent.bringToFront(b);
			b.reset(pos, radius);
		}

	}
}
