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

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.ScrollHolder;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfLullaby;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTerror;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import com.nikita22007.multiplayer.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public class UnstableSpellbook extends Artifact {

	{
		image = ItemSpriteSheet.ARTIFACT_SPELLBOOK;

		levelCap = 10;

		setCharge((int)(level()*0.6f)+2);
		partialCharge = 0;
		chargeCap = (int)(level()*0.6f)+2;

		defaultAction = AC_READ;
	}

	public static final String AC_READ = "READ";
	public static final String AC_ADD = "ADD";

	private final ArrayList<Class> scrolls = new ArrayList<>();

	public UnstableSpellbook() {
		super();

		setupScrolls();
	}

	private void setupScrolls(){
		scrolls.clear();

		Class<?>[] scrollClasses = Generator.Category.SCROLL.classes;
		float[] probs = Generator.Category.SCROLL.defaultProbsTotal.clone(); //array of primitives, clone gives deep copy.
		int i = Random.chances(probs);

		while (i != -1){
			scrolls.add(scrollClasses[i]);
			probs[i] = 0;

			i = Random.chances(probs);
		}
		scrolls.remove(ScrollOfTransmutation.class);
	}

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (isEquipped( hero ) && getCharge() > 0 && !cursed && hero.buff(MagicImmune.class) == null) {
			actions.add(AC_READ);
		}
		if (isEquipped( hero ) && level() < levelCap && !cursed && hero.buff(MagicImmune.class) == null) {
			actions.add(AC_ADD);
		}
		return actions;
	}

	@Override
	public void execute( Hero hero, String action ) {

		super.execute( hero, action );

		if (hero.buff(MagicImmune.class) != null) return;

		if (action.equals( AC_READ )) {

			if (hero.buff( Blindness.class ) != null) GLog.w( Messages.get(this, "blinded") );
			else if (!isEquipped( hero ))             GLog.i( Messages.get(Artifact.class, "need_to_equip") );
			else if (getCharge() <= 0)                     GLog.i( Messages.get(this, "no_charge") );
			else if (cursed)                          GLog.i( Messages.get(this, "cursed") );
			else {
				doReadEffect(hero);
			}

		} else if (action.equals( AC_ADD )) {
			GameScene.selectItem(itemSelector, hero);
		}
	}

	public void doReadEffect(Hero hero){
		setCharge(getCharge() - 1, hero);

		Scroll scroll;
		do {
			scroll = (Scroll) Generator.randomUsingDefaults(Generator.Category.SCROLL);
		} while (scroll == null
				//reduce the frequency of these scrolls by half
				||((scroll instanceof ScrollOfIdentify ||
				scroll instanceof ScrollOfRemoveCurse ||
				scroll instanceof ScrollOfMagicMapping) && Random.Int(2) == 0)
				//cannot roll transmutation
				|| (scroll instanceof ScrollOfTransmutation));

		scroll.anonymize();
		curItem = scroll;
		curUser = hero;

		//if there are charges left and the scroll has been given to the book
		if (getCharge() > 0 && !scrolls.contains(scroll.getClass())) {
			final Scroll fScroll = scroll;

			final ExploitHandler handler = Buff.affect(hero, ExploitHandler.class);
			handler.scroll = scroll;

			GameScene.show(new WndOptions(hero, new ItemSprite(this),
					Messages.get(this, "prompt"),
					Messages.get(this, "read_empowered"),
					scroll.trueName(),
					Messages.get(ExoticScroll.regToExo.get(scroll.getClass()), "name")){
				@Override
				protected void onSelect(int index) {
					handler.detach();
					if (index == 1){
						Scroll scroll = Reflection.newInstance(ExoticScroll.regToExo.get(fScroll.getClass()));
						curItem = scroll;
						setCharge(getCharge() - 1, hero);
						scroll.anonymize();
						checkForArtifactProc(curUser, scroll);
						scroll.doRead(getOwnerHero());
						Talent.onArtifactUsed(getOwnerHero());
					} else {
						checkForArtifactProc(curUser, fScroll);
						fScroll.doRead(getOwnerHero());
						Talent.onArtifactUsed(getOwnerHero());
					}
					updateQuickslot();
				}

				@Override
				public void onBackPressed() {
					//do nothing
				}
			});
		} else {
			checkForArtifactProc(curUser, scroll);
			scroll.doRead(hero);
			Talent.onArtifactUsed(hero);
		}

		updateQuickslot();
	}

	private void checkForArtifactProc(Hero user, Scroll scroll){
		//if the base scroll (exotics all match) is an AOE effect, then also trigger illuminate
		if (scroll instanceof ScrollOfLullaby
				|| scroll instanceof ScrollOfRemoveCurse || scroll instanceof ScrollOfTerror) {
			for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
				if (user.fieldOfView[mob.pos]) {
					artifactProc(mob, visiblyUpgraded(), 1, user);
				}
			}
		//except rage, which affects everything even if it isn't visible
		} else if (scroll instanceof ScrollOfRage){
			for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
				artifactProc(mob, visiblyUpgraded(), 1, user);
			}
		}
	}

	//forces the reading of a regular scroll if the player tried to exploit by quitting the game when the menu was up
	public static class ExploitHandler extends Buff {
		{ actPriority = VFX_PRIO; }

		public Scroll scroll;

		@Override
		public boolean act() {
			curUser = (Hero) target;
			curItem = scroll;
			scroll.anonymize();
			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					scroll.doRead((Hero) target);
					Item.updateQuickslot(target, null);
				}
			});
			detach();
			return true;
		}

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put( "scroll", scroll );
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			scroll = (Scroll)bundle.get("scroll");
		}
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new bookRecharge();
	}
	
	@Override
	public void charge(Hero target, float amount) {
		if (getCharge() < chargeCap && !cursed && target.buff(MagicImmune.class) == null){
			partialCharge += 0.1f*amount;
			while (partialCharge >= 1){
				partialCharge--;
				setCharge(getCharge() + 1, target);
			}
			if (getCharge() >= chargeCap){
				partialCharge = 0;
			}
			updateQuickslot();
		}
	}

	@Override
	public Item upgrade(Hero hero) {
		chargeCap = (int)((level()+1)*0.6f)+2;

		//for artifact transmutation.
		while (!scrolls.isEmpty() && scrolls.size() > (levelCap-1-level())) {
			scrolls.remove(0);
		}

		return super.upgrade(hero);
	}

	@Override
	public void resetForTrinity(int visibleLevel) {
		super.resetForTrinity(visibleLevel);
		setupScrolls();
		while (!scrolls.isEmpty() && scrolls.size() > (levelCap-1-level())) {
			scrolls.remove(0);
		}
	}

	@Override
	public String desc(Hero hero) {
		String desc = super.desc(hero);

		if (isEquipped(hero)) {
			if (cursed) {
				desc += "\n\n" + Messages.get(this, "desc_cursed");
			}
			
			if (level() < levelCap && scrolls.size() > 0) {
				desc += "\n\n" + Messages.get(this, "desc_index");
				desc += "\n" + "_" + Messages.get(scrolls.get(0), "name") + "_";
				if (scrolls.size() > 1)
					desc += "\n" + "_" + Messages.get(scrolls.get(1), "name") + "_";
			}
		}
		
		if (level() > 0) {
			desc += "\n\n" + Messages.get(this, "desc_empowered");
		}

		return desc;
	}

	private static final String SCROLLS =   "scrolls";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);
		bundle.put( SCROLLS, scrolls.toArray(new Class[scrolls.size()]) );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);
		scrolls.clear();
		if (bundle.contains(SCROLLS) && bundle.getClassArray(SCROLLS) != null) {
			for (Class<?> scroll : bundle.getClassArray(SCROLLS)) {
				if (scroll != null) scrolls.add(scroll);
			}
		}
	}

	public class bookRecharge extends ArtifactBuff{
		@Override
		public boolean act() {
			if (getCharge() < chargeCap
					&& !cursed
					&& target.buff(MagicImmune.class) == null
					&& Regeneration.regenOn((Hero) target)) {
				//120 turns to charge at full, 80 turns to charge at 0/8
				float chargeGain = 1 / (120f - (chargeCap - getCharge())*5f);
				chargeGain *= RingOfEnergy.artifactChargeMultiplier(target);
				partialCharge += chargeGain;

				while (partialCharge >= 1) {
					partialCharge --;
					setCharge(getCharge() + 1, (Hero) target);

					if (getCharge() == chargeCap){
						partialCharge = 0;
					}
				}
			}

			updateQuickslot();

			spend( TICK );

			return true;
		}
	}

	protected WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {

		@Override
		public String textPrompt() {
			return Messages.get(UnstableSpellbook.class, "prompt");
		}

		@Override
		public Class<?extends Bag> preferredBag(){
			return ScrollHolder.class;
		}

		@Override
		public boolean itemSelectable(Item item) {
			return item instanceof Scroll && item.isIdentified() && scrolls.contains(item.getClass());
		}

		@Override
		public void onSelect(Item item) {
			if (item != null && item instanceof Scroll && item.isIdentified()){
				Hero hero = getOwner();
				for (int i = 0; ( i <= 1 && i < scrolls.size() ); i++){
					if (scrolls.get(i).equals(item.getClass())){
						hero.getSprite().operate( hero.pos );
						hero.busy();
						hero.spend( 2f );
						Sample.INSTANCE.play(Assets.Sounds.BURNING);
						hero.getSprite().emitter().burst( ElmoParticle.FACTORY, 12 );

						scrolls.remove(i);
						item.detach(hero.belongings.backpack);

						upgrade();
						Catalog.countUse(UnstableSpellbook.class);
						GLog.i( Messages.get(UnstableSpellbook.class, "infuse_scroll") );
						return;
					}
				}
				GLog.w( Messages.get(UnstableSpellbook.class, "unable_scroll") );
			} else if (item instanceof Scroll && !item.isIdentified()) {
				GLog.w( Messages.get(UnstableSpellbook.class, "unknown_scroll") );
			}
		}
	};
}
