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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.BodyForm;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.ClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.MindForm;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.SpiritForm;
import com.shatteredpixel.shatteredpixeldungeon.effects.Enchanting;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClothArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ChaliceOfBlood;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.EtherealChains;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.UnstableSpellbook;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFireblast;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WornShortsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.nikita22007.multiplayer.noosa.audio.Sample;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public class Trinity extends ArmorAbility {

	{
		baseChargeUse = 25;
	}

	private Bundlable bodyForm = null;
	private Bundlable mindForm = null;
	private Bundlable spiritForm = null;

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {

		if (bodyForm == null && mindForm == null && spiritForm == null){
			GLog.w(Messages.get(this, "no_imbue"));
		} else {
			GameScene.show(new WndUseTrinity(armor, hero));
		}

	}

	@Override
	public int targetedPos(Char user, int dst) {
		if (mindForm != null){
			return ((Item)mindForm).targetingPos((Hero)user, dst);
		}
		return super.targetedPos(user, dst);
	}

	public class WndUseTrinity extends WndTitledMessage {

		public WndUseTrinity(ClassArmor armor, Hero hero) {
			super(new HeroIcon(Trinity.this),
					Messages.titleCase(Trinity.this.name()),
					Messages.get(WndUseTrinity.class, "text"), hero);

			int top = height;

			if (bodyForm != null){
				RedButton btnBody = null;
				if (bodyForm instanceof Weapon.Enchantment){

					btnBody = new RedButton(Messages.get(WndUseTrinity.class, "body",
							Messages.titleCase(((Weapon.Enchantment)bodyForm).name()))
							+ " " + trinityItemUseText(bodyForm.getClass(), hero), 6){
						@Override
						protected void onClick() {
							if (hero.belongings.weapon() != null &&
									((Weapon)hero.belongings.weapon()).enchantment != null &&
									((Weapon)hero.belongings.weapon()).enchantment.getClass().equals(bodyForm.getClass())){
								GLog.w(Messages.get(Trinity.class, "no_duplicate"));
								hide();
							} else {
								Buff.prolong(hero, BodyForm.BodyFormBuff.class, BodyForm.duration(hero)).setEffect(bodyForm);
								Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
								Weapon w = new WornShortsword();
								if (hero.belongings.weapon() != null) {
									w.image = hero.belongings.weapon().image;
								}
								w.enchant((Weapon.Enchantment) bodyForm);
								Enchanting.show(hero, w);
								hero.getSprite().operate(hero.pos);
								hero.spendAndNext(1f);
								armor.charge -= trinityChargeUsePerEffect(bodyForm.getClass(), hero);
								armor.updateQuickslot();
								Invisibility.dispel();
								hide();
							}
						}
					};
					if (hero.belongings.weapon() != null) {
						btnBody.icon(new ItemSprite(hero.belongings.weapon().image, ((Weapon.Enchantment) bodyForm).glowing()));
					} else {
						btnBody.icon(new ItemSprite(ItemSpriteSheet.WORN_SHORTSWORD, ((Weapon.Enchantment) bodyForm).glowing()));
					}
				} else if (bodyForm instanceof Armor.Glyph){
					btnBody = new RedButton(Messages.get(WndUseTrinity.class, "body",
							Messages.titleCase(((Armor.Glyph)bodyForm).name()))
							+ " " + trinityItemUseText(bodyForm.getClass(), hero), 6){
						@Override
						protected void onClick() {
							if (hero.belongings.armor() != null &&
									hero.belongings.armor().glyph != null &&
									(hero.belongings.armor()).glyph.getClass().equals(bodyForm.getClass())){
								GLog.w(Messages.get(Trinity.class, "no_duplicate"));
								hide();
							} else {
								Buff.prolong(hero, BodyForm.BodyFormBuff.class, BodyForm.duration(hero)).setEffect(bodyForm);
								Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
								Armor a = new ClothArmor();
								if (hero.belongings.armor() != null) {
									a.image = hero.belongings.armor().image;
								}
								a.inscribe((Armor.Glyph) bodyForm);
								Enchanting.show(hero, a);
								hero.getSprite().operate(hero.pos);
								hero.spendAndNext(1f);
								armor.charge -= trinityChargeUsePerEffect(bodyForm.getClass(), hero);
								armor.updateQuickslot();
								Invisibility.dispel();
								hide();
							}
						}
					};
					if (hero.belongings.armor() != null) {
						btnBody.icon(new ItemSprite(hero.belongings.armor().image, ((Armor.Glyph) bodyForm).glowing()));
					} else {
						btnBody.icon(new ItemSprite(ItemSpriteSheet.ARMOR_CLOTH, ((Armor.Glyph) bodyForm).glowing()));
					}
				}
				btnBody.multiline = true;
				btnBody.setSize(width, 100); //for text layout
				btnBody.setRect(0, top + 2, width, btnBody.reqHeight());
				add(btnBody);
				top = (int)btnBody.bottom();

				btnBody.enable(hero.buff(MagicImmune.class) == null && armor.charge >= trinityChargeUsePerEffect(bodyForm.getClass(), hero));
			}

			if (mindForm != null){
				RedButton btnMind = new RedButton(Messages.get(WndUseTrinity.class, "mind",
						Messages.titleCase(((Item)mindForm).name()))
						+ " " + trinityItemUseText(mindForm.getClass(), hero), 6){
					@Override
					protected void onClick() {
						hide();
						MindForm.targetSelector mindEffect = new MindForm.targetSelector();
						mindEffect.setEffect(mindForm);
						GameScene.selectCell(hero, mindEffect);
						Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
						Enchanting.show(hero, (Item)mindForm);
						hero.getSprite().operate(hero.pos);

						if (((Item) mindForm).usesTargeting && Dungeon.quickslot.contains(armor)){
							//TODO: check this, we may want to change quick slot
							//QuickSlotButton.useTargeting(Dungeon.quickslot.getSlot(armor));
						}
					}
				};
				btnMind.icon(new ItemSprite((Item)mindForm));
				btnMind.multiline = true;
				btnMind.setSize(width, 100); //for text layout
				btnMind.setRect(0, top + 2, width, btnMind.reqHeight());
				add(btnMind);
				top = (int)btnMind.bottom();

				btnMind.enable(armor.charge >= trinityChargeUsePerEffect(mindForm.getClass(), hero));
				if (mindForm instanceof Wand && hero.buff(MagicImmune.class) != null){
					btnMind.enable(false);
				}
			}

			if (spiritForm != null){
				RedButton btnSpirit = new RedButton(Messages.get(WndUseTrinity.class, "spirit",
						Messages.titleCase(((Item)spiritForm).name()))
						+ " " + trinityItemUseText(spiritForm.getClass(), hero), 6){
					@Override
					protected void onClick() {
						if ((hero.belongings.ring() != null && hero.belongings.ring().getClass().equals(spiritForm.getClass()))
								|| (hero.belongings.misc() != null && hero.belongings.misc().getClass().equals(spiritForm.getClass()))
								|| (hero.belongings.artifact() != null && hero.belongings.artifact().getClass().equals(spiritForm.getClass()))){
							GLog.w(Messages.get(Trinity.class, "no_duplicate"));
							hide();
							return;
						}
						Invisibility.dispel();
						//Rings and the Chalice specifically get their passive effects for 20 turns
						if (spiritForm instanceof Ring || spiritForm instanceof ChaliceOfBlood) {
							Buff.prolong(hero, SpiritForm.SpiritFormBuff.class, SpiritForm.SpiritFormBuff.DURATION).setEffect(spiritForm);
							hero.spendAndNext(1f);
						} else {
							SpiritForm.applyActiveArtifactEffect(armor, (Artifact) spiritForm, hero);
							//turn spending is handled within the application of the artifact effect
						}
						Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
						Enchanting.show(hero, (Item) spiritForm);
						hero.getSprite().operate(hero.pos);
						armor.charge -= trinityChargeUsePerEffect(spiritForm.getClass(), hero);
						armor.updateQuickslot();
						hide();
					}
				};
				if (spiritForm instanceof Artifact){
					((Artifact) spiritForm).resetForTrinity(SpiritForm.artifactLevel(hero));
				}

				btnSpirit.icon(new ItemSprite((Item)spiritForm));
				btnSpirit.multiline = true;
				btnSpirit.setSize(width, 100); //for text layout
				btnSpirit.setRect(0, top + 2, width, btnSpirit.reqHeight());
				add(btnSpirit);
				top = (int)btnSpirit.bottom();

				btnSpirit.enable(hero.buff(MagicImmune.class) == null && armor.charge >= trinityChargeUsePerEffect(spiritForm.getClass(), hero));
			}

			resize(width, top);

		}

	}

	private static final String BODY = "body_form";
	private static final String MIND = "mind_form";
	private static final String SPIRIT = "spirit_form";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		if (bodyForm != null)   bundle.put(BODY, bodyForm);
		if (mindForm != null)   bundle.put(MIND, mindForm);
		if (spiritForm != null) bundle.put(SPIRIT, spiritForm);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (bundle.contains(BODY))  bodyForm = bundle.get(BODY);
		if (bundle.contains(MIND))  mindForm = bundle.get(MIND);
		if (bundle.contains(SPIRIT))spiritForm = bundle.get(SPIRIT);
	}

	@Override
	public int icon() {
		return HeroIcon.TRINITY;
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.BODY_FORM, Talent.MIND_FORM, Talent.SPIRIT_FORM, Talent.HEROIC_ENERGY};
	}

	public static class WndItemtypeSelect extends WndTitledMessage {

		//probably want a callback here?
		public WndItemtypeSelect(HolyTome tome, ClericSpell spell, Hero hero) {
			super(new HeroIcon(spell), Messages.titleCase(spell.name()), Messages.get(WndItemtypeSelect.class, "text"), hero);

			//start by filtering and sorting
			ArrayList<Class<?>> discoveredClasses = new ArrayList<>();
			if (spell == BodyForm.INSTANCE) {
				for (Class<?> cls : Catalog.ENCHANTMENTS.items()) {
					if (Statistics.itemTypesDiscovered.contains(cls)) {
						discoveredClasses.add(cls);
					}
				}
				for (Class<?> cls : Catalog.GLYPHS.items()) {
					if (Statistics.itemTypesDiscovered.contains(cls)) {
						discoveredClasses.add(cls);
					}
				}
			} else if (spell == MindForm.INSTANCE){
				for (Class<?> cls : Catalog.WANDS.items()) {
					if (Statistics.itemTypesDiscovered.contains(cls)) {
						discoveredClasses.add(cls);
					}
				}
				for (Class<?> cls : Catalog.THROWN_WEAPONS.items()) {
					if (Statistics.itemTypesDiscovered.contains(cls)) {
						discoveredClasses.add(cls);
					}
				}
				for (Class<?> cls : Catalog.TIPPED_DARTS.items()) {
					if (Statistics.itemTypesDiscovered.contains(cls)) {
						discoveredClasses.add(cls);
					}
				}
			} else if (spell == SpiritForm.INSTANCE){
				for (Class<?> cls : Catalog.RINGS.items()) {
					if (Statistics.itemTypesDiscovered.contains(cls)) {
						discoveredClasses.add(cls);
					}
				}
				for (Class<?> cls : Catalog.ARTIFACTS.items()) {
					if (Statistics.itemTypesDiscovered.contains(cls)) {
						discoveredClasses.add(cls);
					}
					//no tome specifically
					discoveredClasses.remove(HolyTome.class);
				}
			}

			ArrayList<Item> options = new ArrayList<>();
			for (Class<?> cls : discoveredClasses){
				if (Weapon.Enchantment.class.isAssignableFrom(cls)){
					MeleeWeapon w = new WornShortsword();
					if (hero.belongings.weapon() != null){
						w.image = hero.belongings.weapon().image;
					}
					w.enchant((Weapon.Enchantment) Reflection.newInstance(cls));
					options.add(w);
				} else if (Armor.Glyph.class.isAssignableFrom(cls)) {
					Armor a = new ClothArmor();
					if (hero.belongings.armor() != null){
						a.image = hero.belongings.armor().image;
					}
					a.inscribe((Armor.Glyph) Reflection.newInstance(cls));
					options.add(a);
				} else {
					options.add((Item) Reflection.newInstance(cls));
				}
			}

			int top = height + 2;
			int left = 0;

			for (Item item : options){
				ItemButton btn = new ItemButton(){
					@Override
					protected void onClick() {
						GameScene.show(new WndItemConfirm(WndItemtypeSelect.this, item, tome, spell, hero));
					}
				};
				btn.item(item);
				btn.slot().textVisible(false);
				btn.setRect(left, top, 19, 19);
				add(btn);

				left += 20;
				if (left >= width - 19){
					top += 20;
					left = 0;
				}
			}

			if (left > 0){
				top += 20;
				left = 0;
			}

			resize(width, top);

		}

	}

	public static class WndItemConfirm extends WndTitledMessage {

		public WndItemConfirm(Window parentWnd, Item item, HolyTome tome, ClericSpell spell, Hero hero){
			super(new ItemSprite(item),  Messages.titleCase(getName(item)), getText(item, hero), hero);

			String text;
			if (spell == BodyForm.INSTANCE){
				text = Messages.get(this, "body");
			} else if (spell == MindForm.INSTANCE){
				text = Messages.get(this, "mind");
			} else {
				text = Messages.get(this, "spirit");
			}

			RedButton btnConfirm = new RedButton(text){
				@Override
				protected void onClick() {
					parentWnd.hide();
					WndItemConfirm.this.hide();

					if (item instanceof MeleeWeapon) {
						((Trinity)hero.armorAbility).bodyForm = ((MeleeWeapon) item).enchantment;
					} else if (item instanceof Armor) {
						((Trinity)hero.armorAbility).bodyForm = ((Armor) item).glyph;
					} else if (item instanceof Wand || item instanceof MissileWeapon){
						((Trinity)hero.armorAbility).mindForm = item;
					} else {
						((Trinity)hero.armorAbility).spiritForm = item;
					}
					spell.onSpellCast(tome, hero);

					hero.getSprite().operate(hero.pos);
					Enchanting.show(hero, item);
					Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
				}
			};
			btnConfirm.setRect(0, height+2, width, 16);
			add(btnConfirm);

			resize(width, (int)btnConfirm.bottom());

		}

		private static String getName(Item item){
			if (item instanceof MeleeWeapon){
				return ((MeleeWeapon) item).enchantment.name();
			} else if (item instanceof Armor){
				return (((Armor) item).glyph.name());
			}
			return item.name();
		}

		private static String getText(Item item, Hero hero){
			if (item instanceof MeleeWeapon){
				return ((MeleeWeapon) item).enchantment.desc() + "\n\n" + trinityItemUseText(((MeleeWeapon) item).enchantment.getClass(), hero);
			} else if (item instanceof Armor){
				return ((Armor) item).glyph.desc() + "\n\n" + trinityItemUseText(((Armor) item).glyph.getClass(), hero);
			} else {
				return item.desc(hero) + "\n\n" + trinityItemUseText(item.getClass(), hero);
			}
		}

	}

	public static String trinityItemUseText(Class<?> cls, Hero hero ){
		float chargeUse = trinityChargeUsePerEffect(cls, hero);
		if (Weapon.Enchantment.class.isAssignableFrom(cls) || Armor.Glyph.class.isAssignableFrom(cls)) {
			for (Class ench : Weapon.Enchantment.rare) {
				if (ench.equals(cls)) {
					return Messages.get(Trinity.class, "rare_ench_glyph_use", BodyForm.duration(hero), Messages.decimalFormat("#.##", chargeUse));
				}
			}
			for (Class glyph : Armor.Glyph.rare){
				if (glyph.equals(cls)){
					return Messages.get(Trinity.class, "rare_ench_glyph_use", BodyForm.duration(hero), Messages.decimalFormat("#.##", chargeUse));
				}
			}
			return Messages.get(Trinity.class, "ench_glyph_use", BodyForm.duration(hero), Messages.decimalFormat("#.##", chargeUse));
		}
		if (MissileWeapon.class.isAssignableFrom(cls)){
			return Messages.get(Trinity.class, "thrown_use", MindForm.itemLevel(hero), Messages.decimalFormat("#.##", chargeUse));
		}
		if (Wand.class.isAssignableFrom(cls)){
			if (cls.equals(WandOfFireblast.class) || cls.equals(WandOfRegrowth.class)){
				return Messages.get(Trinity.class, "wand_multi_use", MindForm.itemLevel(hero), Messages.decimalFormat("#.##", chargeUse));
			}
			return Messages.get(Trinity.class, "wand_use", MindForm.itemLevel(hero), Messages.decimalFormat("#.##", chargeUse));
		}
		if (Ring.class.isAssignableFrom(cls)){
			return Messages.get(Trinity.class, "ring_use", SpiritForm.ringLevel(hero), Messages.decimalFormat("#.##", chargeUse));
		}
		if (Artifact.class.isAssignableFrom(cls)){
			return Messages.get(Trinity.class, cls.getSimpleName() + "_use", SpiritForm.artifactLevel(hero), Messages.decimalFormat("#.##", chargeUse));
		}
		return "error!";

	}

	public static float trinityChargeUsePerEffect(Class<?> cls, Hero hero){
		float chargeUse = hero.armorAbility.chargeUse(hero);
		if (Weapon.Enchantment.class.isAssignableFrom(cls) || Armor.Glyph.class.isAssignableFrom(cls)) {
			for (Class ench : Weapon.Enchantment.rare) {
				if (ench.equals(cls)) {
					return 2*chargeUse; //50 charge
				}
			}
			for (Class glyph : Armor.Glyph.rare){
				if (glyph.equals(cls)){
					return 2*chargeUse; //50 charge
				}
			}
		}
		if (cls.equals(WandOfFireblast.class) || cls.equals(WandOfRegrowth.class)){
			return 2*chargeUse;
		}
		if (Artifact.class.isAssignableFrom(cls)){
			if (cls.equals(DriedRose.class) || cls.equals(UnstableSpellbook.class)){
				return 2*chargeUse; //50 charge
			}
			if (cls.equals(EtherealChains.class) || cls.equals(TalismanOfForesight.class) || cls.equals(TimekeepersHourglass.class)){
				return 1.4f*chargeUse; //35 charge
			}
		}
		//all other effects are standard charge use, 25 at base
		return chargeUse;
	}

}
